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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSave: (
    name: String,
    course: String,
    year: String,
    bio: String,
    statusEmoji: String,
    statusText: String,
    interests: List<String>,
    skills: List<String>,
    avatarColorHex: Long,
    collegeName: String,
    department: String,
    age: Int,
    gender: String,
    instagramHandle: String,
    snapchatHandle: String,
    connectionPreferences: List<String>
  ) -> Unit
) {
  var name by remember { mutableStateOf(currentUser.name) }
  var collegeName by remember { mutableStateOf(currentUser.collegeName) }
  var department by remember { mutableStateOf(currentUser.department) }
  var course by remember { mutableStateOf(currentUser.course) }
  var year by remember { mutableStateOf(currentUser.year) }
  var ageText by remember { mutableStateOf(currentUser.age.toString()) }
  var gender by remember { mutableStateOf(currentUser.gender) }
  var bio by remember { mutableStateOf(currentUser.bio) }
  var statusEmoji by remember { mutableStateOf(currentUser.statusEmoji) }
  var statusText by remember { mutableStateOf(currentUser.statusText) }
  var instagramHandle by remember { mutableStateOf(currentUser.instagramHandle ?: "") }
  var snapchatHandle by remember { mutableStateOf(currentUser.snapchatHandle ?: "") }
  var selectedColorHex by remember { mutableStateOf(currentUser.avatarColorHex) }

  var connectionPreferences by remember { mutableStateOf(currentUser.connectionPreferences) }
  val allGoals = listOf("Study Partner", "Project Partner", "Friendship", "Common Interests", "Hackathon Squad")

  var interestList by remember { mutableStateOf(currentUser.interests) }
  var newInterestInput by remember { mutableStateOf("") }

  var skillList by remember { mutableStateOf(currentUser.skills) }
  var newSkillInput by remember { mutableStateOf("") }

  val colorOptions = listOf(
    0xFFFF5722, // Neon Coral
    0xFF7C4DFF, // Electric Purple
    0xFF00BFA5, // Cyber Teal
    0xFFFFB300, // Sunburst Gold
    0xFFE91E63, // Hot Pink
    0xFF00B0FF  // Vivid Cyan
  )

  val emojiOptions = listOf("⚡", "🔬", "🩺", "🌿", "💻", "📚", "🎨", "🚀", "☕", "🔥")

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = {
          val parsedAge = ageText.toIntOrNull() ?: currentUser.age
          onSave(
            name,
            course,
            year,
            bio,
            statusEmoji,
            statusText,
            interestList,
            skillList,
            selectedColorHex,
            collegeName,
            department,
            parsedAge,
            gender,
            instagramHandle,
            snapchatHandle,
            connectionPreferences
          )
          onDismiss()
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF651FFF)),
        modifier = Modifier.testTag("save_profile_button")
      ) {
        Text("Save Profile ✨", fontWeight = FontWeight.Bold)
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
          imageVector = Icons.Default.Edit,
          contentDescription = null,
          tint = Color(0xFF651FFF),
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Edit Student Profile",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
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
        // Name
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Full Name") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
        )

        // College/University
        OutlinedTextField(
          value = collegeName,
          onValueChange = { collegeName = it },
          label = { Text("College / University") },
          placeholder = { Text("e.g. Delhi Technological University, BITS Pilani") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("edit_profile_college_input")
        )

        // Department & Course
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = course,
            onValueChange = { course = it },
            label = { Text("Course / Degree") },
            placeholder = { Text("e.g. B.Tech CSE") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = department,
            onValueChange = { department = it },
            label = { Text("Department") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        // Year & Age
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = year,
            onValueChange = { year = it },
            label = { Text("Current Year") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = ageText,
            onValueChange = { ageText = it },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        // Gender selector
        Text("Gender", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Male", "Female", "Non-Binary", "Other").forEach { g ->
            FilterChip(
              selected = gender.equals(g, ignoreCase = true),
              onClick = { gender = g },
              label = { Text(g, fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp)
            )
          }
        }

        // Connection Preferences
        Text("Connection Preferences", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          allGoals.forEach { goal ->
            val isSelected = connectionPreferences.contains(goal)
            FilterChip(
              selected = isSelected,
              onClick = {
                connectionPreferences = if (isSelected) {
                  connectionPreferences - goal
                } else {
                  connectionPreferences + goal
                }
              },
              label = { Text(goal, fontSize = 11.sp) },
              shape = RoundedCornerShape(8.dp)
            )
          }
        }

        // Bio
        OutlinedTextField(
          value = bio,
          onValueChange = { bio = it },
          label = { Text("Bio (Share what you love or are building)") },
          placeholder = { Text("e.g. Hackathon lover, building apps with Compose & Kotlin...") },
          minLines = 3,
          maxLines = 5,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("edit_profile_bio_input")
        )

        // Social Handles (Instagram & Snapchat)
        Text("Social Handles (Optional)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = instagramHandle,
            onValueChange = { instagramHandle = it },
            label = { Text("Instagram @") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = snapchatHandle,
            onValueChange = { snapchatHandle = it },
            label = { Text("Snapchat @") },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
          )
        }

        // Live Campus Status
        Text("Campus Status & Mood", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          emojiOptions.forEach { emoji ->
            Surface(
              shape = CircleShape,
              color = if (statusEmoji == emoji) Color(0xFF651FFF).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
              border = if (statusEmoji == emoji) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF651FFF)) else null,
              modifier = Modifier
                .size(36.dp)
                .clickable { statusEmoji = emoji }
            ) {
              Box(contentAlignment = Alignment.Center) {
                Text(text = emoji, fontSize = 16.sp)
              }
            }
          }
        }

        OutlinedTextField(
          value = statusText,
          onValueChange = { statusText = it },
          label = { Text("Status description") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        // Avatar Accent Color
        Text("Profile Color Accent", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          colorOptions.forEach { colorHex ->
            Surface(
              shape = CircleShape,
              color = Color(colorHex),
              border = if (selectedColorHex == colorHex) androidx.compose.foundation.BorderStroke(3.dp, MaterialTheme.colorScheme.onSurface) else null,
              modifier = Modifier
                .size(36.dp)
                .clickable { selectedColorHex = colorHex }
            ) {}
          }
        }

        // Interests Chips Editor
        Text("Interests & Hobbies", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          interestList.forEach { interest ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, end = 4.dp, top = 2.dp, bottom = 2.dp)
              ) {
                Text(text = interest, style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Remove",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier
                    .size(14.dp)
                    .clickable { interestList = interestList - interest }
                )
              }
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedTextField(
            value = newInterestInput,
            onValueChange = { newInterestInput = it },
            placeholder = { Text("Add interest (e.g. AI, Music, Cricket)") },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f)
          )
          Spacer(modifier = Modifier.width(6.dp))
          IconButton(
            onClick = {
              if (newInterestInput.isNotBlank() && !interestList.contains(newInterestInput.trim())) {
                interestList = interestList + newInterestInput.trim()
                newInterestInput = ""
              }
            }
          ) {
            Icon(Icons.Default.Add, contentDescription = "Add Interest", tint = Color(0xFF651FFF))
          }
        }
      }
    }
  )
}
