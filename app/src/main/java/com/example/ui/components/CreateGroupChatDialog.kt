package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.model.StudentProfile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateGroupChatDialog(
  currentUser: StudentProfile,
  allProfiles: List<StudentProfile>,
  onDismiss: () -> Unit,
  onCreateGroup: (name: String, description: String, category: String, iconEmoji: String, memberIds: Set<String>) -> Unit
) {
  var groupName by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("Tech & Innovation") }
  var selectedEmoji by remember { mutableStateOf("🔥") }

  val otherStudents = remember(allProfiles, currentUser) {
    allProfiles.filter { it.id != currentUser.id }
  }
  var selectedMembers by remember { mutableStateOf(otherStudents.map { it.id }.toSet()) }

  val categories = listOf("Tech & Innovation", "Academic Squads", "Campus Life", "Hostel & Mess", "Sports & Fitness")
  val emojiOptions = listOf("🔥", "⚡", "🚀", "💻", "📚", "🔬", "☕", "🩺", "🌿", "🎮", "🏀")

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = {
          if (groupName.isNotBlank()) {
            onCreateGroup(groupName, description, selectedCategory, selectedEmoji, selectedMembers)
            onDismiss()
          }
        },
        enabled = groupName.isNotBlank(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF651FFF)),
        modifier = Modifier.testTag("confirm_create_group_button")
      ) {
        Text("Create Squad Chat 🚀", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Cancel")
      }
    },
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Group,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "New Student Group Chat",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Connect with peers for group assignments, hackathon squads, or hostel updates.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Emoji picker row
        Column {
          Text("Squad Emoji Icon", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            emojiOptions.take(7).forEach { emoji ->
              val isSelected = emoji == selectedEmoji
              Surface(
                shape = CircleShape,
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .clickable { selectedEmoji = emoji }
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = emoji, fontSize = 16.sp)
                }
              }
            }
          }
        }

        OutlinedTextField(
          value = groupName,
          onValueChange = { groupName = it },
          label = { Text("Squad / Group Name *") },
          placeholder = { Text("e.g. AI Hackathon 2026 Core") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("group_name_input"),
          singleLine = true,
          shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Topic & Purpose") },
          placeholder = { Text("Share notes, meetups, resources") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          minLines = 2
        )

        // Member selection
        Column {
          Text(
            text = "Add Verified MGUG Peers (${selectedMembers.size} selected)",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))

          otherStudents.forEach { peer ->
            val isChecked = selectedMembers.contains(peer.id)
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clickable {
                  selectedMembers = if (isChecked) selectedMembers - peer.id else selectedMembers + peer.id
                },
              shape = RoundedCornerShape(10.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (isChecked) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
              )
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .background(Color(peer.avatarColorHex), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = peer.name.take(1),
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = peer.name,
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                      text = peer.course,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                      color = MaterialTheme.colorScheme.outline
                    )
                  }
                }

                if (isChecked) {
                  Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      }
    }
  )
}
