package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import com.example.data.model.NotificationConfig
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.repository.MgugRealtimeRepository
import com.example.ui.components.NotificationSettingsDialog
import com.example.ui.util.FormatUtils

@Composable
fun NotificationsScreen(
  notifications: List<NotificationItem>,
  onMarkAllRead: () -> Unit,
  onDismissNotification: (String) -> Unit,
  onClearAll: () -> Unit,
  modifier: Modifier = Modifier
) {
  val notificationConfig by MgugRealtimeRepository.notificationConfig.collectAsState()
  var showSettingsDialog by remember { mutableStateOf(false) }
  var filterType by remember { mutableStateOf<NotificationType?>(null) }

  val filteredNotifications = remember(notifications, filterType) {
    if (filterType == null) notifications
    else notifications.filter { it.type == filterType }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(16.dp)
      .testTag("notifications_screen")
  ) {
    // 1. Top Header Bar
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Campus Live Notifications",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = CircleShape,
            color = if (notificationConfig.pushNotificationsEnabled) Color(0xFF00E676) else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(8.dp)
          ) {}
        }
        Text(
          text = if (notificationConfig.pushNotificationsEnabled) "Real-time alerts active · Instant push" else "Push alerts paused in settings",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Settings configuration button
        IconButton(
          onClick = { showSettingsDialog = true },
          modifier = Modifier.testTag("notification_settings_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Notification Settings",
            tint = MaterialTheme.colorScheme.primary
          )
        }

        if (notifications.any { !it.isRead }) {
          IconButton(
            onClick = onMarkAllRead,
            modifier = Modifier.testTag("mark_all_read_button")
          ) {
            Icon(
              imageVector = Icons.Default.DoneAll,
              contentDescription = "Mark all read",
              tint = MaterialTheme.colorScheme.secondary
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // 2. Filter chips (Chats, Replies, Events, Polls, Notices)
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      FilterChip(
        selected = filterType == null,
        onClick = { filterType = null },
        label = { Text("All (${notifications.size})", fontSize = 11.sp) },
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = Color.White
        )
      )
      FilterChip(
        selected = filterType == NotificationType.CHAT_MESSAGE,
        onClick = { filterType = if (filterType == NotificationType.CHAT_MESSAGE) null else NotificationType.CHAT_MESSAGE },
        label = { Text("Chats", fontSize = 11.sp) }
      )
      FilterChip(
        selected = filterType == NotificationType.POST_REPLY,
        onClick = { filterType = if (filterType == NotificationType.POST_REPLY) null else NotificationType.POST_REPLY },
        label = { Text("Replies", fontSize = 11.sp) }
      )
      FilterChip(
        selected = filterType == NotificationType.UPCOMING_EVENT,
        onClick = { filterType = if (filterType == NotificationType.UPCOMING_EVENT) null else NotificationType.UPCOMING_EVENT },
        label = { Text("Events", fontSize = 11.sp) }
      )
      FilterChip(
        selected = filterType == NotificationType.NEW_POLL,
        onClick = { filterType = if (filterType == NotificationType.NEW_POLL) null else NotificationType.NEW_POLL },
        label = { Text("Polls", fontSize = 11.sp) }
      )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 3. Notification List or Empty State
    if (filteredNotifications.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
            modifier = Modifier.size(64.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "You're all caught up!",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "No alerts to show. Incoming chat DMs and event notices will appear here.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    } else {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${filteredNotifications.size} notification${if (filteredNotifications.size > 1) "s" else ""}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
        TextButton(onClick = onClearAll) {
          Text("Dismiss All", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .testTag("notifications_lazy_column"),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
      ) {
        items(filteredNotifications, key = { it.id }) { item ->
          val (icon, color) = when (item.type) {
            NotificationType.CHAT_MESSAGE -> Icons.Default.Chat to Color(0xFF651FFF)
            NotificationType.POST_REPLY -> Icons.AutoMirrored.Filled.Comment to Color(0xFF00B0FF)
            NotificationType.UPCOMING_EVENT -> Icons.Default.Event to Color(0xFF00BFA5)
            NotificationType.NEW_POLL -> Icons.Default.HowToVote to Color(0xFFFF5722)
            NotificationType.CAMPUS_NOTICE -> Icons.Default.Campaign to Color(0xFFFFB300)
            NotificationType.PARTNER_REQUEST -> Icons.Default.Handshake to Color(0xFF3F51B5)
            NotificationType.POST_LIKE -> Icons.Default.Favorite to Color(0xFFE91E63)
          }

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .testTag("notification_card_${item.id}"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (item.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .background(color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = color,
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (item.isRead) FontWeight.SemiBold else FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  // Dismiss button per notification
                  IconButton(
                    onClick = { onDismissNotification(item.id) },
                    modifier = Modifier
                      .size(20.dp)
                      .testTag("dismiss_notification_${item.id}")
                  ) {
                    Icon(
                      Icons.Default.Close,
                      contentDescription = "Dismiss",
                      tint = MaterialTheme.colorScheme.outline,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                  text = item.message,
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = FormatUtils.formatRelativeTime(item.timestamp),
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }
          }
        }
      }
    }
  }

  // Notification Config Dialog
  if (showSettingsDialog) {
    NotificationSettingsDialog(
      currentConfig = notificationConfig,
      onDismiss = { showSettingsDialog = false },
      onSave = { updated ->
        MgugRealtimeRepository.updateNotificationConfig(updated)
      }
    )
  }
}
