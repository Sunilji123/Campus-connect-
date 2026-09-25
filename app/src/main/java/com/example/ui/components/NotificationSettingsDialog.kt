package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Reply
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationConfig

@Composable
fun NotificationSettingsDialog(
  currentConfig: NotificationConfig,
  onDismiss: () -> Unit,
  onSave: (NotificationConfig) -> Unit
) {
  var pushEnabled by remember { mutableStateOf(currentConfig.pushNotificationsEnabled) }
  var chatMessages by remember { mutableStateOf(currentConfig.chatMessages) }
  var postReplies by remember { mutableStateOf(currentConfig.postReplies) }
  var upcomingEvents by remember { mutableStateOf(currentConfig.upcomingEvents) }
  var newPolls by remember { mutableStateOf(currentConfig.newPolls) }
  var universityNotices by remember { mutableStateOf(currentConfig.universityNotices) }
  var soundAndVibration by remember { mutableStateOf(currentConfig.soundAndVibration) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = {
          onSave(
            NotificationConfig(
              pushNotificationsEnabled = pushEnabled,
              chatMessages = chatMessages,
              postReplies = postReplies,
              upcomingEvents = upcomingEvents,
              newPolls = newPolls,
              universityNotices = universityNotices,
              soundAndVibration = soundAndVibration
            )
          )
          onDismiss()
        },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF651FFF)),
        modifier = Modifier.testTag("save_notification_settings_button")
      ) {
        Text("Save Preferences", fontWeight = FontWeight.Bold)
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
          imageVector = Icons.Default.Notifications,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Notification Preferences",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Configure which campus real-time notifications you receive on your device.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (pushEnabled) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Push Notifications Master Switch",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (pushEnabled) "Active & Receiving" else "All notifications paused",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
            Switch(
              checked = pushEnabled,
              onCheckedChange = { pushEnabled = it },
              modifier = Modifier.testTag("master_notification_switch")
            )
          }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Individual toggles
        NotificationToggleRow(
          icon = Icons.Default.Chat,
          title = "Direct & Group Messages",
          description = "Alerts when peers DM or tag you in squad chats",
          checked = chatMessages && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { chatMessages = it },
          testTag = "toggle_chat_notifications"
        )

        NotificationToggleRow(
          icon = Icons.Default.Reply,
          title = "Replies & Comments to Posts",
          description = "When someone replies to your feed or study post",
          checked = postReplies && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { postReplies = it },
          testTag = "toggle_reply_notifications"
        )

        NotificationToggleRow(
          icon = Icons.Default.Event,
          title = "Upcoming Events Reminders",
          description = "Events and hackathons you've shown interest in / RSVP'd",
          checked = upcomingEvents && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { upcomingEvents = it },
          testTag = "toggle_event_notifications"
        )

        NotificationToggleRow(
          icon = Icons.Default.HowToVote,
          title = "New Campus Polls",
          description = "Get notified when new student voice polls open",
          checked = newPolls && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { newPolls = it },
          testTag = "toggle_poll_notifications"
        )

        NotificationToggleRow(
          icon = Icons.Default.Campaign,
          title = "University Announcements",
          description = "Critical notices, exam schedules, and council updates",
          checked = universityNotices && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { universityNotices = it },
          testTag = "toggle_notice_notifications"
        )

        NotificationToggleRow(
          icon = Icons.Default.VolumeUp,
          title = "Sound & Vibration",
          description = "Play notification chime and vibrate on incoming alerts",
          checked = soundAndVibration && pushEnabled,
          enabled = pushEnabled,
          onCheckedChange = { soundAndVibration = it },
          testTag = "toggle_sound_notifications"
        )
      }
    }
  )
}

@Composable
private fun NotificationToggleRow(
  icon: ImageVector,
  title: String,
  description: String,
  checked: Boolean,
  enabled: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1f)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
          color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
        )
        Text(
          text = description,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
          color = MaterialTheme.colorScheme.outline
        )
      }
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      enabled = enabled,
      modifier = Modifier.testTag(testTag)
    )
  }
}
