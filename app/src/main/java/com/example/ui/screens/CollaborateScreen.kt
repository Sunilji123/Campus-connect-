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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LostFoundType
import com.example.data.model.StudentProfile
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.FeedCard

@Composable
fun CollaborateScreen(
  feedItems: List<FeedItem>,
  currentUser: StudentProfile,
  onLikeClick: (String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  onMarkResolved: (String) -> Unit,
  onDeletePost: (String) -> Unit,
  onCreateStudyPost: (subject: String, target: String, hours: String, loc: String, note: String) -> Unit,
  onCreateProjectPost: (title: String, stack: String, roles: String, deadline: String, note: String) -> Unit,
  onCreateLostFoundPost: (type: LostFoundType, loc: String, contact: String, note: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showCreateDialog by remember { mutableStateOf(false) }

  val studyPartners = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.STUDY_PARTNER }
  }
  val projectPartners = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.PROJECT_PARTNER }
  }
  val lostAndFound = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.LOST_FOUND }
  }

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
              Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
              Text("Study (${studyPartners.size})", fontWeight = FontWeight.SemiBold)
            }
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
              Text("Projects (${projectPartners.size})", fontWeight = FontWeight.SemiBold)
            }
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.padding(end = 4.dp))
              Text("Lost/Found (${lostAndFound.size})", fontWeight = FontWeight.SemiBold)
            }
          }
        )
      }

      val activeList = when (selectedTab) {
        0 -> studyPartners
        1 -> projectPartners
        else -> lostAndFound
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("collaborate_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        item {
          val (heading, sub) = when (selectedTab) {
            0 -> "Find Study Partners" to "Group study in library, exam preparation & subject peer tutoring."
            1 -> "Join Project & Capstone Teams" to "Find coders, designers, lab partners, and healthcare advisors."
            else -> "Campus Lost & Found Desk" to "Report or recover lost items across university faculties & hostels."
          }

          Column {
            Text(
              text = heading,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = sub,
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        if (activeList.isEmpty()) {
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
              Text(
                text = "No listings here yet. Tap the button below to add one!",
                modifier = Modifier.padding(20.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        } else {
          items(activeList, key = { it.id }) { item ->
            FeedCard(
              item = item,
              currentUser = currentUser,
              onLikeClick = { onLikeClick(item.id) },
              onCommentClick = {},
              onPartnerActionClick = { onNavigateToChat(item.authorId) },
              onMarkResolvedClick = { onMarkResolved(item.id) },
              onDeleteClick = { onDeletePost(item.id) },
              onReportClick = {}
            )
          }
        }

        item { Spacer(modifier = Modifier.height(70.dp)) }
      }
    }

    ExtendedFloatingActionButton(
      onClick = { showCreateDialog = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("collaborate_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White
    ) {
      Icon(Icons.Default.Add, contentDescription = null)
      Spacer(modifier = Modifier.width(6.dp))
      val label = when (selectedTab) {
        0 -> "+ Request Partner"
        1 -> "+ Recruit Team"
        else -> "+ Report Item"
      }
      Text(label, fontWeight = FontWeight.Bold)
    }

    if (showCreateDialog) {
      CreatePostDialog(
        currentUser = currentUser,
        onDismiss = { showCreateDialog = false },
        onCreateGeneral = {},
        onCreateStudyPartner = onCreateStudyPost,
        onCreateProjectPartner = onCreateProjectPost,
        onCreateLostFound = onCreateLostFoundPost
      )
    }
  }
}
