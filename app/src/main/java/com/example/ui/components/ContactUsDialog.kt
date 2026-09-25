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
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactSupport
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudentProfile

/**
 * Clean and simple Contact Us dialog for student support inquiries.
 */
@Composable
fun ContactUsDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (subject: String, category: String, message: String, attachmentName: String?) -> Unit
) {
  var subject by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }
  var selectedCategory by remember { mutableStateOf("General Inquiry") }
  var hasAttachment by remember { mutableStateOf(false) }
  var isSubmitting by remember { mutableStateOf(false) }

  val categories = listOf("General Inquiry", "Account & Verification", "Campus Club Request", "Report Abuse")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("contact_us_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.ContactSupport,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Contact Support",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("contact_us_close_button")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Text(
          text = "Need help with your campus account or student community? Drop us a note.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category selection
        Text(
          text = "CATEGORY",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.take(2).forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          categories.drop(2).forEach { cat ->
            FilterChip(
              selected = selectedCategory == cat,
              onClick = { selectedCategory = cat },
              label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Subject
        OutlinedTextField(
          value = subject,
          onValueChange = { subject = it },
          label = { Text("Subject") },
          placeholder = { Text("e.g. Question regarding college verification") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("contact_us_subject_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Message
        OutlinedTextField(
          value = message,
          onValueChange = { message = it },
          label = { Text("Your Message") },
          placeholder = { Text("Explain your query or issue in detail…") },
          minLines = 4,
          maxLines = 6,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("contact_us_message_input"),
          shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Optional attachment toggle
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AttachFile,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (hasAttachment) "attachment_screenshot.png attached" else "Attach screenshot / document (optional)",
              style = MaterialTheme.typography.bodySmall,
              color = if (hasAttachment) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          FilterChip(
            selected = hasAttachment,
            onClick = { hasAttachment = !hasAttachment },
            label = { Text(if (hasAttachment) "Remove" else "Attach", style = MaterialTheme.typography.labelSmall) },
            modifier = Modifier.testTag("contact_us_attach_button")
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Submit Button
        Button(
          onClick = {
            if (message.isNotBlank()) {
              isSubmitting = true
              onSubmit(
                if (subject.isNotBlank()) subject else selectedCategory,
                selectedCategory,
                message,
                if (hasAttachment) "attachment_student_proof.png" else null
              )
              onDismiss()
            }
          },
          enabled = message.isNotBlank() && !isSubmitting,
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("contact_us_submit_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Send Message", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
