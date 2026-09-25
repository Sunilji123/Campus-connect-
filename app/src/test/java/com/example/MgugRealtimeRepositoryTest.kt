package com.example

import com.example.data.model.LostFoundStatus
import com.example.data.model.LostFoundType
import com.example.data.model.NotificationConfig
import com.example.data.model.NotificationType
import com.example.data.model.VerificationMethod
import com.example.data.model.VerificationStatus
import com.example.data.repository.MgugRealtimeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MgugRealtimeRepositoryTest {

  @Test
  fun testInitialProfilesAndCurrentUser() {
    val current = MgugRealtimeRepository.currentUser.value
    assertNotNull(current)
    assertEquals("user_aarav", current.id)
    assertEquals(VerificationStatus.VERIFIED, current.verificationStatus)
    assertTrue(current.isVerified)
  }

  @Test
  fun testDirectAndGroupMessaging() {
    val sender = MgugRealtimeRepository.currentUser.value
    val targetPeer = MgugRealtimeRepository.STUDENT_PRIYA.id

    // Send direct message
    MgugRealtimeRepository.sendDirectMessage(targetPeer, "Hey Priya, testing real-time messaging!")
    val conv = MgugRealtimeRepository.getDirectConversation(sender.id, targetPeer)
    assertTrue(conv.isNotEmpty())
    assertEquals("Hey Priya, testing real-time messaging!", conv.last().content)

    // Create group squad chat
    val group = MgugRealtimeRepository.createGroupChat(
      name = "AI Research Squad",
      description = "Campus NLP and Vision research",
      category = "Tech & Innovation",
      iconEmoji = "⚡",
      initialMemberIds = setOf(targetPeer)
    )
    assertNotNull(group)
    assertTrue(group.memberIds.contains(sender.id))
    assertTrue(group.memberIds.contains(targetPeer))

    // Send group message
    MgugRealtimeRepository.sendGroupMessage(group.id, "Welcome team!")
    val groupMessages = MgugRealtimeRepository.getGroupConversation(group.id)
    assertTrue(groupMessages.any { it.content == "Welcome team!" })
  }

  @Test
  fun testNotificationConfigAndDismissal() {
    val config = NotificationConfig(
      pushNotificationsEnabled = true,
      chatMessages = true,
      postReplies = false,
      upcomingEvents = true
    )
    MgugRealtimeRepository.updateNotificationConfig(config)
    assertEquals(false, MgugRealtimeRepository.notificationConfig.value.postReplies)

    // Test notification delivery and dismissal
    val initialCount = MgugRealtimeRepository.notifications.value.size
    val firstNotifId = MgugRealtimeRepository.notifications.value.firstOrNull()?.id
    if (firstNotifId != null) {
      MgugRealtimeRepository.dismissNotification(firstNotifId)
      assertEquals(initialCount - 1, MgugRealtimeRepository.notifications.value.size)
    }
  }

  @Test
  fun testProfileUpdateAndStudentVerification() {
    MgugRealtimeRepository.updateProfile(
      name = "Aarav Sharma",
      course = "B.Tech CSE (AI & Data Science)",
      year = "3rd Year (Sem 6)",
      bio = "Updated bio for testing",
      statusEmoji = "🚀",
      statusText = "Building next gen campus tech",
      interests = listOf("Android", "Compose", "Kotlin"),
      skills = listOf("Kotlin", "Git")
    )
    val updated = MgugRealtimeRepository.currentUser.value
    assertEquals("B.Tech CSE (AI & Data Science)", updated.course)
    assertEquals("🚀", updated.statusEmoji)

    // Test ID card verification
    val verified = MgugRealtimeRepository.submitVerificationRequest(
      method = VerificationMethod.COLLEGE_EMAIL_OTP,
      inputData = "aarav.cse@mgug.ac.in"
    )
    assertTrue(verified)
    assertEquals(VerificationStatus.VERIFIED, MgugRealtimeRepository.currentUser.value.verificationStatus)
  }

  @Test
  fun testLostAndFoundPermanentActiveAndOwnerDeletePermission() {
    // 1. User A uploads a Found Item
    val userA = MgugRealtimeRepository.STUDENT_AARAV
    MgugRealtimeRepository.switchUser(userA)
    val postId = MgugRealtimeRepository.createLostFoundPost(
      type = LostFoundType.FOUND,
      itemName = "Black Leather Wallet",
      description = "Found near Central Library reading hall, contains college ID card.",
      category = "👛 Wallet",
      campusLocation = "Central Library 2nd Floor",
      itemDate = "Today, 11:30 AM"
    )
    assertTrue(postId.isNotBlank())

    // 2. User B can see it
    val userB = MgugRealtimeRepository.STUDENT_ROHAN
    MgugRealtimeRepository.switchUser(userB)
    var feed = MgugRealtimeRepository.feedItems.value
    var post = feed.firstOrNull { it.id == postId }
    assertNotNull("User B must see the uploaded item in feed", post)
    assertEquals(LostFoundStatus.ACTIVE, post!!.lostFoundStatus)
    assertEquals("Black Leather Wallet", post.itemName)

    // 3. User C registers / switches user and can still see the active item (no disappearance)
    val userC = MgugRealtimeRepository.STUDENT_PRIYA
    MgugRealtimeRepository.switchUser(userC)
    feed = MgugRealtimeRepository.feedItems.value
    post = feed.firstOrNull { it.id == postId }
    assertNotNull("User C must see existing active item even though joining later", post)
    assertEquals(LostFoundStatus.ACTIVE, post!!.lostFoundStatus)

    // 4. User B cannot delete User A's post (Backend permission enforcement)
    MgugRealtimeRepository.switchUser(userB)
    val deleteAttemptB = MgugRealtimeRepository.deletePost(postId)
    assertFalse("User B must NOT be allowed to delete User A's post", deleteAttemptB)
    assertTrue("Post must still exist after unauthorized delete attempt by User B",
      MgugRealtimeRepository.feedItems.value.any { it.id == postId })

    // 5. User C cannot delete User A's post (Backend permission enforcement)
    MgugRealtimeRepository.switchUser(userC)
    val deleteAttemptC = MgugRealtimeRepository.deletePost(postId)
    assertFalse("User C must NOT be allowed to delete User A's post", deleteAttemptC)
    assertTrue("Post must still exist after unauthorized delete attempt by User C",
      MgugRealtimeRepository.feedItems.value.any { it.id == postId })

    // 6. User A (owner) can mark it Found/Returned
    MgugRealtimeRepository.switchUser(userA)
    val markStatusResult = MgugRealtimeRepository.markLostFoundStatus(postId, LostFoundStatus.FOUND_RETURNED)
    assertTrue("User A should be able to mark post status", markStatusResult)
    val updatedPost = MgugRealtimeRepository.feedItems.value.firstOrNull { it.id == postId }
    assertNotNull(updatedPost)
    assertEquals(LostFoundStatus.FOUND_RETURNED, updatedPost!!.lostFoundStatus)
    assertTrue("Post should be marked isResolved", updatedPost.isResolved)

    // 7. User A can edit their post
    val editResult = MgugRealtimeRepository.updateLostFoundPost(
      postId = postId,
      itemName = "Black Leather Wallet (Returned to owner)",
      description = "Handed over to librarian desk.",
      category = "👛 Wallet",
      campusLocation = "Central Library Librarian Desk",
      itemDate = "Today, 12:00 PM",
      type = LostFoundType.FOUND,
      status = LostFoundStatus.FOUND_RETURNED
    )
    assertTrue("User A should be able to edit their post", editResult)

    // 8. User A (owner) can delete their own post
    val deleteAttemptA = MgugRealtimeRepository.deletePost(postId)
    assertTrue("User A must be allowed to delete their own post", deleteAttemptA)
    assertFalse("Post must be completely removed from repository after owner deletion",
      MgugRealtimeRepository.feedItems.value.any { it.id == postId })
  }
}
