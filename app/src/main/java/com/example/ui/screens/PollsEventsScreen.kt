package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Event
import com.example.data.model.Poll
import com.example.data.model.StudentProfile
import com.example.ui.components.CreateEventDialog
import com.example.ui.components.CreatePollDialog
import com.example.ui.components.EventCard
import com.example.ui.components.PollCard

@Composable
fun PollsEventsScreen(
  polls: List<Poll>,
  events: List<Event>,
  currentUser: StudentProfile,
  onVotePoll: (pollId: String, optionIndex: Int) -> Unit,
  onCreatePoll: (question: String, options: List<String>, category: String) -> Unit,
  onToggleEventRsvp: (eventId: String) -> Unit,
  onCreateEvent: (title: String, desc: String, cat: String, date: String, time: String, venue: String, org: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showCreatePollDialog by remember { mutableStateOf(false) }
  var showCreateEventDialog by remember { mutableStateOf(false) }

  var eventFilterMyRsvps by remember { mutableStateOf(false) }

  Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.HowToVote, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
              Text("Polls (${polls.size})", fontWeight = FontWeight.SemiBold)
            }
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
              Text("Events (${events.size})", fontWeight = FontWeight.SemiBold)
            }
          }
        )
      }

      when (selectedTab) {
        0 -> {
          // Polls Tab
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("polls_tab_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Text(
                text = "Real-time Campus Democracy",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Vote on university facilities, fests, academic policies, and student welfare.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Spacer(modifier = Modifier.height(6.dp))
            }

            if (polls.isEmpty()) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                  Text(
                    text = "No polls currently active.",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            } else {
              items(polls, key = { it.id }) { poll ->
                PollCard(
                  poll = poll,
                  currentUserId = currentUser.id,
                  onVote = { optIdx -> onVotePoll(poll.id, optIdx) },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
          }
        }

        1 -> {
          // Events Tab
          val displayedEvents = if (eventFilterMyRsvps) {
            events.filter { it.isRegistered(currentUser.id) }
          } else {
            events
          }

          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .testTag("events_tab_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
          ) {
            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "MGUG Campus Events",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Symposiums, hackathons, medical camps & sports meets.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                  selected = !eventFilterMyRsvps,
                  onClick = { eventFilterMyRsvps = false },
                  label = { Text("All Events") }
                )
                FilterChip(
                  selected = eventFilterMyRsvps,
                  onClick = { eventFilterMyRsvps = true },
                  label = { Text("My RSVPs (${events.count { it.isRegistered(currentUser.id) }})") }
                )
              }
            }

            if (displayedEvents.isEmpty()) {
              item {
                Card(
                  modifier = Modifier.fillMaxWidth(),
                  shape = RoundedCornerShape(16.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                  Text(
                    text = if (eventFilterMyRsvps) "You haven't RSVP'd to any events yet." else "No events listed.",
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            } else {
              items(displayedEvents, key = { it.id }) { event ->
                EventCard(
                  event = event,
                  currentUserId = currentUser.id,
                  onToggleRsvp = { onToggleEventRsvp(event.id) },
                  modifier = Modifier.fillMaxWidth()
                )
              }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
          }
        }
      }
    }

    // FAB based on active tab
    ExtendedFloatingActionButton(
      onClick = {
        if (selectedTab == 0) showCreatePollDialog = true
        else showCreateEventDialog = true
      },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("polls_events_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White
    ) {
      Icon(Icons.Default.Add, contentDescription = null)
      Spacer(modifier = Modifier.width(6.dp))
      Text(if (selectedTab == 0) "+ New Poll" else "+ New Event", fontWeight = FontWeight.Bold)
    }

    if (showCreatePollDialog) {
      CreatePollDialog(
        onDismiss = { showCreatePollDialog = false },
        onCreatePoll = onCreatePoll
      )
    }

    if (showCreateEventDialog) {
      CreateEventDialog(
        onDismiss = { showCreateEventDialog = false },
        onCreateEvent = { t, d, c, dt, tm, v, o ->
          onCreateEvent(t, d, c, dt, tm, v, o)
        }
      )
    }
  }
}
