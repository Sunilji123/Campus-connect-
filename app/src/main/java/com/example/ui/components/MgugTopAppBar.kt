package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConnectionStatus
import com.example.data.model.StudentProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MgugTopAppBar(
  currentUser: StudentProfile,
  connectionStatus: ConnectionStatus,
  unreadNotifications: Int,
  onNotificationsClick: () -> Unit,
  onMenuClick: () -> Unit = {},
  onProfileClick: () -> Unit = {},
  onToggleConnection: () -> Unit = {},
  onGoogleSignInClick: () -> Unit = {},
  onSearchUserClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier.fillMaxWidth()) {
    TopAppBar(
      modifier = Modifier.testTag("mgug_top_app_bar"),
      colors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface
      ),
      navigationIcon = {
        IconButton(
          onClick = onMenuClick,
          modifier = Modifier.testTag("top_bar_menu_button")
        ) {
          Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu Drawer",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      },
      title = {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "Campus Connect",
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.3).sp
              ),
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Verified Campus Community",
              tint = MaterialTheme.colorScheme.secondary,
              modifier = Modifier.size(15.dp)
            )
          }
          Text(
            text = if (currentUser.collegeName.isNotBlank()) currentUser.collegeName else "Connect. Learn. Grow. Together.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
          )
        }
      },
      actions = {
        // Search icon
        IconButton(
          onClick = onSearchUserClick,
          modifier = Modifier.testTag("top_bar_search_users_button")
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search Campus",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }

        // Notifications bell with badge
        IconButton(
          onClick = onNotificationsClick,
          modifier = Modifier.testTag("notifications_button")
        ) {
          BadgedBox(
            badge = {
              if (unreadNotifications > 0) {
                Badge {
                  Text(text = if (unreadNotifications > 9) "9+" else unreadNotifications.toString())
                }
              }
            }
          ) {
            Icon(
              imageVector = Icons.Default.Notifications,
              contentDescription = "Campus Notifications",
              tint = MaterialTheme.colorScheme.onSurface
            )
          }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Profile Avatar Button (Navigates directly to Profile tab!)
        Box(
          modifier = Modifier
            .padding(end = 12.dp)
            .size(36.dp)
            .background(Color(currentUser.avatarColorHex), CircleShape)
            .clip(CircleShape)
            .clickable { onProfileClick() }
            .testTag("top_bar_profile_avatar"),
          contentAlignment = Alignment.Center
        ) {
          Text(
            text = currentUser.name.take(1).uppercase(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    )

    // Offline / Reconnecting State Banner (Requirement 26)
    AnimatedVisibility(
      visible = connectionStatus == ConnectionStatus.OFFLINE || connectionStatus == ConnectionStatus.CONNECTING,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onToggleConnection() },
        color = if (connectionStatus == ConnectionStatus.OFFLINE) Color(0xFFD32F2F) else Color(0xFFF57C00)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (connectionStatus == ConnectionStatus.OFFLINE) Icons.Default.WifiOff else Icons.Default.Wifi,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(15.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (connectionStatus == ConnectionStatus.OFFLINE) "You're offline. Reconnecting…" else "Syncing campus feed in background…",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    }
  }
}
