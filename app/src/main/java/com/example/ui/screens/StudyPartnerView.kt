package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.StudentProfile
import com.example.ui.components.campusAmbientMesh
import com.example.ui.util.FormatUtils

/**
 * Dedicated Study Partner Screen:
 * - Subject, Topic, College, Course, Department
 * - Availability & Preferred study method
 * - Interest/skill tags
 * - Connect / Join Study Squad button
 * - Realtime updates
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPartnerView(
  feedItems: List<FeedItem>,
  currentUser: StudentProfile,
  onBack: () -> Unit,
  onCreateStudyPost: (subject: String, target: String, hours: String, loc: String, note: String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedSubjectFilter by remember { mutableStateOf("All") }
  var showCreateDialog by remember { mutableStateOf(false) }

  val studyItems = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.STUDY_PARTNER }
  }

  val filteredItems = remember(studyItems, selectedSubjectFilter) {
    if (selectedSubjectFilter == "All") studyItems
    else studyItems.filter { it.subject?.contains(selectedSubjectFilter, ignoreCase = true) == true }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("study_partner_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          Column {
            Text(
              text = "Study Partner & Exam Squads",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${studyItems.size} active study requests",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showCreateDialog = true },
        containerColor = Color(0xFF2E7D32),
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_create_study_partner")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Find Study Squad", fontWeight = FontWeight.Bold)
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .campusAmbientMesh()
        .padding(padding)
    ) {
      // Subject quick filter row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "Algorithms", "DBMS", "OS", "Maths", "AI/ML").forEach { subject ->
          FilterChip(
            selected = selectedSubjectFilter == subject,
            onClick = { selectedSubjectFilter = subject },
            label = { Text(subject, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }

      if (filteredItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF2E7D32).copy(alpha = 0.12f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "No Study Requests",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Create a request to find peers preparing for end-sems, GATE, or campus interviews.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                  androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(
                      Color(0xFF004D40), // Dark Teal
                      Color(0xFF00796B), // Teal
                      Color(0xFF2E7D32), // Emerald Green
                      Color(0xFF66BB6A)  // Light Green
                    )
                  )
                )
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.28f)
                  ) {
                    Text(
                      text = "🎓 MID-SEM & EXAM SQUADS",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Ace Your Exams Together.",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Connect with peers from your course & department for focused group study.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text("📚", fontSize = 28.sp)
                }
              }
            }
          }

          items(filteredItems, key = { it.id }) { item ->
            StudyPartnerCard(
              item = item,
              currentUser = currentUser,
              onConnectClick = { onNavigateToChat(item.authorId) }
            )
          }
          item {
            Spacer(modifier = Modifier.height(70.dp))
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    CreateStudyPartnerDialog(
      currentUser = currentUser,
      onDismiss = { showCreateDialog = false },
      onSubmit = { subject, target, hours, loc, note ->
        onCreateStudyPost(subject, target, hours, loc, note)
        showCreateDialog = false
      }
    )
  }
}

@Composable
private fun StudyPartnerCard(
  item: FeedItem,
  currentUser: StudentProfile,
  onConnectClick: () -> Unit
) {
  val isAuthor = item.authorId == currentUser.id

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("study_partner_card_${item.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(Color(item.authorColorHex), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.authorName.take(1).uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.authorName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = "${item.authorFaculty} · ${item.authorYear}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF2E7D32).copy(alpha = 0.12f)
        ) {
          Text(
            text = "📚 Study Squad",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF2E7D32),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Subject Banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = item.subject ?: "General Course Study",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSecondaryContainer
          )
          if (!item.targetExam.isNullOrBlank()) {
            Text(
              text = "Target: ${item.targetExam}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Description
      Text(
        text = item.content,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Timing & Location
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        if (!item.studyHours.isNullOrBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(13.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(item.studyHours, style = MaterialTheme.typography.labelSmall)
            }
          }
        }

        if (!item.studyLocation.isNullOrBlank()) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "📍 ${item.studyLocation}",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Connect Button
      if (!isAuthor) {
        Button(
          onClick = onConnectClick,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_connect_study_${item.id}")
        ) {
          Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Connect · Study Together", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun CreateStudyPartnerDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (subject: String, target: String, hours: String, loc: String, note: String) -> Unit
) {
  var subject by remember { mutableStateOf("") }
  var targetExam by remember { mutableStateOf("") }
  var hours by remember { mutableStateOf("") }
  var location by remember { mutableStateOf("Campus Library / Online") }
  var note by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_create_study_partner")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Create Study Partner Request",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = subject,
          onValueChange = { subject = it },
          label = { Text("Subject / Course") },
          placeholder = { Text("e.g. Data Structures & Algorithms, Machine Learning") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_study_subject")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = targetExam,
          onValueChange = { targetExam = it },
          label = { Text("Topic / Target Exam") },
          placeholder = { Text("e.g. End-Sem Exam, GATE CS, Placement Prep") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_study_target")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = hours,
          onValueChange = { hours = it },
          label = { Text("Preferred Hours / Availability") },
          placeholder = { Text("e.g. Evenings 6-8 PM, Weekends") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_study_hours")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = location,
          onValueChange = { location = it },
          label = { Text("Preferred Spot / Method") },
          placeholder = { Text("e.g. Central Library, Discussion Room, Discord") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_study_loc")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Short Description & Goals") },
          placeholder = { Text("Explain what you want to study together…") },
          minLines = 3,
          maxLines = 4,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_study_note")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (subject.isNotBlank() && note.isNotBlank()) {
                onSubmit(subject, targetExam, hours, location, note)
              }
            },
            enabled = subject.isNotBlank() && note.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            modifier = Modifier.testTag("btn_submit_study_partner")
          ) {
            Text("Publish Request", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
