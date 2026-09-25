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
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Report
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.sp
import com.example.data.model.ReportTargetType
import com.example.data.repository.MgugRealtimeRepository

@Composable
fun SupportReportDialog(
  onDismiss: () -> Unit,
  defaultMode: String = "SUPPORT" // "SUPPORT" or "REPORT"
) {
  var mode by remember { mutableStateOf(defaultMode) }

  // Report fields
  var targetType by remember { mutableStateOf(ReportTargetType.POST) }
  var reportReason by remember { mutableStateOf("") }
  var targetDescription by remember { mutableStateOf("") }

  // Support / Feedback fields
  var subject by remember { mutableStateOf("") }
  var message by remember { mutableStateOf("") }

  var submitted by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = if (mode == "REPORT") Icons.Default.Report else Icons.Default.Help,
          contentDescription = null,
          tint = if (mode == "REPORT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (mode == "REPORT") "Community Report" else "Help & Student Support",
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
        if (submitted) {
          Text(
            text = "Thank you! Your request has been recorded and submitted to the Campus Connect community moderators.",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF00C853),
            fontWeight = FontWeight.SemiBold
          )
        } else {
          // Mode switcher
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            FilterChip(
              selected = mode == "SUPPORT",
              onClick = { mode = "SUPPORT" },
              label = { Text("Contact Support", fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp)
            )
            FilterChip(
              selected = mode == "REPORT",
              onClick = { mode = "REPORT" },
              label = { Text("Report Content/User", fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp)
            )
          }

          if (mode == "REPORT") {
            Text(
              text = "Report inappropriate behavior, scam requests, or offensive campus posts.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text("What would you like to report?", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(ReportTargetType.POST, ReportTargetType.USER, ReportTargetType.POLL, ReportTargetType.EVENT).forEach { t ->
                FilterChip(
                  selected = targetType == t,
                  onClick = { targetType = t },
                  label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 11.sp) },
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }

            OutlinedTextField(
              value = targetDescription,
              onValueChange = { targetDescription = it },
              label = { Text("Item identifier / username / title") },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = reportReason,
              onValueChange = { reportReason = it },
              label = { Text("Reason for report") },
              placeholder = { Text("e.g. Inappropriate content, spam, misinformation, harassment") },
              minLines = 3,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            )
          } else {
            Text(
              text = "Have feedback, found a bug, or need help connecting with your college squad?",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
              value = subject,
              onValueChange = { subject = it },
              label = { Text("Subject") },
              placeholder = { Text("e.g. Feature suggestion, verification help") },
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
              value = message,
              onValueChange = { message = it },
              label = { Text("Your Message") },
              placeholder = { Text("Explain your suggestion or issue in detail…") },
              minLines = 3,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    },
    confirmButton = {
      if (submitted) {
        Button(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
          Text("Done")
        }
      } else {
        Button(
          onClick = {
            if (mode == "REPORT") {
              MgugRealtimeRepository.submitReport(
                targetType = targetType,
                targetId = targetDescription.ifEmpty { "general" },
                reason = reportReason.ifEmpty { "Reported by user" }
              )
            } else {
              MgugRealtimeRepository.submitFeedback(
                subject = subject.ifEmpty { "General Feedback" },
                message = message.ifEmpty { "No feedback details" }
              )
            }
            submitted = true
          },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (mode == "REPORT") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier.testTag("submit_support_report_button")
        ) {
          Text(if (mode == "REPORT") "Submit Report" else "Send Message", fontWeight = FontWeight.Bold)
        }
      }
    },
    dismissButton = {
      if (!submitted) {
        OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(12.dp)) {
          Text("Cancel")
        }
      }
    }
  )
}
