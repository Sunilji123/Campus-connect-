package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.data.model.Comment
import com.example.data.model.FeedItem
import com.example.data.model.LostFoundType
import com.example.data.model.StudentProfile
import com.example.ui.util.FormatUtils

// 1. Create Post Dialog with Tabs
@Composable
fun CreatePostDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onCreateGeneral: (content: String) -> Unit,
  onCreateStudyPartner: (subject: String, targetExam: String, hours: String, location: String, note: String) -> Unit,
  onCreateProjectPartner: (title: String, techStack: String, roles: String, deadline: String, note: String) -> Unit,
  onCreateLostFound: (type: LostFoundType, location: String, contact: String, note: String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  val tabs = listOf("General", "Study Partner", "Project Team", "Lost/Found")

  // Common content
  var generalContent by remember { mutableStateOf("") }

  // Study Partner fields
  var studySubject by remember { mutableStateOf("") }
  var studyTargetExam by remember { mutableStateOf("") }
  var studyHours by remember { mutableStateOf("") }
  var studyLocation by remember { mutableStateOf("Campus Central Library") }
  var studyNote by remember { mutableStateOf("") }

  // Project fields
  var projectTitle by remember { mutableStateOf("") }
  var projectStack by remember { mutableStateOf("") }
  var projectRoles by remember { mutableStateOf("") }
  var projectDeadline by remember { mutableStateOf("") }
  var projectNote by remember { mutableStateOf("") }

  // Lost & Found fields
  var lostFoundType by remember { mutableStateOf(LostFoundType.LOST) }
  var lfLocation by remember { mutableStateOf("") }
  var lfContact by remember { mutableStateOf("") }
  var lfNote by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("create_post_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Create MGUG Feed Post",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        TabRow(selectedTabIndex = selectedTab) {
          tabs.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = { Text(text = title, fontSize = 11.sp, maxLines = 1) }
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Content per tab
        when (selectedTab) {
          0 -> {
            // General
            OutlinedTextField(
              value = generalContent,
              onValueChange = { generalContent = it },
              label = { Text("What's on your mind at MGUG?") },
              placeholder = { Text("Ask questions, share advice, discuss campus news...") },
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .testTag("post_general_input"),
              shape = RoundedCornerShape(12.dp)
            )
          }
          1 -> {
            // Study Partner
            OutlinedTextField(
              value = studySubject,
              onValueChange = { studySubject = it },
              label = { Text("Course / Subject (e.g. Pharmacology II)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = studyTargetExam,
              onValueChange = { studyTargetExam = it },
              label = { Text("Target Exam / Goal (e.g. Mid-Sem Finals)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = studyHours,
              onValueChange = { studyHours = it },
              label = { Text("Timings (e.g. Weekdays 4:30 PM - 6:30 PM)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = studyNote,
              onValueChange = { studyNote = it },
              label = { Text("Description & expectations") },
              modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
              shape = RoundedCornerShape(10.dp)
            )
          }
          2 -> {
            // Project Partner
            OutlinedTextField(
              value = projectTitle,
              onValueChange = { projectTitle = it },
              label = { Text("Project Title") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = projectStack,
              onValueChange = { projectStack = it },
              label = { Text("Tech Stack / Domain (e.g. Kotlin, IoT, UI/UX)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = projectRoles,
              onValueChange = { projectRoles = it },
              label = { Text("Roles Needed (e.g. Android Dev, Content Writer)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = projectNote,
              onValueChange = { projectNote = it },
              label = { Text("Project summary & goal") },
              modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
              shape = RoundedCornerShape(10.dp)
            )
          }
          3 -> {
            // Lost & Found
            Row(verticalAlignment = Alignment.CenterVertically) {
              FilterChip(
                selected = lostFoundType == LostFoundType.LOST,
                onClick = { lostFoundType = LostFoundType.LOST },
                label = { Text("I LOST Something") }
              )
              Spacer(modifier = Modifier.width(8.dp))
              FilterChip(
                selected = lostFoundType == LostFoundType.FOUND,
                onClick = { lostFoundType = LostFoundType.FOUND },
                label = { Text("I FOUND Something") }
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = lfLocation,
              onValueChange = { lfLocation = it },
              label = { Text("Campus Location (e.g. Central Library 2nd Floor)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = lfContact,
              onValueChange = { lfContact = it },
              label = { Text("How to contact/claim (e.g. Leave note or Room 204)") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
              value = lfNote,
              onValueChange = { lfNote = it },
              label = { Text("Item description (Color, Brand, features)") },
              modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit Button
        Button(
          onClick = {
            when (selectedTab) {
              0 -> if (generalContent.isNotBlank()) onCreateGeneral(generalContent)
              1 -> if (studySubject.isNotBlank()) {
                onCreateStudyPartner(
                  studySubject,
                  studyTargetExam.ifBlank { "Mid-Sem Exams" },
                  studyHours.ifBlank { "Flexible evenings" },
                  studyLocation,
                  studyNote.ifBlank { "Looking for sincere study partners to revise and discuss notes." }
                )
              }
              2 -> if (projectTitle.isNotBlank()) {
                onCreateProjectPartner(
                  projectTitle,
                  projectStack.ifBlank { "Open" },
                  projectRoles.ifBlank { "Team Member" },
                  projectDeadline.ifBlank { "End of Semester" },
                  projectNote.ifBlank { "Exciting project for MGUG students, let's build together!" }
                )
              }
              3 -> if (lfNote.isNotBlank()) {
                onCreateLostFound(
                  lostFoundType,
                  lfLocation.ifBlank { "MGUG Campus" },
                  lfContact.ifBlank { "Reply via Campus Connect chat" },
                  lfNote
                )
              }
            }
            onDismiss()
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .testTag("submit_post_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Publish Instantly to Realtime Feed", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

// 2. Create Poll Dialog
@Composable
fun CreatePollDialog(
  onDismiss: () -> Unit,
  onCreatePoll: (question: String, options: List<String>, category: String) -> Unit
) {
  var question by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Campus Life") }
  val options = remember { mutableStateListOf("Yes", "No") }

  val categories = listOf("Campus Life", "Academics", "Fest & Events", "Hostel & Mess", "Sports")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Create Student Poll",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = question,
          onValueChange = { question = it },
          label = { Text("Poll Question") },
          placeholder = { Text("e.g. Should hostel library hours be extended?") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("create_poll_question_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Options (2-5 options):",
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )

        Spacer(modifier = Modifier.height(6.dp))

        options.forEachIndexed { index, optionText ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = optionText,
              onValueChange = { options[index] = it },
              label = { Text("Option ${index + 1}") },
              modifier = Modifier.weight(1f),
              singleLine = true,
              shape = RoundedCornerShape(10.dp)
            )
            if (options.size > 2) {
              IconButton(onClick = { options.removeAt(index) }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove Option", tint = Color.Gray)
              }
            }
          }
        }

        if (options.size < 5) {
          TextButton(
            onClick = { options.add("") },
            modifier = Modifier.align(Alignment.End)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add Option")
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val validOptions = options.filter { it.isNotBlank() }
          if (question.isNotBlank() && validOptions.size >= 2) {
            onCreatePoll(question, validOptions, category)
            onDismiss()
          }
        },
        enabled = question.isNotBlank() && options.count { it.isNotBlank() } >= 2,
        modifier = Modifier.testTag("confirm_create_poll")
      ) {
        Text("Start Live Poll")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

// 3. Create Event Dialog
@Composable
fun CreateEventDialog(
  onDismiss: () -> Unit,
  onCreateEvent: (title: String, desc: String, category: String, date: String, time: String, venue: String, org: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Tech & Innovation") }
  var date by remember { mutableStateOf("Oct 15, 2026") }
  var time by remember { mutableStateOf("10:00 AM - 4:00 PM") }
  var venue by remember { mutableStateOf("Main Auditorium, MGUG") }
  var organizer by remember { mutableStateOf("Student Council") }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Add Campus Event",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Event Title") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = date,
          onValueChange = { date = it },
          label = { Text("Date (e.g. Oct 15, 2026)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = time,
          onValueChange = { time = it },
          label = { Text("Time (e.g. 10:00 AM - 4:00 PM)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = venue,
          onValueChange = { venue = it },
          label = { Text("Venue / Room") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Event Description") },
          modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
          shape = RoundedCornerShape(10.dp)
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onCreateEvent(title, desc, category, date, time, venue, organizer)
            onDismiss()
          }
        },
        enabled = title.isNotBlank()
      ) {
        Text("Schedule Event")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}

// 4. Comments Bottom Sheet (Real-time Comments)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentsBottomSheet(
  post: FeedItem,
  comments: List<Comment>,
  currentUser: StudentProfile,
  onAddComment: (String) -> Unit,
  onDismiss: () -> Unit
) {
  var commentInput by remember { mutableStateOf("") }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    modifier = Modifier.testTag("comments_bottom_sheet")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp)
        .padding(bottom = 24.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Comments (${comments.size})",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Comments list
      if (comments.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = "No comments yet. Start the conversation!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      } else {
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(comments, key = { it.id }) { comment ->
            Row(modifier = Modifier.fillMaxWidth()) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .background(Color(comment.authorColorHex), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = comment.authorName.take(1),
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                  .padding(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = comment.authorName,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = FormatUtils.formatRelativeTime(comment.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                  )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                  text = comment.content,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Comment input
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = commentInput,
          onValueChange = { commentInput = it },
          placeholder = { Text("Write a comment as ${currentUser.name.split(" ").first()}...") },
          modifier = Modifier
            .weight(1f)
            .testTag("comment_input_field"),
          shape = RoundedCornerShape(20.dp),
          singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(
          onClick = {
            if (commentInput.isNotBlank()) {
              onAddComment(commentInput)
              commentInput = ""
            }
          },
          enabled = commentInput.isNotBlank(),
          modifier = Modifier.testTag("send_comment_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send Comment",
            tint = if (commentInput.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
          )
        }
      }
    }
  }
}

// 5. Report Dialog (Community safety & moderation)
@Composable
fun ReportDialog(
  postId: String,
  onDismiss: () -> Unit,
  onConfirmReport: (reason: String) -> Unit
) {
  val reasons = listOf(
    "Irrelevant or not related to MGUG campus",
    "Harassment, abuse or hate speech",
    "Commercial spam or unauthorized promotion",
    "False or misleading information",
    "Dating app behavior (violates 18+ academic community policy)"
  )
  var selectedReason by remember { mutableStateOf(reasons.first()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    icon = {
      Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
    },
    title = {
      Text(text = "Report Content to MGUG Moderation", fontWeight = FontWeight.Bold)
    },
    text = {
      Column {
        Text(
          text = "Help keep Campus Connect safe and educational. Why are you reporting this post?",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        reasons.forEach { reason ->
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { selectedReason = reason }
              .padding(vertical = 4.dp)
          ) {
            RadioButton(
              selected = selectedReason == reason,
              onClick = { selectedReason = reason }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = reason, style = MaterialTheme.typography.bodySmall)
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onConfirmReport(selectedReason)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
      ) {
        Text("Submit Report")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) { Text("Cancel") }
    }
  )
}
