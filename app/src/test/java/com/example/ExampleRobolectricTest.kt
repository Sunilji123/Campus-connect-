package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.FeedItemType
import com.example.data.repository.MgugRealtimeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read app name from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Campus Connect", appName)
  }

  @Test
  fun `repository contains seeded feed items and polls`() {
    val items = MgugRealtimeRepository.feedItems.value
    val polls = MgugRealtimeRepository.polls.value
    val events = MgugRealtimeRepository.events.value

    assertTrue("Feed items should not be empty", items.isNotEmpty())
    assertTrue("Polls should not be empty", polls.isNotEmpty())
    assertTrue("Events should not be empty", events.isNotEmpty())
  }

  @Test
  fun `voting on a poll increments votes and sets user vote`() {
    val poll = MgugRealtimeRepository.polls.value.first()
    val initialVotes = poll.totalVotes
    val currentUser = MgugRealtimeRepository.currentUser.value

    MgugRealtimeRepository.votePoll(poll.id, 0)

    val updatedPoll = MgugRealtimeRepository.polls.value.first { it.id == poll.id }
    assertTrue("User should have voted", updatedPoll.hasVoted(currentUser.id))
    assertEquals("Total votes should increment by 1", initialVotes + 1, updatedPoll.totalVotes)
  }

  @Test
  fun `creating general post publishes to feed`() {
    val initialCount = MgugRealtimeRepository.feedItems.value.size
    MgugRealtimeRepository.createGeneralPost("Robolectric Test Campus Post")

    val updatedItems = MgugRealtimeRepository.feedItems.value
    assertEquals("Feed should have 1 more item", initialCount + 1, updatedItems.size)
    assertEquals("Latest item should match created content", "Robolectric Test Campus Post", updatedItems.first().content)
  }

  @Test
  fun `toggling like updates like set and count`() {
    val currentUser = MgugRealtimeRepository.currentUser.value
    val item = MgugRealtimeRepository.feedItems.value.first()
    val wasLiked = item.likes.contains(currentUser.id)
    val initialLikes = item.likeCount

    MgugRealtimeRepository.toggleLike(item.id)

    val updatedItem = MgugRealtimeRepository.feedItems.value.first { it.id == item.id }
    val isNowLiked = updatedItem.likes.contains(currentUser.id)

    assertEquals("Like state should toggle", !wasLiked, isNowLiked)
    val expectedCount = if (wasLiked) initialLikes - 1 else initialLikes + 1
    assertEquals("Like count should reflect toggle", expectedCount, updatedItem.likeCount)
  }
}
