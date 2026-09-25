package com.example.data.repository

import android.content.Context
import com.example.data.firebase.FirebaseAuthManager
import com.example.data.firebase.FirebaseFirestoreManager
import com.example.data.model.ChatGroup
import com.example.data.model.ChatMessage
import com.example.data.model.CollegeInfo
import com.example.data.model.Comment
import com.example.data.model.ConnectionStatus
import com.example.data.model.Event
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LostFoundStatus
import com.example.data.model.LostFoundType
import com.example.data.model.MessageType
import com.example.data.model.NotificationConfig
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Poll
import com.example.data.model.PollOption
import com.example.data.model.StudentProfile
import com.example.data.model.VerificationMethod
import com.example.data.model.VerificationStatus
import com.example.ui.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

object MgugRealtimeRepository {

  private val scope = CoroutineScope(Dispatchers.Default)
  private var appContext: Context? = null

  fun setContext(context: Context) {
    appContext = context.applicationContext
    FirebaseAuthManager.initialize(context)
    FirebaseFirestoreManager.initialize(context)

    FirebaseAuthManager.setOnUserSignedInCallback { firebaseUser ->
      val email = firebaseUser.email ?: "chauhansunil3410@gmail.com"
      val name = firebaseUser.displayName?.ifEmpty { null }
        ?: email.substringBefore("@").replace(".", " ").split(" ")
          .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
      val photoUrl = firebaseUser.photoUrl?.toString()
      val userId = "google_${firebaseUser.uid}"

      val googleProfile = StudentProfile(
        id = userId,
        name = name,
        email = email,
        rollNo = "CC/2026/042",
        collegeId = "col_dtu",
        collegeName = "Delhi Technological University",
        universityName = "Delhi Technological University",
        department = "Computer Science & Engineering",
        faculty = "Faculty of Technology",
        course = "B.Tech Computer Science & Engg",
        year = "2nd Year",
        age = 20,
        gender = "Student",
        avatarColorHex = 0xFF651FFF,
        avatarUrl = photoUrl,
        isVerified = true,
        verificationStatus = VerificationStatus.VERIFIED,
        verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
        bio = "Student building and collaborating across campuses on Campus Connect.",
        statusEmoji = "⚡",
        statusText = "Campus Live · Student",
        connectionPreferences = listOf("Friendship", "Study Partner", "Project Partner"),
        interests = listOf("Coding", "Android", "Campus Events", "Innovation"),
        skills = listOf("Kotlin", "Jetpack Compose", "Git"),
        graduationYear = "2026",
        isGoogleUser = true
      )

      _currentUser.value = googleProfile
      _allProfiles.update { list ->
        if (list.any { it.id == googleProfile.id }) {
          list.map { if (it.id == googleProfile.id) googleProfile else it }
        } else {
          listOf(googleProfile) + list
        }
      }
      FirebaseFirestoreManager.saveUserProfile(googleProfile)
      _eventStream.tryEmit("🎉 Logged in with Gmail: ${googleProfile.email}")
    }

    // Attempt silent auto-sign in
    FirebaseAuthManager.silentSignInWithGoogle(context)

    // Listen to real-time Cloud Firestore updates
    FirebaseFirestoreManager.listenToFeedItems { remoteItems ->
      if (remoteItems.isNotEmpty()) {
        _feedItems.update { current ->
          val remoteMap = remoteItems.associateBy { it.id }
          val merged = remoteItems + current.filter { !remoteMap.containsKey(it.id) }
          merged.sortedByDescending { it.timestamp }
        }
      }
    }

    FirebaseFirestoreManager.listenToPolls { remotePolls ->
      if (remotePolls.isNotEmpty()) {
        _polls.update { current ->
          val remoteMap = remotePolls.associateBy { it.id }
          val merged = remotePolls + current.filter { !remoteMap.containsKey(it.id) }
          merged.sortedByDescending { it.createdAt }
        }
      }
    }

    FirebaseFirestoreManager.listenToEvents { remoteEvents ->
      if (remoteEvents.isNotEmpty()) {
        _events.update { current ->
          val remoteMap = remoteEvents.associateBy { it.id }
          remoteEvents + current.filter { !remoteMap.containsKey(it.id) }
        }
      }
    }

    FirebaseFirestoreManager.listenToChatMessages { remoteMsgs ->
      if (remoteMsgs.isNotEmpty()) {
        _chatMessages.update { current ->
          val remoteMap = remoteMsgs.associateBy { it.id }
          val merged = current.filter { !remoteMap.containsKey(it.id) } + remoteMsgs
          merged.sortedBy { it.timestamp }
        }
      }
    }
  }

  fun loginWithGmail(email: String = "chauhansunil3410@gmail.com", name: String = "Sunil Chauhan") {
    val cleanEmail = email.trim().lowercase()
    val cleanName = name.trim().ifEmpty {
      cleanEmail.substringBefore("@").replace(".", " ").split(" ")
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
    }
    val userId = "google_" + cleanEmail.replace("@", "_").replace(".", "_")

    val googleProfile = StudentProfile(
      id = userId,
      name = cleanName,
      email = cleanEmail,
      rollNo = "CC/2026/042",
      collegeId = "col_dtu",
      collegeName = "Delhi Technological University",
      universityName = "Delhi Technological University",
      department = "Computer Science & Engineering",
      faculty = "Faculty of Technology",
      course = "B.Tech Computer Science & Engg",
      year = "2nd Year",
      age = 20,
      gender = "Student",
      avatarColorHex = 0xFF651FFF,
      isVerified = true,
      verificationStatus = VerificationStatus.VERIFIED,
      verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
      bio = "Student building and collaborating across campuses on Campus Connect.",
      statusEmoji = "⚡",
      statusText = "Campus Live · Student",
      connectionPreferences = listOf("Friendship", "Study Partner", "Project Partner"),
      interests = listOf("Coding", "Android Dev", "AI & ML", "Campus Life"),
      skills = listOf("Kotlin", "Jetpack Compose", "Git"),
      graduationYear = "2026",
      isGoogleUser = true
    )

    _currentUser.value = googleProfile
    _allProfiles.update { list ->
      if (list.any { it.id == googleProfile.id }) {
        list.map { if (it.id == googleProfile.id) googleProfile else it }
      } else {
        listOf(googleProfile) + list
      }
    }
    FirebaseFirestoreManager.saveUserProfile(googleProfile)
    _eventStream.tryEmit("🎉 Logged in with Gmail: $cleanEmail")
  }

  // Supported institutions for cross-college discovery
  val SUPPORTED_COLLEGES: List<CollegeInfo> = listOf(
    CollegeInfo("col_dtu", "Delhi Technological University", "Delhi Technological University", "New Delhi", "Delhi", 14500),
    CollegeInfo("col_bits", "BITS Pilani", "Birla Institute of Technology and Science", "Pilani", "Rajasthan", 11200),
    CollegeInfo("col_iitr", "Indian Institute of Technology Roorkee", "IIT Roorkee", "Roorkee", "Uttarakhand", 9800),
    CollegeInfo("col_vit", "Vellore Institute of Technology", "VIT University", "Vellore", "Tamil Nadu", 28000),
    CollegeInfo("col_mu", "University of Mumbai", "Mumbai University", "Mumbai", "Maharashtra", 35000),
    CollegeInfo("col_mgug", "Mahayogi Gorakhnath University", "Mahayogi Gorakhnath University", "Gorakhpur", "Uttar Pradesh", 8500),
    CollegeInfo("col_du", "University of Delhi", "Delhi University", "New Delhi", "Delhi", 42000),
    CollegeInfo("col_anna", "Anna University", "Anna University", "Chennai", "Tamil Nadu", 24000)
  )

  // Pre-defined multi-college students for cross-campus simulation & discovery
  val STUDENT_AARAV = StudentProfile(
    id = "user_aarav",
    name = "Aarav Sharma",
    email = "aarav.cse@dtu.ac.in",
    rollNo = "DTU/2023/CSE/042",
    collegeId = "col_dtu",
    collegeName = "Delhi Technological University",
    universityName = "Delhi Technological University",
    department = "Computer Science & Engineering",
    faculty = "Faculty of Technology",
    course = "B.Tech Computer Science & Engg",
    year = "3rd Year",
    age = 21,
    gender = "Male",
    avatarColorHex = 0xFFFF5722, // Vibrant Neon Coral
    isVerified = true,
    verificationStatus = VerificationStatus.VERIFIED,
    verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
    bio = "Building Android apps with Kotlin & Compose. Hackathon runner-up. Lowkey obsessed with neural nets & chai ☕",
    statusEmoji = "⚡",
    statusText = "Shipping apps & solving DSA",
    instagramHandle = "aarav_dtu",
    snapchatHandle = "aarav_builds",
    githubHandle = "aarav-codes",
    connectionPreferences = listOf("Study Partner", "Project Partner", "Friendship"),
    interests = listOf("Android Dev", "AI/ML", "Hackathons", "Cricket", "Valorant"),
    skills = listOf("Kotlin", "Jetpack Compose", "Python", "Git", "System Design"),
    graduationYear = "2026"
  )

  val STUDENT_PRIYA = StudentProfile(
    id = "user_priya",
    name = "Priya Verma",
    email = "priya.pharm@pilani.bits.ac.in",
    rollNo = "BITS/2024/PHARM/019",
    collegeId = "col_bits",
    collegeName = "BITS Pilani",
    universityName = "Birla Institute of Technology and Science",
    department = "Biological Sciences & Pharmacy",
    faculty = "Faculty of Pharmaceutical Sciences",
    course = "B.Pharm (Pharmacology)",
    year = "2nd Year",
    age = 20,
    gender = "Female",
    avatarColorHex = 0xFF00BFA5, // Vibrant Cyber Teal
    isVerified = true,
    verificationStatus = VerificationStatus.VERIFIED,
    verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
    bio = "Medicinal Chemistry & Drug Formulation nerd 🔬 Always at Central Library. Down for study revision squads!",
    statusEmoji = "📚",
    statusText = "Library grinding for Sem 3",
    instagramHandle = "priya_bits",
    snapchatHandle = "priya_pharma",
    githubHandle = null,
    connectionPreferences = listOf("Study Partner", "Common Interests", "Friendship"),
    interests = listOf("Medicinal Chem", "Herbal Drugs", "Debating", "Badminton", "Podcasts"),
    skills = listOf("Lab Analysis", "Organic Synthesis", "Scientific Writing"),
    graduationYear = "2027"
  )

  val STUDENT_ROHAN = StudentProfile(
    id = "user_rohan",
    name = "Rohan Gupta",
    email = "rohan.nursing@mgug.ac.in",
    rollNo = "MGUG/FON/2022/108",
    collegeId = "col_mgug",
    collegeName = "Mahayogi Gorakhnath University",
    universityName = "Mahayogi Gorakhnath University",
    department = "Faculty of Nursing",
    faculty = "Faculty of Nursing",
    course = "B.Sc Nursing",
    year = "4th Year",
    age = 22,
    gender = "Male",
    avatarColorHex = 0xFF7C4DFF, // Electric Purple
    isVerified = true,
    verificationStatus = VerificationStatus.VERIFIED,
    verificationMethod = VerificationMethod.STUDENT_ID_CARD_UPLOAD,
    bio = "Clinical intern at University Hospital ER 🩺 Emergency response enthusiast. Loves campus photography 📸",
    statusEmoji = "🏥",
    statusText = "Hospital clinical rotations",
    instagramHandle = "rohan_clicks",
    snapchatHandle = "rohan_er",
    githubHandle = null,
    connectionPreferences = listOf("Project Partner", "Friendship"),
    interests = listOf("Clinical Care", "First Aid", "Photography", "Music", "Fitness"),
    skills = listOf("Patient Care", "BLS Certified", "Clinical Triage"),
    graduationYear = "2025"
  )

  val STUDENT_ANANYA = StudentProfile(
    id = "user_ananya",
    name = "Ananya Singh",
    email = "ananya.botany@du.ac.in",
    rollNo = "DU/2023/BOT/007",
    collegeId = "col_du",
    collegeName = "University of Delhi",
    universityName = "Delhi University",
    department = "Botany & Ayurvedic Sciences",
    faculty = "Faculty of Science",
    course = "BAMS (Ayurveda Medicine)",
    year = "3rd Year",
    age = 21,
    gender = "Female",
    avatarColorHex = 0xFFFFB300, // Sunburst Gold
    isVerified = true,
    verificationStatus = VerificationStatus.VERIFIED,
    verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
    bio = "Integrating traditional botany with modern biochemistry. Yoga trainer & cultural fest coordinator ✨",
    statusEmoji = "🌿",
    statusText = "Curating herbal taxonomy",
    instagramHandle = "ananya_du",
    snapchatHandle = "ananya_singh",
    githubHandle = null,
    connectionPreferences = listOf("Friendship", "Common Interests", "Study Partner"),
    interests = listOf("Ayurvedic Herbs", "Yoga", "Classical Music", "Literature", "Hiking"),
    skills = listOf("Herbal Formulation", "Scientific Botany", "Event Coordination"),
    graduationYear = "2026"
  )

  val STUDENT_KAVYA = StudentProfile(
    id = "user_kavya",
    name = "Kavya Iyer",
    email = "kavya.ai@vit.ac.in",
    rollNo = "VIT/2024/SCOPE/112",
    collegeId = "col_vit",
    collegeName = "Vellore Institute of Technology",
    universityName = "VIT University",
    department = "School of Computer Science",
    faculty = "Faculty of Computing",
    course = "B.Tech AI & Data Science",
    year = "2nd Year",
    age = 20,
    gender = "Female",
    avatarColorHex = 0xFF00B0FF, // Vivid Cyan
    isVerified = true,
    verificationStatus = VerificationStatus.VERIFIED,
    verificationMethod = VerificationMethod.COLLEGE_EMAIL_OTP,
    bio = "Generative AI researcher, hackathon finalist. Looking for teammates to build real-time multi-agent systems!",
    statusEmoji = "🤖",
    statusText = "Training LLM adapters",
    instagramHandle = "kavya_ai_vit",
    snapchatHandle = "kavya_codes",
    githubHandle = "kavya-ai",
    connectionPreferences = listOf("Project Partner", "Hackathon Squad", "Friendship"),
    interests = listOf("AI/ML", "NLP", "Robotics", "Anime", "Chess"),
    skills = listOf("Python", "PyTorch", "FastAPI", "React", "Docker"),
    graduationYear = "2027"
  )

  // Seed list of all profiles
  private val _allProfiles = MutableStateFlow<List<StudentProfile>>(
    listOf(STUDENT_AARAV, STUDENT_PRIYA, STUDENT_ROHAN, STUDENT_ANANYA, STUDENT_KAVYA)
  )
  val allProfiles: StateFlow<List<StudentProfile>> = _allProfiles.asStateFlow()

  val ALL_PROFILES: List<StudentProfile>
    get() = _allProfiles.value

  // Reports and Feedback collection
  private val _reports = MutableStateFlow<List<com.example.data.model.ReportItem>>(emptyList())
  val reports: StateFlow<List<com.example.data.model.ReportItem>> = _reports.asStateFlow()

  private val _feedbacks = MutableStateFlow<List<com.example.data.model.FeedbackItem>>(emptyList())
  val feedbacks: StateFlow<List<com.example.data.model.FeedbackItem>> = _feedbacks.asStateFlow()

  // Current active user
  private val _currentUser = MutableStateFlow(STUDENT_AARAV)
  val currentUser: StateFlow<StudentProfile> = _currentUser.asStateFlow()

  // Real-time connection status
  private val _connectionStatus = MutableStateFlow(ConnectionStatus.CONNECTED)
  val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

  // Real-time Event broadcast bus
  private val _eventStream = MutableSharedFlow<String>(extraBufferCapacity = 64)
  val eventStream: SharedFlow<String> = _eventStream.asSharedFlow()

  // Typing indicators: conversationKey (e.g., peerId or groupId) -> Set of userIds currently typing
  private val _typingStatus = MutableStateFlow<Map<String, Set<String>>>(emptyMap())
  val typingStatus: StateFlow<Map<String, Set<String>>> = _typingStatus.asStateFlow()

  // Dynamic Home Feed Items
  private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
  val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()

  // Active Polls
  private val _polls = MutableStateFlow<List<Poll>>(emptyList())
  val polls: StateFlow<List<Poll>> = _polls.asStateFlow()

  // Events
  private val _events = MutableStateFlow<List<Event>>(emptyList())
  val events: StateFlow<List<Event>> = _events.asStateFlow()

  // Comments per feed item (feedItemId -> List<Comment>)
  private val _comments = MutableStateFlow<Map<String, List<Comment>>>(emptyMap())
  val comments: StateFlow<Map<String, List<Comment>>> = _comments.asStateFlow()

  // Chat Groups
  private val _chatGroups = MutableStateFlow<List<ChatGroup>>(emptyList())
  val chatGroups: StateFlow<List<ChatGroup>> = _chatGroups.asStateFlow()

  // Chat Messages (both Direct 1-on-1 and Group chats)
  private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
  val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

  // Notifications
  private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
  val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

  // User Notification Settings Configuration
  private val _notificationConfig = MutableStateFlow(NotificationConfig())
  val notificationConfig: StateFlow<NotificationConfig> = _notificationConfig.asStateFlow()

  init {
    seedInitialCampusData()
  }

  // ==========================================
  // NOTIFICATION CONFIGURATION & PREFERENCES
  // ==========================================

  fun updateNotificationConfig(config: NotificationConfig) {
    _notificationConfig.value = config
    _eventStream.tryEmit("Notification preferences saved")
  }

  fun dismissNotification(id: String) {
    _notifications.update { list -> list.filter { it.id != id } }
  }

  fun clearAllNotifications() {
    val currentUserId = _currentUser.value.id
    _notifications.update { list -> list.filter { it.userId != currentUserId } }
    _eventStream.tryEmit("All alerts dismissed")
  }

  fun markNotificationsRead() {
    val currentUserId = _currentUser.value.id
    _notifications.update { list ->
      list.map { if (it.userId == currentUserId) it.copy(isRead = true) else it }
    }
  }

  // ==========================================
  // USER PROFILE & VERIFICATION SYSTEM
  // ==========================================

  fun updateProfile(
    name: String,
    course: String,
    year: String,
    bio: String,
    statusEmoji: String,
    statusText: String,
    interests: List<String>,
    skills: List<String>,
    avatarColorHex: Long? = null,
    collegeName: String? = null,
    department: String? = null,
    age: Int? = null,
    gender: String? = null,
    instagramHandle: String? = null,
    snapchatHandle: String? = null,
    connectionPreferences: List<String>? = null
  ) {
    val current = _currentUser.value
    val updated = current.copy(
      name = name.trim().ifEmpty { current.name },
      course = course.trim().ifEmpty { current.course },
      year = year.trim().ifEmpty { current.year },
      bio = bio.trim().ifEmpty { current.bio },
      statusEmoji = statusEmoji,
      statusText = statusText,
      interests = interests,
      skills = skills,
      avatarColorHex = avatarColorHex ?: current.avatarColorHex,
      collegeName = collegeName?.trim()?.ifEmpty { current.collegeName } ?: current.collegeName,
      department = department?.trim()?.ifEmpty { current.department } ?: current.department,
      age = age ?: current.age,
      gender = gender?.trim()?.ifEmpty { current.gender } ?: current.gender,
      instagramHandle = instagramHandle?.trim()?.ifEmpty { null } ?: current.instagramHandle,
      snapchatHandle = snapchatHandle?.trim()?.ifEmpty { null } ?: current.snapchatHandle,
      connectionPreferences = connectionPreferences ?: current.connectionPreferences
    )
    _currentUser.value = updated
    _allProfiles.update { list -> list.map { if (it.id == updated.id) updated else it } }
    FirebaseFirestoreManager.saveUserProfile(updated)
    _eventStream.tryEmit("Profile updated successfully ✨")
  }

  fun updateUserCollege(college: CollegeInfo) {
    val current = _currentUser.value
    val updated = current.copy(
      collegeId = college.id,
      collegeName = college.name,
      universityName = college.universityName
    )
    _currentUser.value = updated
    _allProfiles.update { list -> list.map { if (it.id == updated.id) updated else it } }
    FirebaseFirestoreManager.saveUserProfile(updated)
    _eventStream.tryEmit("Switched campus to ${college.name} 🎓")
  }

  fun connectWithPeer(targetUserId: String): Boolean {
    val current = _currentUser.value
    if (current.freeSuggestionsRemaining > 0) {
      val updated = current.copy(freeSuggestionsRemaining = current.freeSuggestionsRemaining - 1)
      _currentUser.value = updated
      _allProfiles.update { list -> list.map { if (it.id == updated.id) updated else it } }
    }
    val targetUser = _allProfiles.value.find { it.id == targetUserId }
    val targetName = targetUser?.name ?: "Student"
    _eventStream.tryEmit("Sent connection request to $targetName 🤝")
    
    // Add notification
    val newNotification = NotificationItem(
      id = "notif_${System.currentTimeMillis()}",
      userId = current.id,
      title = "Connection Request Sent",
      message = "You connected with $targetName from ${targetUser?.collegeName ?: "Campus"}.",
      timestamp = System.currentTimeMillis(),
      type = NotificationType.PARTNER_REQUEST,
      targetId = targetUserId,
      senderName = targetName,
      senderAvatarHex = targetUser?.avatarColorHex ?: 0xFF651FFF
    )
    _notifications.update { listOf(newNotification) + it }
    return true
  }

  fun purchaseExtraSuggestions() {
    val current = _currentUser.value
    val updated = current.copy(freeSuggestionsRemaining = current.freeSuggestionsRemaining + 10)
    _currentUser.value = updated
    _allProfiles.update { list -> list.map { if (it.id == updated.id) updated else it } }
    _eventStream.tryEmit("Added 10 extra connects with Campus Connect Pass! 🚀")
  }

  fun submitReport(
    targetType: com.example.data.model.ReportTargetType,
    targetId: String,
    reason: String
  ) {
    val current = _currentUser.value
    val report = com.example.data.model.ReportItem(
      id = "rep_${System.currentTimeMillis()}",
      reporterId = current.id,
      reporterName = current.name,
      targetType = targetType,
      targetId = targetId,
      reason = reason,
      timestamp = System.currentTimeMillis(),
      status = "PENDING"
    )
    _reports.update { listOf(report) + it }
    _eventStream.tryEmit("Report submitted. Campus moderators will review it promptly.")
  }

  fun submitFeedback(subject: String, message: String) {
    val current = _currentUser.value
    val feedback = com.example.data.model.FeedbackItem(
      id = "fb_${System.currentTimeMillis()}",
      userId = current.id,
      userName = current.name,
      userCollege = current.collegeName,
      subject = subject,
      message = message,
      timestamp = System.currentTimeMillis()
    )
    _feedbacks.update { listOf(feedback) + it }
    _eventStream.tryEmit("Feedback sent! Thank you for supporting Campus Connect.")
  }

  fun getAdminStats(): com.example.data.model.AdminStats {
    val profiles = _allProfiles.value
    val posts = _feedItems.value
    val activePolls = _polls.value.count { it.isActive }
    val activeEvents = _events.value.size
    val partnerCount = posts.count { it.type == FeedItemType.STUDY_PARTNER || it.type == FeedItemType.PROJECT_PARTNER }
    val lostFoundCount = posts.count { it.type == FeedItemType.LOST_FOUND }
    val collegeMap = profiles.groupBy { it.collegeName }.mapValues { it.value.size }
    val genderMap = profiles.groupBy { it.gender }.mapValues { it.value.size }

    return com.example.data.model.AdminStats(
      totalUsers = profiles.size,
      usersByCollege = collegeMap,
      usersByGender = genderMap,
      activePosts = posts.size,
      activePolls = activePolls,
      activeEvents = activeEvents,
      partnerRequests = partnerCount,
      lostFoundItems = lostFoundCount,
      totalReports = _reports.value.size,
      totalFeedback = _feedbacks.value.size
    )
  }

  fun clearSpamReports() {
    _reports.value = emptyList()
    _eventStream.tryEmit("All pending moderation reports handled & resolved ✓")
  }

  fun submitVerificationRequest(
    method: VerificationMethod,
    inputData: String // either student college email or ID card document note
  ): Boolean {
    val current = _currentUser.value
    val updated = current.copy(
      isVerified = true,
      verificationStatus = VerificationStatus.VERIFIED,
      verificationMethod = method,
      verifiedAt = System.currentTimeMillis()
    )
    _currentUser.value = updated
    _allProfiles.update { list -> list.map { if (it.id == updated.id) updated else it } }
    FirebaseFirestoreManager.saveUserProfile(updated)
    _eventStream.tryEmit("🎉 Student ID verified! You are officially confirmed as a verified student.")

    deliverNotification(
      recipientId = current.id,
      title = "Verification Confirmed 🛡️",
      message = "Your MGUG student status is verified via ${if (method == VerificationMethod.COLLEGE_EMAIL_OTP) "MGUG Edu Email" else "Smart ID Card"}.",
      type = NotificationType.CAMPUS_NOTICE
    )
    return true
  }

  fun switchUser(profile: StudentProfile) {
    _currentUser.value = profile
    _eventStream.tryEmit("Switched active student to ${profile.name}")
  }

  fun logOut() {
    _currentUser.value = STUDENT_AARAV
    _eventStream.tryEmit("Logged out securely")
  }

  // ==========================================
  // REAL-TIME DIRECT & GROUP MESSAGING
  // ==========================================

  fun createGroupChat(
    name: String,
    description: String,
    category: String,
    iconEmoji: String,
    initialMemberIds: Set<String>
  ): ChatGroup {
    val creator = _currentUser.value
    val allMembers = initialMemberIds + creator.id
    val group = ChatGroup(
      id = "group_${UUID.randomUUID().toString().take(8)}",
      name = name,
      description = description,
      iconEmoji = iconEmoji,
      creatorId = creator.id,
      memberIds = allMembers,
      createdAt = System.currentTimeMillis(),
      lastMessageText = "Group created by ${creator.name}",
      lastMessageTimestamp = System.currentTimeMillis(),
      category = category
    )
    _chatGroups.update { listOf(group) + it }
    _eventStream.tryEmit("Created group: '$name' 🔥")

    // Welcome system message
    val welcomeMsg = ChatMessage(
      id = "msg_${UUID.randomUUID()}",
      conversationId = group.id,
      senderId = "system",
      senderName = "MGUG Campus Bot",
      receiverId = null,
      content = "Welcome to #${group.name}! Let's connect, share notes, and collaborate respectfully 🚀",
      timestamp = System.currentTimeMillis(),
      isRead = true,
      readBy = setOf(creator.id)
    )
    _chatMessages.update { it + welcomeMsg }

    // Notify other members
    allMembers.filter { it != creator.id }.forEach { memberId ->
      deliverNotification(
        recipientId = memberId,
        title = "Added to Group: ${group.name}",
        message = "${creator.name} added you to '${group.name}'",
        type = NotificationType.CHAT_MESSAGE,
        targetId = group.id,
        senderName = creator.name,
        senderAvatarHex = creator.avatarColorHex
      )
    }

    return group
  }

  fun sendDirectMessage(receiverId: String, content: String) {
    val sender = _currentUser.value
    val msg = ChatMessage(
      id = "msg_${UUID.randomUUID()}",
      conversationId = getDirectConversationId(sender.id, receiverId),
      senderId = sender.id,
      senderName = sender.name,
      receiverId = receiverId,
      content = content,
      timestamp = System.currentTimeMillis(),
      isRead = false,
      readBy = setOf(sender.id)
    )
    _chatMessages.update { it + msg }
    FirebaseFirestoreManager.saveChatMessage(msg)
    _eventStream.tryEmit("Sent message to ${getProfileById(receiverId)?.name?.split(" ")?.first() ?: "peer"}")

    // Clear typing indicator for current user
    setTyping(receiverId, false)

    // Send real-time notification to recipient
    deliverNotification(
      recipientId = receiverId,
      title = "New message from ${sender.name}",
      message = content,
      type = NotificationType.CHAT_MESSAGE,
      targetId = sender.id,
      senderName = sender.name,
      senderAvatarHex = sender.avatarColorHex
    )
  }

  fun sendGroupMessage(groupId: String, content: String) {
    val sender = _currentUser.value
    val group = _chatGroups.value.firstOrNull { it.id == groupId }
    val msg = ChatMessage(
      id = "msg_${UUID.randomUUID()}",
      conversationId = groupId,
      senderId = sender.id,
      senderName = sender.name,
      receiverId = null,
      content = content,
      timestamp = System.currentTimeMillis(),
      isRead = false,
      readBy = setOf(sender.id)
    )
    _chatMessages.update { it + msg }
    FirebaseFirestoreManager.saveChatMessage(msg)
    _chatGroups.update { groups ->
      groups.map {
        if (it.id == groupId) it.copy(lastMessageText = "${sender.name}: $content", lastMessageTimestamp = System.currentTimeMillis())
        else it
      }
    }
    _eventStream.tryEmit("Sent message to #${group?.name ?: "group"}")

    setTyping(groupId, false)

    // Notify other group members
    group?.memberIds?.filter { it != sender.id }?.forEach { memberId ->
      deliverNotification(
        recipientId = memberId,
        title = "#${group.name}: ${sender.name}",
        message = content,
        type = NotificationType.CHAT_MESSAGE,
        targetId = groupId,
        senderName = sender.name,
        senderAvatarHex = sender.avatarColorHex
      )
    }
  }

  fun markConversationAsRead(conversationId: String) {
    val currentUserId = _currentUser.value.id
    _chatMessages.update { messages ->
      messages.map { msg ->
        if (msg.conversationId == conversationId && !msg.readBy.contains(currentUserId)) {
          msg.copy(
            isRead = true,
            readBy = msg.readBy + currentUserId
          )
        } else msg
      }
    }
  }

  fun setTyping(targetKey: String, isTyping: Boolean) {
    val currentUserId = _currentUser.value.id
    _typingStatus.update { current ->
      val currentTypers = current[targetKey] ?: emptySet()
      val updated = if (isTyping) currentTypers + currentUserId else currentTypers - currentUserId
      if (updated.isEmpty()) current - targetKey else current + (targetKey to updated)
    }
  }

  fun addMessageReaction(messageId: String, emoji: String) {
    val currentUserId = _currentUser.value.id
    _chatMessages.update { messages ->
      messages.map { msg ->
        if (msg.id == messageId) {
          val updatedReactions = msg.reactions + (currentUserId to emoji)
          msg.copy(reactions = updatedReactions)
        } else msg
      }
    }
  }

  fun getDirectConversation(userA: String, userB: String): List<ChatMessage> {
    val convId = getDirectConversationId(userA, userB)
    return _chatMessages.value.filter { it.conversationId == convId }.sortedBy { it.timestamp }
  }

  fun getGroupConversation(groupId: String): List<ChatMessage> {
    return _chatMessages.value.filter { it.conversationId == groupId }.sortedBy { it.timestamp }
  }

  fun getDirectConversationId(userA: String, userB: String): String {
    return if (userA < userB) "dm_${userA}_$userB" else "dm_${userB}_$userA"
  }

  fun getProfileById(userId: String): StudentProfile? {
    return _allProfiles.value.firstOrNull { it.id == userId }
  }

  // ==========================================
  // REAL-TIME NOTIFICATION DISPATCHER
  // ==========================================

  private fun deliverNotification(
    recipientId: String,
    title: String,
    message: String,
    type: NotificationType,
    targetId: String? = null,
    senderName: String? = null,
    senderAvatarHex: Long? = null
  ) {
    val config = _notificationConfig.value

    // Check user preference filters
    if (!config.pushNotificationsEnabled) return
    val allowed = when (type) {
      NotificationType.CHAT_MESSAGE -> config.chatMessages
      NotificationType.POST_REPLY -> config.postReplies
      NotificationType.UPCOMING_EVENT -> config.upcomingEvents
      NotificationType.NEW_POLL -> config.newPolls
      NotificationType.CAMPUS_NOTICE -> config.universityNotices
      NotificationType.PARTNER_REQUEST -> true
      NotificationType.POST_LIKE -> true
    }
    if (!allowed) return

    val item = NotificationItem(
      id = "notif_${UUID.randomUUID()}",
      userId = recipientId,
      title = title,
      message = message,
      timestamp = System.currentTimeMillis(),
      type = type,
      isRead = false,
      targetId = targetId,
      senderName = senderName,
      senderAvatarHex = senderAvatarHex
    )
    _notifications.update { listOf(item) + it }

    // If it's for current user and app context is set, trigger native Android Notification
    if (recipientId == _currentUser.value.id && appContext != null) {
      appContext?.let { ctx ->
        NotificationHelper.triggerSystemNotification(
          context = ctx,
          title = title,
          message = message,
          type = type
        )
      }
    }
  }

  private fun broadcastCampusNotification(
    title: String,
    message: String,
    type: NotificationType,
    targetId: String? = null
  ) {
    val currentUserId = _currentUser.value.id
    ALL_PROFILES.filter { it.id != currentUserId }.forEach { profile ->
      deliverNotification(
        recipientId = profile.id,
        title = title,
        message = message,
        type = type,
        targetId = targetId
      )
    }
  }

  // ==========================================
  // REAL-TIME FEED, POSTS, POLLS, EVENTS
  // ==========================================

  fun createGeneralPost(content: String) {
    val user = _currentUser.value
    val newPost = FeedItem(
      id = "post_${UUID.randomUUID()}",
      authorId = user.id,
      authorName = user.name,
      authorFaculty = user.faculty,
      authorYear = user.year,
      authorColorHex = user.avatarColorHex,
      timestamp = System.currentTimeMillis(),
      content = content,
      type = FeedItemType.GENERAL,
      likes = emptySet(),
      commentsCount = 0
    )
    _feedItems.update { listOf(newPost) + it }
    FirebaseFirestoreManager.saveFeedItem(newPost)
    _eventStream.tryEmit("${user.name} shared a campus thought 💬")
  }

  fun createStudyPartnerPost(
    subject: String,
    targetExam: String,
    studyHours: String,
    studyLocation: String,
    note: String
  ) {
    val user = _currentUser.value
    val newPost = FeedItem(
      id = "post_${UUID.randomUUID()}",
      authorId = user.id,
      authorName = user.name,
      authorFaculty = user.faculty,
      authorYear = user.year,
      authorColorHex = user.avatarColorHex,
      timestamp = System.currentTimeMillis(),
      content = note.ifBlank { "Looking for study buddy for $subject" },
      type = FeedItemType.STUDY_PARTNER,
      subject = subject,
      targetExam = targetExam,
      studyHours = studyHours,
      studyLocation = studyLocation,
      likes = emptySet(),
      commentsCount = 0
    )
    _feedItems.update { listOf(newPost) + it }
    FirebaseFirestoreManager.saveFeedItem(newPost)
    _eventStream.tryEmit("Study partner alert for $subject 📚")
  }

  fun createProjectPartnerPost(
    projectTitle: String,
    techStack: String,
    rolesNeeded: String,
    deadline: String,
    note: String
  ) {
    val user = _currentUser.value
    val newPost = FeedItem(
      id = "post_${UUID.randomUUID()}",
      authorId = user.id,
      authorName = user.name,
      authorFaculty = user.faculty,
      authorYear = user.year,
      authorColorHex = user.avatarColorHex,
      timestamp = System.currentTimeMillis(),
      content = note.ifBlank { "Teammates wanted for $projectTitle" },
      type = FeedItemType.PROJECT_PARTNER,
      projectTitle = projectTitle,
      techStack = techStack,
      rolesNeeded = rolesNeeded,
      deadline = deadline,
      likes = emptySet(),
      commentsCount = 0
    )
    _feedItems.update { listOf(newPost) + it }
    FirebaseFirestoreManager.saveFeedItem(newPost)
    _eventStream.tryEmit("Project teammate desk: $projectTitle 💻")
  }

  fun createLostFoundPost(
    type: LostFoundType,
    itemName: String,
    description: String,
    category: String,
    campusLocation: String,
    itemDate: String,
    photoUrl: String? = null
  ): String {
    val user = _currentUser.value
    val tag = if (type == LostFoundType.LOST) "[LOST]" else "[FOUND]"
    val cleanContent = if (itemName.isNotBlank()) "[$itemName] $description" else "$tag $description"
    val postId = "post_${UUID.randomUUID()}"
    val newPost = FeedItem(
      id = postId,
      authorId = user.id,
      authorName = user.name,
      authorFaculty = user.faculty,
      authorYear = user.year,
      authorColorHex = user.avatarColorHex,
      collegeName = user.collegeName,
      timestamp = System.currentTimeMillis(),
      content = cleanContent,
      type = FeedItemType.LOST_FOUND,
      lostFoundType = type,
      lostFoundStatus = LostFoundStatus.ACTIVE,
      lostFoundCategory = category,
      itemName = itemName,
      itemDate = itemDate.ifBlank { "Reported just now" },
      itemImageUrl = photoUrl,
      campusLocation = campusLocation,
      contactInfo = "In-App Connect",
      likes = emptySet(),
      commentsCount = 0,
      isResolved = false
    )
    _feedItems.update { listOf(newPost) + it }
    FirebaseFirestoreManager.saveFeedItem(newPost)
    _eventStream.tryEmit("${if (type == LostFoundType.LOST) "Lost" else "Found"} item posted: $itemName 🔍")
    return postId
  }

  fun createLostFoundPost(
    type: LostFoundType,
    campusLocation: String,
    contactInfo: String,
    note: String
  ): String {
    return createLostFoundPost(
      type = type,
      itemName = note.take(30),
      description = note,
      category = "📦 Other",
      campusLocation = campusLocation,
      itemDate = "Today",
      photoUrl = null
    )
  }

  fun markLostFoundStatus(postId: String, newStatus: LostFoundStatus): Boolean {
    val currentUserId = _currentUser.value.id
    val target = _feedItems.value.firstOrNull { it.id == postId }
    if (target == null) return false

    val isAdmin = _currentUser.value.email.contains("admin", ignoreCase = true) ||
                  _currentUser.value.rollNo.contains("ADMIN", ignoreCase = true)

    if (target.authorId != currentUserId && !isAdmin) {
      _eventStream.tryEmit("Permission denied: Only the original uploader or admin can update item status 🚫")
      return false
    }

    val isResolvedBool = (newStatus == LostFoundStatus.FOUND_RETURNED || newStatus == LostFoundStatus.CLOSED)
    _feedItems.update { list ->
      list.map {
        if (it.id == postId) {
          it.copy(
            lostFoundStatus = newStatus,
            isResolved = isResolvedBool
          )
        } else it
      }
    }
    val updatedPost = _feedItems.value.firstOrNull { it.id == postId }
    if (updatedPost != null) {
      FirebaseFirestoreManager.saveFeedItem(updatedPost)
    }

    val label = when (newStatus) {
      LostFoundStatus.ACTIVE -> "Active"
      LostFoundStatus.FOUND_RETURNED -> "Found / Returned 🎉"
      LostFoundStatus.CLOSED -> "Closed"
    }
    _eventStream.tryEmit("Item status changed to: $label")
    return true
  }

  fun markLostFoundResolved(postId: String): Boolean {
    return markLostFoundStatus(postId, LostFoundStatus.FOUND_RETURNED)
  }

  fun updateLostFoundPost(
    postId: String,
    itemName: String,
    description: String,
    category: String,
    campusLocation: String,
    itemDate: String,
    type: LostFoundType,
    status: LostFoundStatus
  ): Boolean {
    val currentUserId = _currentUser.value.id
    val target = _feedItems.value.firstOrNull { it.id == postId }
    if (target == null) return false

    val isAdmin = _currentUser.value.email.contains("admin", ignoreCase = true) ||
                  _currentUser.value.rollNo.contains("ADMIN", ignoreCase = true)

    if (target.authorId != currentUserId && !isAdmin) {
      _eventStream.tryEmit("Permission denied: Only the uploader can edit this post 🚫")
      return false
    }

    val isResolvedBool = (status == LostFoundStatus.FOUND_RETURNED || status == LostFoundStatus.CLOSED)
    val tag = if (type == LostFoundType.LOST) "[LOST]" else "[FOUND]"
    val cleanContent = if (itemName.isNotBlank()) "[$itemName] $description" else "$tag $description"

    _feedItems.update { list ->
      list.map {
        if (it.id == postId) {
          it.copy(
            content = cleanContent,
            itemName = itemName,
            lostFoundCategory = category,
            campusLocation = campusLocation,
            itemDate = itemDate,
            lostFoundType = type,
            lostFoundStatus = status,
            isResolved = isResolvedBool
          )
        } else it
      }
    }

    val updatedPost = _feedItems.value.firstOrNull { it.id == postId }
    if (updatedPost != null) {
      FirebaseFirestoreManager.saveFeedItem(updatedPost)
    }
    _eventStream.tryEmit("Lost & Found post updated ✨")
    return true
  }

  fun toggleLike(postId: String) {
    val userId = _currentUser.value.id
    val user = _currentUser.value
    var postAuthorId: String? = null
    var isNowLiked = false

    _feedItems.update { list ->
      list.map { item ->
        if (item.id == postId) {
          postAuthorId = item.authorId
          val isLiked = item.likes.contains(userId)
          isNowLiked = !isLiked
          val updatedLikes = if (isLiked) item.likes - userId else item.likes + userId
          FirebaseFirestoreManager.toggleFeedItemLike(postId, updatedLikes)
          item.copy(likes = updatedLikes)
        } else item
      }
    }

    if (isNowLiked && postAuthorId != null && postAuthorId != userId) {
      deliverNotification(
        recipientId = postAuthorId!!,
        title = "Post Liked ❤️",
        message = "${user.name} liked your campus post",
        type = NotificationType.POST_LIKE,
        targetId = postId,
        senderName = user.name,
        senderAvatarHex = user.avatarColorHex
      )
    }
  }

  fun deletePost(postId: String): Boolean {
    val currentUserId = _currentUser.value.id
    val target = _feedItems.value.firstOrNull { it.id == postId }
    if (target == null) return false

    val isAdmin = _currentUser.value.email.contains("admin", ignoreCase = true) ||
                  _currentUser.value.rollNo.contains("ADMIN", ignoreCase = true)

    if (target.authorId != currentUserId && !isAdmin) {
      _eventStream.tryEmit("Permission denied: Only the original uploader or admin can delete this post 🚫")
      return false
    }

    _feedItems.update { list -> list.filter { it.id != postId } }
    FirebaseFirestoreManager.deleteFeedItem(postId)
    _eventStream.tryEmit("Post deleted permanently 🗑️")
    return true
  }

  fun reportPost(postId: String, reason: String) {
    val userId = _currentUser.value.id
    _feedItems.update { list ->
      list.map {
        if (it.id == postId) it.copy(reportedBy = it.reportedBy + userId) else it
      }
    }
    _eventStream.tryEmit("Report filed: Moderator team alerted 🛡️")
  }

  fun submitFeedback(type: String, message: String, hasScreenshot: Boolean) {
    _eventStream.tryEmit("Feedback submitted ($type) – thank you! 💬")
  }

  fun submitContactRequest(subject: String, category: String, message: String, attachment: String?) {
    _eventStream.tryEmit("Support ticket logged: $subject 📩")
  }

  // ==========================================
  // REAL-TIME POLLS
  // ==========================================

  fun votePoll(pollId: String, optionIndex: Int) {
    val userId = _currentUser.value.id
    _polls.update { list ->
      list.map { poll ->
        if (poll.id == pollId) {
          if (poll.hasVoted(userId)) return@map poll
          val updatedOptions = poll.options.mapIndexed { idx, opt ->
            if (idx == optionIndex) {
              opt.copy(voteCount = opt.voteCount + 1, voterIds = opt.voterIds + userId)
            } else opt
          }
          poll.copy(options = updatedOptions)
        } else poll
      }
    }
    val updatedPoll = _polls.value.firstOrNull { it.id == pollId }
    if (updatedPoll != null) {
      FirebaseFirestoreManager.savePoll(updatedPoll)
    }
    _eventStream.tryEmit("Vote casted live! Poll totals updated.")
  }

  fun createPoll(question: String, options: List<String>, category: String = "Campus Life") {
    val user = _currentUser.value
    val validOptions = options.filter { it.isNotBlank() }
    if (validOptions.size < 2) return

    val newPoll = Poll(
      id = "poll_${UUID.randomUUID()}",
      question = question,
      options = validOptions.mapIndexed { idx, txt ->
        PollOption(id = "opt_$idx", text = txt, voteCount = 0)
      },
      creatorId = user.id,
      creatorName = user.name,
      creatorFaculty = user.faculty,
      createdAt = System.currentTimeMillis(),
      expiresAt = System.currentTimeMillis() + 86400000L * 7,
      category = category
    )
    _polls.update { listOf(newPoll) + it }
    FirebaseFirestoreManager.savePoll(newPoll)
    _eventStream.tryEmit("Poll created: '${question.take(28)}...' 📊")

    broadcastCampusNotification(
      title = "New Campus Poll 🗳️",
      message = question,
      type = NotificationType.NEW_POLL,
      targetId = newPoll.id
    )
  }

  // ==========================================
  // REAL-TIME EVENTS
  // ==========================================

  fun createEvent(
    title: String,
    description: String,
    category: String,
    date: String,
    time: String,
    venue: String,
    organizer: String,
    contactInfo: String
  ) {
    val user = _currentUser.value
    val newEvent = Event(
      id = "event_${UUID.randomUUID()}",
      title = title,
      description = description,
      category = category,
      date = date,
      time = time,
      venue = venue,
      organizer = organizer.ifBlank { user.faculty },
      contactInfo = contactInfo.ifBlank { user.email },
      registeredUserIds = setOf(user.id),
      isUpcoming = true,
      bannerGradientIndex = (0..3).random()
    )
    _events.update { listOf(newEvent) + it }
    FirebaseFirestoreManager.saveEvent(newEvent)
    _eventStream.tryEmit("New MGUG event scheduled: $title 📅")

    broadcastCampusNotification(
      title = "Upcoming Campus Event: $title",
      message = "$title at $venue on $date",
      type = NotificationType.UPCOMING_EVENT,
      targetId = newEvent.id
    )
  }

  fun toggleEventRsvp(eventId: String) {
    val userId = _currentUser.value.id
    var registered = false
    var eventTitle = ""

    _events.update { list ->
      list.map { event ->
        if (event.id == eventId) {
          eventTitle = event.title
          val isRegistered = event.registeredUserIds.contains(userId)
          registered = !isRegistered
          val updated = if (isRegistered) {
            event.registeredUserIds - userId
          } else {
            event.registeredUserIds + userId
          }
          event.copy(registeredUserIds = updated)
        } else event
      }
    }

    val updatedEvent = _events.value.firstOrNull { it.id == eventId }
    if (updatedEvent != null) {
      FirebaseFirestoreManager.saveEvent(updatedEvent)
    }

    if (registered) {
      _eventStream.tryEmit("RSVP Confirmed for $eventTitle! You'll receive event reminders 🎉")
    }
  }

  // ==========================================
  // REAL-TIME COMMENTS
  // ==========================================

  fun addComment(feedItemId: String, content: String) {
    val user = _currentUser.value
    val comment = Comment(
      id = "comment_${UUID.randomUUID()}",
      feedItemId = feedItemId,
      authorId = user.id,
      authorName = user.name,
      authorFaculty = user.faculty,
      authorColorHex = user.avatarColorHex,
      content = content,
      timestamp = System.currentTimeMillis()
    )
    _comments.update { map ->
      val existing = map[feedItemId] ?: emptyList()
      map + (feedItemId to (existing + comment))
    }
    var postAuthorId: String? = null
    _feedItems.update { list ->
      list.map {
        if (it.id == feedItemId) {
          postAuthorId = it.authorId
          it.copy(commentsCount = it.commentsCount + 1)
        } else it
      }
    }
    _eventStream.tryEmit("${user.name} commented in real time 💬")

    if (postAuthorId != null && postAuthorId != user.id) {
      deliverNotification(
        recipientId = postAuthorId!!,
        title = "Reply on your post 💬",
        message = "${user.name}: \"${content.take(45)}\"",
        type = NotificationType.POST_REPLY,
        targetId = feedItemId,
        senderName = user.name,
        senderAvatarHex = user.avatarColorHex
      )
    }
  }

  fun getCommentsForPost(feedItemId: String): List<Comment> {
    return _comments.value[feedItemId] ?: emptyList()
  }

  // ==========================================
  // NETWORK & PEER SIMULATION
  // ==========================================

  fun toggleNetworkSimulation() {
    val current = _connectionStatus.value
    if (current == ConnectionStatus.CONNECTED) {
      _connectionStatus.value = ConnectionStatus.OFFLINE
      _eventStream.tryEmit("Device disconnected: Offline mode")
    } else {
      _connectionStatus.value = ConnectionStatus.CONNECTING
      scope.launch {
        delay(1000)
        _connectionStatus.value = ConnectionStatus.CONNECTED
        _eventStream.tryEmit("Reconnected to MGUG Realtime Engine! State synced.")
      }
    }
  }

  fun simulatePeerActivity() {
    val current = _currentUser.value
    val peer = ALL_PROFILES.firstOrNull { it.id != current.id } ?: STUDENT_PRIYA
    scope.launch {
      val actions = listOf("message", "typing", "poll_vote", "comment")
      when (actions.random()) {
        "message" -> {
          // Send a direct message from peer to current user
          val sampleDMs = listOf(
            "Hey ${current.name.split(" ").first()}! Are you attending the Campus Hackathon briefing tomorrow?",
            "Just checked the DSA notes you posted, super clean! 🔥",
            "Are you free for group revision at Central Library after 4 PM?",
            "Did you vote on the hostel Wi-Fi upgrade poll yet? The count is crazy!"
          )
          val dmText = sampleDMs.random()
          // First show typing for 1.5 seconds
          setTyping(current.id, true)
          delay(1500)
          setTyping(current.id, false)
          val msg = ChatMessage(
            id = "msg_${UUID.randomUUID()}",
            conversationId = getDirectConversationId(peer.id, current.id),
            senderId = peer.id,
            senderName = peer.name,
            receiverId = current.id,
            content = dmText,
            timestamp = System.currentTimeMillis(),
            isRead = false,
            readBy = setOf(peer.id)
          )
          _chatMessages.update { it + msg }
          _eventStream.tryEmit("⚡ New message from ${peer.name}!")
          deliverNotification(
            recipientId = current.id,
            title = "New message from ${peer.name}",
            message = dmText,
            type = NotificationType.CHAT_MESSAGE,
            targetId = peer.id,
            senderName = peer.name,
            senderAvatarHex = peer.avatarColorHex
          )
        }
        "typing" -> {
          setTyping(current.id, true)
          _eventStream.tryEmit("⚡ ${peer.name} is typing a message...")
          delay(3000)
          setTyping(current.id, false)
        }
        "poll_vote" -> {
          val activePoll = _polls.value.firstOrNull { it.isActive }
          if (activePoll != null && !activePoll.hasVoted(peer.id)) {
            val optIdx = (0 until activePoll.options.size).random()
            val updatedOptions = activePoll.options.mapIndexed { idx, opt ->
              if (idx == optIdx) opt.copy(voteCount = opt.voteCount + 1, voterIds = opt.voterIds + peer.id)
              else opt
            }
            _polls.update { list ->
              list.map { if (it.id == activePoll.id) it.copy(options = updatedOptions) else it }
            }
            _eventStream.tryEmit("⚡ Realtime: ${peer.name} voted on '${activePoll.question.take(22)}...'")
          }
        }
        "comment" -> {
          val post = _feedItems.value.firstOrNull()
          if (post != null) {
            val commentTexts = listOf("Count me in!", "Super helpful info, thanks!", "Let's connect on this 🙌")
            val cText = commentTexts.random()
            val c = Comment(
              id = "c_${UUID.randomUUID()}",
              feedItemId = post.id,
              authorId = peer.id,
              authorName = peer.name,
              authorFaculty = peer.faculty,
              authorColorHex = peer.avatarColorHex,
              content = cText,
              timestamp = System.currentTimeMillis()
            )
            _comments.update { map ->
              val cur = map[post.id] ?: emptyList()
              map + (post.id to (cur + c))
            }
            _feedItems.update { list ->
              list.map { if (it.id == post.id) it.copy(commentsCount = it.commentsCount + 1) else it }
            }
            _eventStream.tryEmit("⚡ ${peer.name} commented on feed: \"$cText\"")
            if (post.authorId == current.id) {
              deliverNotification(
                recipientId = current.id,
                title = "Reply on your post 💬",
                message = "${peer.name}: \"$cText\"",
                type = NotificationType.POST_REPLY,
                targetId = post.id,
                senderName = peer.name,
                senderAvatarHex = peer.avatarColorHex
              )
            }
          }
        }
      }
    }
  }

  // ==========================================
  // SEED CAMPUS DATA
  // ==========================================

  private fun seedInitialCampusData() {
    // 1. Initial Groups
    val groupTech = ChatGroup(
      id = "group_tech_hackers",
      name = "MGUG Devs & Hackers",
      description = "Building open-source, mobile apps, Web3 & AI projects for Gorakhnath Tech Fest 2026",
      iconEmoji = "⚡",
      creatorId = STUDENT_AARAV.id,
      memberIds = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id, STUDENT_ROHAN.id, STUDENT_ANANYA.id),
      createdAt = System.currentTimeMillis() - 86400000L * 3,
      lastMessageText = "Aarav: Hackathon submission portal is open!",
      lastMessageTimestamp = System.currentTimeMillis() - 3600000L * 2,
      category = "Tech & Innovation"
    )

    val groupPharma = ChatGroup(
      id = "group_pharma_squad",
      name = "Pharma & Med Revisions",
      description = "Drug formulation research, medicinal chem flashcards, and group study",
      iconEmoji = "🔬",
      creatorId = STUDENT_PRIYA.id,
      memberIds = setOf(STUDENT_PRIYA.id, STUDENT_ANANYA.id, STUDENT_AARAV.id),
      createdAt = System.currentTimeMillis() - 86400000L * 2,
      lastMessageText = "Priya: Sharing pharmacology chapter 4 summary pdf tonight",
      lastMessageTimestamp = System.currentTimeMillis() - 3600000L * 5,
      category = "Academic Squads"
    )

    val groupCampus = ChatGroup(
      id = "group_mgug_vibes",
      name = "MGUG Campus Lounge",
      description = "General student community chat, campus queries, hostel life, cafeteria food reviews",
      iconEmoji = "☕",
      creatorId = STUDENT_ROHAN.id,
      memberIds = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id, STUDENT_ROHAN.id, STUDENT_ANANYA.id),
      createdAt = System.currentTimeMillis() - 86400000L * 5,
      lastMessageText = "Rohan: Anyone going to the basketball court after 5:30?",
      lastMessageTimestamp = System.currentTimeMillis() - 1800000L,
      category = "Campus Life"
    )

    _chatGroups.value = listOf(groupTech, groupPharma, groupCampus)

    // 2. Chat Messages
    val msg1 = ChatMessage(
      id = "msg_seed_1",
      conversationId = groupTech.id,
      senderId = STUDENT_AARAV.id,
      senderName = STUDENT_AARAV.name,
      receiverId = null,
      content = "Hey everyone! We formed the official MGUG Devs squad for the upcoming AI Hackathon 🚀",
      timestamp = System.currentTimeMillis() - 3600000L * 4,
      isRead = true,
      readBy = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id, STUDENT_ROHAN.id)
    )
    val msg2 = ChatMessage(
      id = "msg_seed_2",
      conversationId = groupTech.id,
      senderId = STUDENT_PRIYA.id,
      senderName = STUDENT_PRIYA.name,
      receiverId = null,
      content = "Love this! I can help with UI research and presenting the healthcare track demo.",
      timestamp = System.currentTimeMillis() - 3600000L * 3,
      isRead = true,
      readBy = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id)
    )
    val msg3 = ChatMessage(
      id = "msg_seed_3",
      conversationId = groupTech.id,
      senderId = STUDENT_AARAV.id,
      senderName = STUDENT_AARAV.name,
      receiverId = null,
      content = "Hackathon submission portal is open! Let's lock in our repo by Friday.",
      timestamp = System.currentTimeMillis() - 3600000L * 2,
      isRead = true,
      readBy = setOf(STUDENT_AARAV.id)
    )

    // Direct Chat between Aarav & Priya
    val dmConvId = getDirectConversationId(STUDENT_AARAV.id, STUDENT_PRIYA.id)
    val dm1 = ChatMessage(
      id = "dm_seed_1",
      conversationId = dmConvId,
      senderId = STUDENT_PRIYA.id,
      senderName = STUDENT_PRIYA.name,
      receiverId = STUDENT_AARAV.id,
      content = "Hi Aarav! Are you free to review the data structures assignment questions after lunch?",
      timestamp = System.currentTimeMillis() - 3600000L * 1,
      isRead = true,
      readBy = setOf(STUDENT_PRIYA.id, STUDENT_AARAV.id)
    )
    val dm2 = ChatMessage(
      id = "dm_seed_2",
      conversationId = dmConvId,
      senderId = STUDENT_AARAV.id,
      senderName = STUDENT_AARAV.name,
      receiverId = STUDENT_PRIYA.id,
      content = "Hey Priya! Yes absolutely, let's meet at Central Library 2nd floor study cubicle around 3:30 PM.",
      timestamp = System.currentTimeMillis() - 1800000L,
      isRead = true,
      readBy = setOf(STUDENT_AARAV.id)
    )

    _chatMessages.value = listOf(msg1, msg2, msg3, dm1, dm2)

    // 3. Polls
    _polls.value = listOf(
      Poll(
        id = "poll_1",
        question = "Should Central Library extend 24/7 night hours during Mid-Sem Exams?",
        options = listOf(
          PollOption("opt_1", "Yes, 100% need 24/7 access", 48, setOf("u_1", "u_2")),
          PollOption("opt_2", "Extend till 2:00 AM only", 12),
          PollOption("opt_3", "Current 10:00 PM is sufficient", 3)
        ),
        creatorId = STUDENT_AARAV.id,
        creatorName = STUDENT_AARAV.name,
        creatorFaculty = STUDENT_AARAV.faculty,
        createdAt = System.currentTimeMillis() - 7200000L,
        expiresAt = System.currentTimeMillis() + 86400000L * 5,
        category = "Campus Facilities"
      ),
      Poll(
        id = "poll_2",
        question = "Preferred venue & theme for MGUG Annual Fest 'Tarang 2026'?",
        options = listOf(
          PollOption("opt_21", "Main Auditorium - Cyberpunk & Tech", 35),
          PollOption("opt_22", "Open Air Amphitheatre - Desi Fusion", 42, setOf("u_3")),
          PollOption("opt_23", "Sports Complex - Bollywood Retro", 19)
        ),
        creatorId = STUDENT_ANANYA.id,
        creatorName = STUDENT_ANANYA.name,
        creatorFaculty = STUDENT_ANANYA.faculty,
        createdAt = System.currentTimeMillis() - 14400000L,
        expiresAt = System.currentTimeMillis() + 86400000L * 6,
        category = "Student Council"
      )
    )

    // 4. Events
    _events.value = listOf(
      Event(
        id = "event_1",
        title = "MGUG Inter-Faculty Hackathon 2026",
        description = "36-hour sprint creating solutions for Healthcare, Smart Campus, and AI. Mentorship from industry tech leads and cash prizes worth ₹1,50,000!",
        category = "Tech & Innovation",
        date = "Oct 12, 2026",
        time = "09:00 AM IST",
        venue = "FST Computer Labs 3 & 4, Ground Floor",
        organizer = "Faculty of Science & Tech · Coding Club",
        contactInfo = "aarav.cse@mgug.ac.in",
        registeredUserIds = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id, STUDENT_ROHAN.id),
        isUpcoming = true,
        bannerGradientIndex = 0
      ),
      Event(
        id = "event_2",
        title = "National Ayurveda & Integrative Medicine Conclave",
        description = "Keynotes by distinguished researchers on pharmacokinetics of traditional herbs and clinical trials in modern medicine.",
        category = "Academic & Research",
        date = "Oct 18, 2026",
        time = "10:30 AM IST",
        venue = "Main University Auditorium",
        organizer = "Faculty of Ayurveda, MGUG",
        contactInfo = "ananya.ayur@mgug.ac.in",
        registeredUserIds = setOf(STUDENT_ANANYA.id, STUDENT_PRIYA.id),
        isUpcoming = true,
        bannerGradientIndex = 1
      )
    )

    // 5. Feed Items
    _feedItems.value = listOf(
      FeedItem(
        id = "post_notice_1",
        authorId = "admin_council",
        authorName = "MGUG Student Welfare Board",
        authorFaculty = "University Administration",
        authorYear = "Official Notice",
        authorColorHex = 0xFFFF5722,
        timestamp = System.currentTimeMillis() - 3600000L * 5,
        content = "📢 Official University Notice: Mid-Semester exam admit cards are now available on the MGUG ERP portal. Please verify your course credentials and reach out to Dean of Students for corrections by Oct 5th.",
        type = FeedItemType.CAMPUS_UPDATE,
        isOfficialNotice = true,
        likes = setOf(STUDENT_AARAV.id, STUDENT_PRIYA.id, STUDENT_ROHAN.id, STUDENT_ANANYA.id),
        commentsCount = 2
      ),
      FeedItem(
        id = "post_study_1",
        authorId = STUDENT_PRIYA.id,
        authorName = STUDENT_PRIYA.name,
        authorFaculty = STUDENT_PRIYA.faculty,
        authorYear = STUDENT_PRIYA.year,
        authorColorHex = STUDENT_PRIYA.avatarColorHex,
        timestamp = System.currentTimeMillis() - 3600000L * 3,
        content = "Looking for a serious revision partner for Pharmaceutical Chemistry & Reaction Mechanisms before Mid-Sems! Daily 2 hours focus session.",
        type = FeedItemType.STUDY_PARTNER,
        subject = "Pharmaceutics & Organic Chemistry",
        targetExam = "Mid-Sem Exams (Oct 2026)",
        studyHours = "5:00 PM – 7:00 PM Daily",
        studyLocation = "Central Library, 2nd Floor Quiet Zone",
        likes = setOf(STUDENT_AARAV.id, STUDENT_ANANYA.id),
        commentsCount = 1
      ),
      FeedItem(
        id = "post_proj_1",
        authorId = STUDENT_AARAV.id,
        authorName = STUDENT_AARAV.name,
        authorFaculty = STUDENT_AARAV.faculty,
        authorYear = STUDENT_AARAV.year,
        authorColorHex = STUDENT_AARAV.avatarColorHex,
        timestamp = System.currentTimeMillis() - 3600000L * 2,
        content = "Building an IoT-based Smart Hospital Bed occupancy tracker for Gorakhnath Hospital. Need a Flutter or Compose mobile dev and an embedded systems geek!",
        type = FeedItemType.PROJECT_PARTNER,
        projectTitle = "SmartCare IoT Hospital System",
        techStack = "Kotlin, Jetpack Compose, MQTT, ESP32",
        rolesNeeded = "1x Android Dev, 1x IoT Hardware",
        deadline = "Oct 25 (Pre-Incubation Demo)",
        likes = setOf(STUDENT_ROHAN.id, STUDENT_PRIYA.id),
        commentsCount = 3
      ),
      FeedItem(
        id = "post_lf_1",
        authorId = STUDENT_ROHAN.id,
        authorName = STUDENT_ROHAN.name,
        authorFaculty = STUDENT_ROHAN.faculty,
        authorYear = STUDENT_ROHAN.year,
        authorColorHex = STUDENT_ROHAN.avatarColorHex,
        collegeName = STUDENT_ROHAN.collegeName,
        timestamp = System.currentTimeMillis() - 3600000L,
        content = "[LOST] Blue Casio Scientific Calculator (fx-991EX) left behind in Lecture Hall 3 during Biostatistics lecture.",
        type = FeedItemType.LOST_FOUND,
        lostFoundType = LostFoundType.LOST,
        lostFoundStatus = LostFoundStatus.ACTIVE,
        lostFoundCategory = "📱 Electronics",
        itemName = "Casio fx-991EX Scientific Calculator",
        itemDate = "Yesterday, 3:30 PM",
        campusLocation = "Academic Block A, LH 3, 2nd Row",
        contactInfo = "In-App Connect",
        likes = setOf(STUDENT_AARAV.id),
        commentsCount = 0
      ),
      FeedItem(
        id = "post_lf_2",
        authorId = STUDENT_PRIYA.id,
        authorName = STUDENT_PRIYA.name,
        authorFaculty = STUDENT_PRIYA.faculty,
        authorYear = STUDENT_PRIYA.year,
        authorColorHex = STUDENT_PRIYA.avatarColorHex,
        collegeName = STUDENT_PRIYA.collegeName,
        timestamp = System.currentTimeMillis() - 7200000L,
        content = "[FOUND] Titan Black Quartz Watch found near Reading Desk 4 in Central Library.",
        type = FeedItemType.LOST_FOUND,
        lostFoundType = LostFoundType.FOUND,
        lostFoundStatus = LostFoundStatus.ACTIVE,
        lostFoundCategory = "👓 Accessories",
        itemName = "Titan Black Quartz Watch",
        itemDate = "Today, 10:15 AM",
        campusLocation = "Central Library Reading Desk 4",
        contactInfo = "In-App Connect",
        likes = setOf(STUDENT_ROHAN.id),
        commentsCount = 0
      )
    )

    // 6. Comments
    _comments.value = mapOf(
      "post_proj_1" to listOf(
        Comment("c1", "post_proj_1", STUDENT_ROHAN.id, STUDENT_ROHAN.name, STUDENT_ROHAN.faculty, STUDENT_ROHAN.avatarColorHex, "I can test it with the clinical team at the hospital ward!", System.currentTimeMillis() - 3600000L),
        Comment("c2", "post_proj_1", STUDENT_PRIYA.id, STUDENT_PRIYA.name, STUDENT_PRIYA.faculty, STUDENT_PRIYA.avatarColorHex, "Great initiative! Let me know if you need healthcare workflow inputs.", System.currentTimeMillis() - 1800000L)
      ),
      "post_study_1" to listOf(
        Comment("c3", "post_study_1", STUDENT_ANANYA.id, STUDENT_ANANYA.name, STUDENT_ANANYA.faculty, STUDENT_ANANYA.avatarColorHex, "I'll join for organic synthesis revision on Wednesdays!", System.currentTimeMillis() - 1200000L)
      )
    )

    // 7. Seed Notifications
    _notifications.value = listOf(
      NotificationItem(
        id = "n1",
        userId = STUDENT_AARAV.id,
        title = "New message from Priya Verma",
        message = "Are you attending the Campus Hackathon briefing tomorrow?",
        timestamp = System.currentTimeMillis() - 1200000L,
        type = NotificationType.CHAT_MESSAGE,
        isRead = false,
        targetId = STUDENT_PRIYA.id,
        senderName = STUDENT_PRIYA.name,
        senderAvatarHex = STUDENT_PRIYA.avatarColorHex
      ),
      NotificationItem(
        id = "n2",
        userId = STUDENT_AARAV.id,
        title = "Upcoming Event in 3 Days 📅",
        message = "MGUG Inter-Faculty Hackathon 2026 starts at FST Computer Labs!",
        timestamp = System.currentTimeMillis() - 3600000L * 2,
        type = NotificationType.UPCOMING_EVENT,
        isRead = false,
        targetId = "event_1"
      ),
      NotificationItem(
        id = "n3",
        userId = STUDENT_AARAV.id,
        title = "New Campus Poll Published 📊",
        message = "Should Central Library extend 24/7 night hours during Mid-Sem Exams?",
        timestamp = System.currentTimeMillis() - 7200000L,
        type = NotificationType.NEW_POLL,
        isRead = true,
        targetId = "poll_1"
      ),
      NotificationItem(
        id = "n4",
        userId = STUDENT_AARAV.id,
        title = "Rohan Gupta commented on your post",
        message = "\"I can test it with the clinical team at the hospital ward!\"",
        timestamp = System.currentTimeMillis() - 3600000L,
        type = NotificationType.POST_REPLY,
        isRead = true,
        targetId = "post_proj_1",
        senderName = STUDENT_ROHAN.name,
        senderAvatarHex = STUDENT_ROHAN.avatarColorHex
      ),
      NotificationItem(
        id = "n5",
        userId = STUDENT_AARAV.id,
        title = "Official MGUG Notice 📢",
        message = "Mid-Semester exam admit cards are now available on the MGUG ERP portal.",
        timestamp = System.currentTimeMillis() - 3600000L * 5,
        type = NotificationType.CAMPUS_NOTICE,
        isRead = true
      )
    )
  }
}
