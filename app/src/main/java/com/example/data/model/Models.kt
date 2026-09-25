package com.example.data.model

enum class FeedItemType {
  GENERAL,
  STUDY_PARTNER,
  PROJECT_PARTNER,
  LOST_FOUND,
  CAMPUS_UPDATE
}

enum class LostFoundType {
  LOST,
  FOUND
}

enum class LostFoundStatus {
  ACTIVE,
  FOUND_RETURNED,
  CLOSED
}

val LOST_FOUND_CATEGORIES = listOf(
  "All",
  "📱 Electronics",
  "🎒 Bags",
  "👛 Wallet",
  "🔑 Keys",
  "📚 Books",
  "🪪 ID/Card",
  "👓 Accessories",
  "🧥 Clothing",
  "📦 Other"
)

enum class ConnectionStatus {
  CONNECTED,
  CONNECTING,
  OFFLINE
}

enum class VerificationStatus {
  VERIFIED,
  PENDING_REVIEW,
  UNVERIFIED
}

enum class VerificationMethod {
  COLLEGE_EMAIL_OTP,
  STUDENT_ID_CARD_UPLOAD,
  FACULTY_NOMINATION
}

data class CollegeInfo(
  val id: String,
  val name: String,
  val universityName: String,
  val city: String,
  val state: String,
  val studentCount: Int = 1200
)

data class StudentProfile(
  val id: String,
  val name: String,
  val email: String,
  val rollNo: String = "",
  val collegeId: String = "col_dtu",
  val collegeName: String = "Delhi Technological University",
  val universityName: String = "Delhi Technological University",
  val department: String = "Computer Science & Engineering",
  val faculty: String = "Faculty of Technology",
  val course: String = "B.Tech Computer Science",
  val year: String = "2nd Year",
  val age: Int = 20,
  val gender: String = "Not Specified",
  val avatarColorHex: Long = 0xFF651FFF,
  val avatarUrl: String? = null,
  val isVerified: Boolean = true,
  val verificationStatus: VerificationStatus = VerificationStatus.VERIFIED,
  val verificationMethod: VerificationMethod? = VerificationMethod.COLLEGE_EMAIL_OTP,
  val verifiedAt: Long = System.currentTimeMillis(),
  val bio: String = "Passionate student, developer & campus community enthusiast.",
  val statusEmoji: String = "⚡",
  val statusText: String = "Campus Live · Student",
  val instagramHandle: String? = null,
  val snapchatHandle: String? = null,
  val githubHandle: String? = null,
  val connectionPreferences: List<String> = listOf("Friendship", "Study Partner", "Project Partner"),
  val interests: List<String> = emptyList(),
  val skills: List<String> = emptyList(),
  val graduationYear: String = "2026",
  val isGoogleUser: Boolean = false,
  val freeSuggestionsRemaining: Int = 15
)

data class PollOption(
  val id: String,
  val text: String,
  val voteCount: Int = 0,
  val voterIds: Set<String> = emptySet()
)

data class Poll(
  val id: String,
  val question: String,
  val options: List<PollOption>,
  val creatorId: String,
  val creatorName: String,
  val creatorFaculty: String,
  val createdAt: Long,
  val expiresAt: Long,
  val isActive: Boolean = true,
  val category: String = "Campus Life"
) {
  val totalVotes: Int
    get() = options.sumOf { it.voteCount }

  fun hasVoted(userId: String): Boolean =
    options.any { it.voterIds.contains(userId) }

  fun getUserVotedOptionIndex(userId: String): Int =
    options.indexOfFirst { it.voterIds.contains(userId) }
}

data class Event(
  val id: String,
  val title: String,
  val description: String,
  val category: String,
  val date: String,
  val time: String,
  val venue: String,
  val organizer: String,
  val contactInfo: String,
  val registeredUserIds: Set<String> = emptySet(),
  val isUpcoming: Boolean = true,
  val bannerGradientIndex: Int = 0
) {
  val attendeeCount: Int
    get() = registeredUserIds.size

  fun isRegistered(userId: String): Boolean =
    registeredUserIds.contains(userId)
}

data class FeedItem(
  val id: String,
  val authorId: String,
  val authorName: String,
  val authorFaculty: String,
  val authorYear: String,
  val authorColorHex: Long,
  val authorAvatarUrl: String? = null,
  val collegeName: String? = "Campus Community",
  val universityName: String? = null,
  val timestamp: Long,
  val content: String,
  val type: FeedItemType = FeedItemType.GENERAL,
  val likes: Set<String> = emptySet(),
  val commentsCount: Int = 0,
  val shareCount: Int = 0,
  val isOfficialNotice: Boolean = false,
  val reportedBy: Set<String> = emptySet(),
  // Study Partner specific
  val subject: String? = null,
  val targetExam: String? = null,
  val studyHours: String? = null,
  val studyLocation: String? = null,
  val studyMode: String? = "In-Person / Online",
  // Project Partner specific
  val projectTitle: String? = null,
  val techStack: String? = null,
  val rolesNeeded: String? = null,
  val deadline: String? = null,
  // Lost and Found specific
  val lostFoundType: LostFoundType? = null,
  val campusLocation: String? = null,
  val contactInfo: String? = null,
  val isResolved: Boolean = false,
  val lostFoundStatus: LostFoundStatus = LostFoundStatus.ACTIVE,
  val lostFoundCategory: String? = null,
  val itemName: String? = null,
  val itemDate: String? = null,
  val itemImageUrl: String? = null
) {
  val likeCount: Int
    get() = likes.size

  fun isLikedBy(userId: String): Boolean = likes.contains(userId)
}

data class Comment(
  val id: String,
  val feedItemId: String,
  val authorId: String,
  val authorName: String,
  val authorFaculty: String,
  val authorColorHex: Long,
  val content: String,
  val timestamp: Long
)

enum class MessageType {
  TEXT,
  IMAGE,
  LOCATION,
  STUDY_INVITE
}

data class ChatMessage(
  val id: String,
  val conversationId: String = "", // groupId or direct conversation identifier
  val senderId: String,
  val senderName: String,
  val receiverId: String? = null, // null for group message
  val content: String,
  val timestamp: Long,
  val isRead: Boolean = false,
  val readBy: Set<String> = emptySet(),
  val messageType: MessageType = MessageType.TEXT,
  val reactions: Map<String, String> = emptyMap() // userId -> emoji
)

data class ChatGroup(
  val id: String,
  val name: String,
  val description: String,
  val iconEmoji: String = "🔥",
  val creatorId: String,
  val memberIds: Set<String>,
  val createdAt: Long,
  val lastMessageText: String? = null,
  val lastMessageTimestamp: Long = 0L,
  val category: String = "Campus General" // e.g. "B.Tech CSE", "Hackathon 2026", "Hostel 3", "Sports"
)

enum class NotificationType {
  CHAT_MESSAGE,
  POST_REPLY,
  UPCOMING_EVENT,
  NEW_POLL,
  CAMPUS_NOTICE,
  PARTNER_REQUEST,
  POST_LIKE
}

data class NotificationItem(
  val id: String,
  val userId: String,
  val title: String,
  val message: String,
  val timestamp: Long,
  val type: NotificationType,
  val isRead: Boolean = false,
  val targetId: String? = null,
  val senderName: String? = null,
  val senderAvatarHex: Long? = null
)

data class NotificationConfig(
  val pushNotificationsEnabled: Boolean = true,
  val chatMessages: Boolean = true,
  val postReplies: Boolean = true,
  val upcomingEvents: Boolean = true,
  val newPolls: Boolean = true,
  val universityNotices: Boolean = true,
  val soundAndVibration: Boolean = true
)

enum class ReportTargetType {
  POST,
  USER,
  POLL,
  EVENT,
  MESSAGE
}

data class ReportItem(
  val id: String,
  val targetType: ReportTargetType,
  val targetId: String,
  val reason: String,
  val reporterId: String,
  val reporterName: String,
  val timestamp: Long = System.currentTimeMillis(),
  val status: String = "PENDING"
)

data class FeedbackItem(
  val id: String,
  val userId: String,
  val userName: String,
  val userCollege: String,
  val subject: String,
  val message: String,
  val timestamp: Long = System.currentTimeMillis()
)

data class AdminStats(
  val totalUsers: Int,
  val usersByCollege: Map<String, Int>,
  val activePosts: Int,
  val activePolls: Int,
  val activeEvents: Int,
  val partnerRequests: Int,
  val lostFoundItems: Int,
  val totalReports: Int,
  val totalFeedback: Int = 0,
  val usersByGender: Map<String, Int> = emptyMap()
)
