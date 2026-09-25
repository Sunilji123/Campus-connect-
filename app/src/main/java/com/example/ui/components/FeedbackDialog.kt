package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudentProfile

/**
 * Clean feedback dialog with:
 * - Bug Report
 * - Feature Request
 * - UI Feedback
 * - General Feedback
 * - Other
 * Asynchronous submission with immediate feedback, no page reload!
 */
@Composable
fun FeedbackDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (type: String, message: String, hasScreenshot: Boolean) -> Unit
) {
  val feedbackTypes = listOf(
    "Bug Report",
    "Feature Request",
    "UI Feedback",
    "General Feedback",
    "Other"
  )

  var selectedType by remember { mutableStateOf("Bug Report") }
  var message by remember { mutableStateOf("") }
  var hasScreenshot by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("feedback_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Top Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Feedback,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Share Feedback",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("feedback_close_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "Help us build the ultimate campus community app for students.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Feedback options chips
        Text(
          text = "FEEDBACK TYPE",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          feedbackTypes.take(3).forEach { type ->
            FilterChip(
              selected = selectedType == type,
              onClick = { selectedType = type },
              label = { Text(type, style = MaterialTheme.typography.labelSmall) },
              modifier = Modifier.testTag("feedback_type_$type")
            )
          }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          feedbackTypes.drop(3).forEach { type ->
            FilterChip(
              selected = selectedType == type,
              onClick = { selectedType = type },
              label = { Text(type, style = MaterialTheme.typography.labelSmall) },
              modifier = Modifier.testTag("feedback_type_$type")
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Feedback message
        OutlinedTextField(
          value = message,
          onValueChange = { message = it },
          label = { Text("Your Feedback / Bug Description") },
          placeholder = { Text("What happened, or what feature would make campus life easier?") },
          minLines = 4,
          maxLines = 6,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("feedback_message_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Optional screenshot toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Icon(
              imageVector = if (hasScreenshot) Icons.Default.Check else Icons.Default.AddPhotoAlternate,
              contentDescription = null,
              tint = if (hasScreenshot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (hasScreenshot) "Screenshot attached" else "Attach screenshot (optional)",
              style = MaterialTheme.typography.bodySmall,
              color = if (hasScreenshot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          FilterChip(
            selected = hasScreenshot,
            onClick = { hasScreenshot = !hasScreenshot },
            label = { Text(if (hasScreenshot) "Remove" else "Add Screenshot", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("feedback_screenshot_toggle")
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
          onClick = {
            if (message.isNotBlank()) {
              isSubmitting = true
              onSubmit(selectedType, message, hasScreenshot)
              onDismiss()
            }
          },
          enabled = message.isNotBlank() && !isSubmitting,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("feedback_submit_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Submit Feedback", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
