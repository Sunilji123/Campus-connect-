package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.ChatMessage
import com.example.data.model.Comment
import com.example.data.model.Event
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LostFoundStatus
import com.example.data.model.LostFoundType
import com.example.data.model.MessageType
import com.example.data.model.Poll
import com.example.data.model.PollOption
import com.example.data.model.StudentProfile
import com.example.data.model.VerificationMethod
import com.example.data.model.VerificationStatus
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object FirebaseFirestoreManager {
  private const val TAG = "FirebaseFirestoreManager"
  private val scope = CoroutineScope(Dispatchers.IO)

  private var firestore: FirebaseFirestore? = null
  private var isInitialized = false

  private var feedListener: ListenerRegistration? = null
  private var pollsListener: ListenerRegistration? = null
  private var eventsListener: ListenerRegistration? = null
  private var chatListener: ListenerRegistration? = null

  fun initialize(context: Context) {
    if (isInitialized) return

    try {
      if (FirebaseApp.getApps(context).isEmpty()) {
        FirebaseApp.initializeApp(context)
      }
      val app = FirebaseApp.getInstance()
      val dbId = try {
        context.getString(R.string.firestore_database_id)
      } catch (e: Exception) {
        ""
      }

      firestore = if (dbId.isNotEmpty() && dbId != "(default)") {
        Log.i(TAG, "Initializing Firestore with named database ID: $dbId")
        FirebaseFirestore.getInstance(app, dbId)
      } else {
        FirebaseFirestore.getInstance(app)
      }
      isInitialized = true
      Log.i(TAG, "Firestore successfully initialized")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize Firestore with custom dbId, falling back to default", e)
      try {
        firestore = FirebaseFirestore.getInstance()
        isInitialized = true
      } catch (fallbackEx: Exception) {
        Log.e(TAG, "Failed to initialize default Firestore", fallbackEx)
      }
    }
  }

  // ==========================================
  // USERS PERSISTENCE
  // ==========================================

  fun saveUserProfile(profile: StudentProfile) {
    val db = firestore ?: return
    scope.launch {
      try {
        val data = mapOf(
          "id" to profile.id,
          "name" to profile.name,
          "email" to profile.email,
          "rollNo" to profile.rollNo,
          "collegeId" to profile.collegeId,
          "collegeName" to profile.collegeName,
          "universityName" to profile.universityName,
          "department" to profile.department,
          "faculty" to profile.faculty,
          "course" to profile.course,
          "year" to profile.year,
          "age" to profile.age,
          "gender" to profile.gender,
          "avatarColorHex" to profile.avatarColorHex,
          "avatarUrl" to (profile.avatarUrl ?: ""),
          "isVerified" to profile.isVerified,
          "verificationStatus" to profile.verificationStatus.name,
          "verificationMethod" to (profile.verificationMethod?.name ?: ""),
          "bio" to profile.bio,
          "statusEmoji" to profile.statusEmoji,
          "statusText" to profile.statusText,
          "instagramHandle" to (profile.instagramHandle ?: ""),
          "snapchatHandle" to (profile.snapchatHandle ?: ""),
          "githubHandle" to (profile.githubHandle ?: ""),
          "connectionPreferences" to profile.connectionPreferences,
          "interests" to profile.interests,
          "skills" to profile.skills,
          "graduationYear" to profile.graduationYear,
          "isGoogleUser" to profile.isGoogleUser,
          "freeSuggestionsRemaining" to profile.freeSuggestionsRemaining,
          "updatedAt" to System.currentTimeMillis()
        )
        db.collection("users").document(profile.id)
          .set(data, SetOptions.merge())
          .addOnSuccessListener {
            Log.d(TAG, "User profile saved to Firestore: ${profile.name} (${profile.id})")
          }
          .addOnFailureListener { e ->
            Log.w(TAG, "Failed to save user profile: ${e.message}")
          }
      } catch (e: Exception) {
        Log.e(TAG, "Error saving user profile to Firestore", e)
      }
    }
  }

  fun listenToUsers(onUpdate: (List<StudentProfile>) -> Unit): ListenerRegistration? {
    val db = firestore ?: return null
    return db.collection("users").addSnapshotListener { snapshot, error ->
      if (error != null) {
        Log.w(TAG, "Listen to users failed: ${error.message}")
        return@addSnapshotListener
      }
      if (snapshot != null && !snapshot.isEmpty) {
        val profiles = snapshot.documents.mapNotNull { doc -> documentToProfile(doc) }
        onUpdate(profiles)
      }
    }
  }

  private fun documentToProfile(doc: DocumentSnapshot): StudentProfile? {
    return try {
      val id = doc.getString("id") ?: doc.id
      val name = doc.getString("name") ?: return null
      val email = doc.getString("email") ?: ""
      val rollNo = doc.getString("rollNo") ?: ""
      val collegeId = doc.getString("collegeId") ?: "col_dtu"
      val collegeName = doc.getString("collegeName") ?: "Delhi Technological University"
      val universityName = doc.getString("universityName") ?: "Delhi Technological University"
      val department = doc.getString("department") ?: "Computer Science & Engineering"
      val faculty = doc.getString("faculty") ?: "Faculty of Technology"
      val course = doc.getString("course") ?: "B.Tech Computer Science"
      val year = doc.getString("year") ?: "2nd Year"
      val age = doc.getLong("age")?.toInt() ?: 20
      val gender = doc.getString("gender") ?: "Not Specified"
      val avatarColorHex = doc.getLong("avatarColorHex") ?: 0xFF651FFF
      val avatarUrl = doc.getString("avatarUrl")?.ifEmpty { null }
      val isVerified = doc.getBoolean("isVerified") ?: true
      val statusStr = doc.getString("verificationStatus") ?: VerificationStatus.VERIFIED.name
      val verificationStatus = try {
        VerificationStatus.valueOf(statusStr)
      } catch (e: Exception) {
        VerificationStatus.VERIFIED
      }
      val methodStr = doc.getString("verificationMethod")
      val verificationMethod = if (!methodStr.isNullOrEmpty()) {
        try { VerificationMethod.valueOf(methodStr) } catch (e: Exception) { null }
      } else null
      val bio = doc.getString("bio") ?: "Passionate student & developer"
      val statusEmoji = doc.getString("statusEmoji") ?: "⚡"
      val statusText = doc.getString("statusText") ?: "Campus Live"
      val instagramHandle = doc.getString("instagramHandle")?.ifEmpty { null }
      val snapchatHandle = doc.getString("snapchatHandle")?.ifEmpty { null }
      val githubHandle = doc.getString("githubHandle")?.ifEmpty { null }
      val connectionPreferences = (doc.get("connectionPreferences") as? List<*>)?.filterIsInstance<String>()
        ?: listOf("Friendship", "Study Partner", "Project Partner")
      val interests = (doc.get("interests") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
      val skills = (doc.get("skills") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
      val graduationYear = doc.getString("graduationYear") ?: "2026"
      val isGoogleUser = doc.getBoolean("isGoogleUser") ?: false
      val freeSuggestionsRemaining = doc.getLong("freeSuggestionsRemaining")?.toInt() ?: 15

      StudentProfile(
        id = id,
        name = name,
        email = email,
        rollNo = rollNo,
        collegeId = collegeId,
        collegeName = collegeName,
        universityName = universityName,
        department = department,
        faculty = faculty,
        course = course,
        year = year,
        age = age,
        gender = gender,
        avatarColorHex = avatarColorHex,
        avatarUrl = avatarUrl,
        isVerified = isVerified,
        verificationStatus = verificationStatus,
        verificationMethod = verificationMethod,
        bio = bio,
        statusEmoji = statusEmoji,
        statusText = statusText,
        instagramHandle = instagramHandle,
        snapchatHandle = snapchatHandle,
        githubHandle = githubHandle,
        connectionPreferences = connectionPreferences,
        interests = interests,
        skills = skills,
        graduationYear = graduationYear,
        isGoogleUser = isGoogleUser,
        freeSuggestionsRemaining = freeSuggestionsRemaining
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error mapping document to profile", e)
      null
    }
  }

  // ==========================================
  // FEED POSTS PERSISTENCE
  // ==========================================

  fun saveFeedItem(item: FeedItem) {
    val db = firestore ?: return
    scope.launch {
      try {
        val data = mapOf(
          "id" to item.id,
          "authorId" to item.authorId,
          "authorName" to item.authorName,
          "authorFaculty" to item.authorFaculty,
          "authorYear" to item.authorYear,
          "authorColorHex" to item.authorColorHex,
          "authorAvatarUrl" to (item.authorAvatarUrl ?: ""),
          "collegeName" to (item.collegeName ?: "Campus Community"),
          "timestamp" to item.timestamp,
          "content" to item.content,
          "type" to item.type.name,
          "likes" to item.likes.toList(),
          "commentsCount" to item.commentsCount,
          "shareCount" to item.shareCount,
          "isOfficialNotice" to item.isOfficialNotice,
          "subject" to (item.subject ?: ""),
          "targetExam" to (item.targetExam ?: ""),
          "studyHours" to (item.studyHours ?: ""),
          "studyLocation" to (item.studyLocation ?: ""),
          "projectTitle" to (item.projectTitle ?: ""),
          "techStack" to (item.techStack ?: ""),
          "rolesNeeded" to (item.rolesNeeded ?: ""),
          "deadline" to (item.deadline ?: ""),
          "lostFoundType" to (item.lostFoundType?.name ?: ""),
          "campusLocation" to (item.campusLocation ?: ""),
          "contactInfo" to (item.contactInfo ?: ""),
          "isResolved" to item.isResolved,
          "lostFoundStatus" to item.lostFoundStatus.name,
          "lostFoundCategory" to (item.lostFoundCategory ?: ""),
          "itemName" to (item.itemName ?: ""),
          "itemDate" to (item.itemDate ?: ""),
          "itemImageUrl" to (item.itemImageUrl ?: "")
        )
        db.collection("feed_posts").document(item.id)
          .set(data, SetOptions.merge())
          .addOnSuccessListener {
            Log.d(TAG, "Feed post synced to Firestore: ${item.id}")
          }
          .addOnFailureListener { e ->
            Log.w(TAG, "Failed to sync feed post: ${e.message}")
          }
      } catch (e: Exception) {
        Log.e(TAG, "Error saving feed post to Firestore", e)
      }
    }
  }

  fun deleteFeedItem(itemId: String) {
    val db = firestore ?: return
    db.collection("feed_posts").document(itemId).delete()
  }

  fun toggleFeedItemLike(itemId: String, updatedLikes: Set<String>) {
    val db = firestore ?: return
    db.collection("feed_posts").document(itemId)
      .update("likes", updatedLikes.toList())
  }

  fun listenToFeedItems(onUpdate: (List<FeedItem>) -> Unit) {
    val db = firestore ?: return
    feedListener?.remove()
    feedListener = db.collection("feed_posts")
      .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
      .limit(50)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Listen to feed_posts failed: ${error.message}")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val items = snapshot.documents.mapNotNull { doc -> documentToFeedItem(doc) }
          onUpdate(items)
        }
      }
  }

  private fun documentToFeedItem(doc: DocumentSnapshot): FeedItem? {
    return try {
      val id = doc.getString("id") ?: doc.id
      val authorId = doc.getString("authorId") ?: "unknown"
      val authorName = doc.getString("authorName") ?: "Campus Student"
      val authorFaculty = doc.getString("authorFaculty") ?: "MGUG"
      val authorYear = doc.getString("authorYear") ?: "3rd Year"
      val authorColorHex = doc.getLong("authorColorHex") ?: 0xFF651FFF
      val authorAvatarUrl = doc.getString("authorAvatarUrl")?.ifEmpty { null }
      val collegeName = doc.getString("collegeName") ?: "Campus Community"
      val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
      val content = doc.getString("content") ?: ""
      val typeStr = doc.getString("type") ?: FeedItemType.GENERAL.name
      val type = try { FeedItemType.valueOf(typeStr) } catch (e: Exception) { FeedItemType.GENERAL }
      val rawLikes = (doc.get("likes") as? List<*>)?.filterIsInstance<String>() ?: emptyList()
      val likes = rawLikes.toSet()
      val commentsCount = (doc.getLong("commentsCount") ?: 0).toInt()
      val shareCount = (doc.getLong("shareCount") ?: 0).toInt()
      val isOfficialNotice = doc.getBoolean("isOfficialNotice") ?: false
      val subject = doc.getString("subject")?.ifEmpty { null }
      val targetExam = doc.getString("targetExam")?.ifEmpty { null }
      val studyHours = doc.getString("studyHours")?.ifEmpty { null }
      val studyLocation = doc.getString("studyLocation")?.ifEmpty { null }
      val projectTitle = doc.getString("projectTitle")?.ifEmpty { null }
      val techStack = doc.getString("techStack")?.ifEmpty { null }
      val rolesNeeded = doc.getString("rolesNeeded")?.ifEmpty { null }
      val deadline = doc.getString("deadline")?.ifEmpty { null }
      val lostFoundTypeStr = doc.getString("lostFoundType")
      val lostFoundType = if (!lostFoundTypeStr.isNullOrEmpty()) {
        try { LostFoundType.valueOf(lostFoundTypeStr) } catch (e: Exception) { null }
      } else null
      val campusLocation = doc.getString("campusLocation")?.ifEmpty { null }
      val contactInfo = doc.getString("contactInfo")?.ifEmpty { null }
      val isResolved = doc.getBoolean("isResolved") ?: false
      val lostFoundStatusStr = doc.getString("lostFoundStatus")
      val lostFoundStatus = if (!lostFoundStatusStr.isNullOrEmpty()) {
        try { LostFoundStatus.valueOf(lostFoundStatusStr) } catch (e: Exception) { LostFoundStatus.ACTIVE }
      } else if (isResolved) LostFoundStatus.FOUND_RETURNED else LostFoundStatus.ACTIVE
      val lostFoundCategory = doc.getString("lostFoundCategory")?.ifEmpty { null }
      val itemName = doc.getString("itemName")?.ifEmpty { null }
      val itemDate = doc.getString("itemDate")?.ifEmpty { null }
      val itemImageUrl = doc.getString("itemImageUrl")?.ifEmpty { null }

      FeedItem(
        id = id,
        authorId = authorId,
        authorName = authorName,
        authorFaculty = authorFaculty,
        authorYear = authorYear,
        authorColorHex = authorColorHex,
        authorAvatarUrl = authorAvatarUrl,
        collegeName = collegeName,
        timestamp = timestamp,
        content = content,
        type = type,
        likes = likes,
        commentsCount = commentsCount,
        shareCount = shareCount,
        isOfficialNotice = isOfficialNotice,
        subject = subject,
        targetExam = targetExam,
        studyHours = studyHours,
        studyLocation = studyLocation,
        projectTitle = projectTitle,
        techStack = techStack,
        rolesNeeded = rolesNeeded,
        deadline = deadline,
        lostFoundType = lostFoundType,
        campusLocation = campusLocation,
        contactInfo = contactInfo,
        isResolved = isResolved,
        lostFoundStatus = lostFoundStatus,
        lostFoundCategory = lostFoundCategory,
        itemName = itemName,
        itemDate = itemDate,
        itemImageUrl = itemImageUrl
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error mapping doc to FeedItem: ${e.message}")
      null
    }
  }

  // ==========================================
  // POLLS PERSISTENCE
  // ==========================================

  fun savePoll(poll: Poll) {
    val db = firestore ?: return
    scope.launch {
      try {
        val optionsList = poll.options.map { opt ->
          mapOf(
            "id" to opt.id,
            "text" to opt.text,
            "voteCount" to opt.voteCount,
            "voterIds" to opt.voterIds.toList()
          )
        }
        val data = mapOf(
          "id" to poll.id,
          "question" to poll.question,
          "creatorId" to poll.creatorId,
          "creatorName" to poll.creatorName,
          "creatorFaculty" to poll.creatorFaculty,
          "createdAt" to poll.createdAt,
          "expiresAt" to poll.expiresAt,
          "isActive" to poll.isActive,
          "category" to poll.category,
          "options" to optionsList
        )
        db.collection("polls").document(poll.id).set(data, SetOptions.merge())
      } catch (e: Exception) {
        Log.e(TAG, "Error saving poll to Firestore", e)
      }
    }
  }

  fun listenToPolls(onUpdate: (List<Poll>) -> Unit) {
    val db = firestore ?: return
    pollsListener?.remove()
    pollsListener = db.collection("polls")
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Listen to polls failed: ${error.message}")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val polls = snapshot.documents.mapNotNull { doc -> documentToPoll(doc) }
          onUpdate(polls)
        }
      }
  }

  private fun documentToPoll(doc: DocumentSnapshot): Poll? {
    return try {
      val id = doc.getString("id") ?: doc.id
      val question = doc.getString("question") ?: ""
      val creatorId = doc.getString("creatorId") ?: ""
      val creatorName = doc.getString("creatorName") ?: "Campus Student"
      val creatorFaculty = doc.getString("creatorFaculty") ?: "MGUG"
      val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
      val expiresAt = doc.getLong("expiresAt") ?: (System.currentTimeMillis() + 86400000L)
      val isActive = doc.getBoolean("isActive") ?: true
      val category = doc.getString("category") ?: "Campus Life"
      val rawOptions = doc.get("options") as? List<*> ?: emptyList<Any>()
      val options = rawOptions.mapIndexed { idx, item ->
        val map = item as? Map<*, *>
        val optId = map?.get("id") as? String ?: "opt_$idx"
        val text = map?.get("text") as? String ?: "Option ${idx + 1}"
        val voteCount = (map?.get("voteCount") as? Long ?: 0L).toInt()
        val voterIds = (map?.get("voterIds") as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
        PollOption(id = optId, text = text, voteCount = voteCount, voterIds = voterIds)
      }
      Poll(
        id = id,
        question = question,
        options = options,
        creatorId = creatorId,
        creatorName = creatorName,
        creatorFaculty = creatorFaculty,
        createdAt = createdAt,
        expiresAt = expiresAt,
        isActive = isActive,
        category = category
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error mapping doc to Poll: ${e.message}")
      null
    }
  }

  // ==========================================
  // EVENTS PERSISTENCE
  // ==========================================

  fun saveEvent(event: Event) {
    val db = firestore ?: return
    scope.launch {
      try {
        val data = mapOf(
          "id" to event.id,
          "title" to event.title,
          "description" to event.description,
          "category" to event.category,
          "date" to event.date,
          "time" to event.time,
          "venue" to event.venue,
          "organizer" to event.organizer,
          "contactInfo" to event.contactInfo,
          "registeredUserIds" to event.registeredUserIds.toList(),
          "isUpcoming" to event.isUpcoming,
          "bannerGradientIndex" to event.bannerGradientIndex
        )
        db.collection("events").document(event.id).set(data, SetOptions.merge())
      } catch (e: Exception) {
        Log.e(TAG, "Error saving event to Firestore", e)
      }
    }
  }

  fun listenToEvents(onUpdate: (List<Event>) -> Unit) {
    val db = firestore ?: return
    eventsListener?.remove()
    eventsListener = db.collection("events")
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Listen to events failed: ${error.message}")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val events = snapshot.documents.mapNotNull { doc -> documentToEvent(doc) }
          onUpdate(events)
        }
      }
  }

  private fun documentToEvent(doc: DocumentSnapshot): Event? {
    return try {
      val id = doc.getString("id") ?: doc.id
      val title = doc.getString("title") ?: ""
      val description = doc.getString("description") ?: ""
      val category = doc.getString("category") ?: "General"
      val date = doc.getString("date") ?: ""
      val time = doc.getString("time") ?: ""
      val venue = doc.getString("venue") ?: ""
      val organizer = doc.getString("organizer") ?: ""
      val contactInfo = doc.getString("contactInfo") ?: ""
      val registeredUserIds = (doc.get("registeredUserIds") as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
      val isUpcoming = doc.getBoolean("isUpcoming") ?: true
      val bannerGradientIndex = (doc.getLong("bannerGradientIndex") ?: 0L).toInt()

      Event(
        id = id,
        title = title,
        description = description,
        category = category,
        date = date,
        time = time,
        venue = venue,
        organizer = organizer,
        contactInfo = contactInfo,
        registeredUserIds = registeredUserIds,
        isUpcoming = isUpcoming,
        bannerGradientIndex = bannerGradientIndex
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error mapping doc to Event: ${e.message}")
      null
    }
  }

  // ==========================================
  // CHAT MESSAGES PERSISTENCE
  // ==========================================

  fun saveChatMessage(message: ChatMessage) {
    val db = firestore ?: return
    scope.launch {
      try {
        val data = mapOf(
          "id" to message.id,
          "conversationId" to message.conversationId,
          "senderId" to message.senderId,
          "senderName" to message.senderName,
          "receiverId" to (message.receiverId ?: ""),
          "content" to message.content,
          "timestamp" to message.timestamp,
          "isRead" to message.isRead,
          "readBy" to message.readBy.toList(),
          "messageType" to message.messageType.name,
          "reactions" to message.reactions
        )
        db.collection("chat_messages").document(message.id).set(data, SetOptions.merge())
      } catch (e: Exception) {
        Log.e(TAG, "Error saving chat message to Firestore", e)
      }
    }
  }

  fun listenToChatMessages(onUpdate: (List<ChatMessage>) -> Unit) {
    val db = firestore ?: return
    chatListener?.remove()
    chatListener = db.collection("chat_messages")
      .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING)
      .limitToLast(100)
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w(TAG, "Listen to chat messages failed: ${error.message}")
          return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
          val msgs = snapshot.documents.mapNotNull { doc -> documentToChatMessage(doc) }
          onUpdate(msgs)
        }
      }
  }

  private fun documentToChatMessage(doc: DocumentSnapshot): ChatMessage? {
    return try {
      val id = doc.getString("id") ?: doc.id
      val conversationId = doc.getString("conversationId") ?: ""
      val senderId = doc.getString("senderId") ?: ""
      val senderName = doc.getString("senderName") ?: ""
      val receiverId = doc.getString("receiverId")?.ifEmpty { null }
      val content = doc.getString("content") ?: ""
      val timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
      val isRead = doc.getBoolean("isRead") ?: false
      val readBy = (doc.get("readBy") as? List<*>)?.filterIsInstance<String>()?.toSet() ?: emptySet()
      val messageTypeStr = doc.getString("messageType") ?: MessageType.TEXT.name
      val messageType = try { MessageType.valueOf(messageTypeStr) } catch (e: Exception) { MessageType.TEXT }
      val reactions = (doc.get("reactions") as? Map<*, *>)?.mapNotNull { (k, v) ->
        val key = k as? String
        val value = v as? String
        if (key != null && value != null) key to value else null
      }?.toMap() ?: emptyMap()

      ChatMessage(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        senderName = senderName,
        receiverId = receiverId,
        content = content,
        timestamp = timestamp,
        isRead = isRead,
        readBy = readBy,
        messageType = messageType,
        reactions = reactions
      )
    } catch (e: Exception) {
      Log.e(TAG, "Error mapping doc to ChatMessage: ${e.message}")
      null
    }
  }
}
