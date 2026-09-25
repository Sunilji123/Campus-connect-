package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.ui.components.campusAmbientMesh

/**
 * Modern slide-out navigation menu for Campus Connect.
 * Contains ONLY the 7 designated items:
 * 1. 🔎 Lost & Found
 * 2. 📚 Study Partner
 * 3. 💻 Project Partner
 * 4. 🎉 Events
 * 5. 📩 Contact Us
 * 6. 💬 Feedback
 * 7. ⚙️ Settings
 */
@Composable
fun CampusDrawerContent(
  currentUser: StudentProfile,
  onSelectLostFound: () -> Unit,
  onSelectStudyPartner: () -> Unit,
  onSelectProjectPartner: () -> Unit,
  onSelectEvents: () -> Unit,
  onSelectContactUs: () -> Unit,
  onSelectFeedback: () -> Unit,
  onSelectSettings: () -> Unit,
  onProfileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ModalDrawerSheet(
    modifier = modifier
      .width(320.dp)
      .fillMaxHeight()
      .campusAmbientMesh(),
    drawerContainerColor = MaterialTheme.colorScheme.surface,
    drawerShape = RoundedCornerShape(topEnd = 28.dp, bottomEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxHeight()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
      // 1. User Mini-Profile Header with Electric Gradient
      Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.Transparent,
        shadowElevation = 4.dp,
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(22.dp))
          .clickable { onProfileClick() }
          .testTag("drawer_mini_profile_header")
      ) {
        Box(
          modifier = Modifier
            .background(
              Brush.linearGradient(
                colors = listOf(
                  Color(0xFF651FFF), // Electric Violet
                  Color(0xFF7C4DFF), // Indigo
                  Color(0xFFFF4081)  // Electric Pink
                )
              )
            )
            .padding(16.dp)
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.25f)
              ) {
                Text(
                  text = "⚡ CAMPUS CONNECT",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.8.sp
                  ),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.25f)
              ) {
                Text(
                  text = "★ VERIFIED",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              // Profile photo / avatar with glowing gradient border
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .background(
                    Brush.sweepGradient(
                      listOf(Color(0xFFFFD600), Color(0xFFFF5722), Color(0xFF00E5FF), Color(0xFFFFD600))
                    ),
                    CircleShape
                  )
                  .padding(2.5.dp),
                contentAlignment = Alignment.Center
              ) {
                Box(
                  modifier = Modifier
                    .fillMaxSize()
                    .background(Color(currentUser.avatarColorHex), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = currentUser.name.take(1).uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                  )
                }

                // Status Emoji overlay
                Box(
                  modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(19.dp)
                    .background(Color.White, CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(currentUser.statusEmoji, fontSize = 11.sp)
                }
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = currentUser.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verified Student",
                    tint = Color(0xFF80D8FF),
                    modifier = Modifier.size(15.dp)
                  )
                }

                Text(
                  text = currentUser.collegeName,
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.9f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )

                Text(
                  text = "${currentUser.course} · ${currentUser.year}",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White.copy(alpha = 0.75f),
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(20.dp))

      Text(
        text = "CAMPUS OPPORTUNITIES",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 1. 🔎 Lost & Found
      DrawerMenuItem(
        icon = Icons.Default.Search,
        label = "Lost & Found",
        subtitle = "Report or claim lost items on campus",
        accentColor = Color(0xFFE53935),
        testTag = "drawer_item_lost_found",
        onClick = onSelectLostFound
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 2. 📚 Study Partner
      DrawerMenuItem(
        icon = Icons.Default.MenuBook,
        label = "Study Partner",
        subtitle = "Find exam squads & study buddies",
        accentColor = Color(0xFF2E7D32),
        testTag = "drawer_item_study_partner",
        onClick = onSelectStudyPartner
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 3. 💻 Project Partner
      DrawerMenuItem(
        icon = Icons.Default.Code,
        label = "Project Partner",
        subtitle = "Build hackathon & tech projects",
        accentColor = Color(0xFF1565C0),
        testTag = "drawer_item_project_partner",
        onClick = onSelectProjectPartner
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 4. 🎉 Events
      DrawerMenuItem(
        icon = Icons.Default.Event,
        label = "Events",
        subtitle = "Fests, workshops & campus gatherings",
        accentColor = Color(0xFF7B1FA2),
        testTag = "drawer_item_events",
        onClick = onSelectEvents
      )

      Spacer(modifier = Modifier.height(16.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(16.dp))

      Text(
        text = "SUPPORT & PREFERENCES",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.Bold,
          letterSpacing = 1.2.sp
        ),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 5. 📩 Contact Us
      DrawerMenuItem(
        icon = Icons.Default.ContactSupport,
        label = "Contact Us",
        subtitle = "Campus Connect helpdesk & support",
        accentColor = Color(0xFF00897B),
        testTag = "drawer_item_contact_us",
        onClick = onSelectContactUs
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 6. 💬 Feedback
      DrawerMenuItem(
        icon = Icons.Default.Feedback,
        label = "Feedback",
        subtitle = "Share bug reports & feature ideas",
        accentColor = Color(0xFFF57C00),
        testTag = "drawer_item_feedback",
        onClick = onSelectFeedback
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 7. ⚙️ Settings
      DrawerMenuItem(
        icon = Icons.Default.Settings,
        label = "Settings",
        subtitle = "Account, notifications & privacy",
        accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        testTag = "drawer_item_settings",
        onClick = onSelectSettings
      )

      Spacer(modifier = Modifier.weight(1f, fill = false))
      Spacer(modifier = Modifier.height(24.dp))

      // Footer branding
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Campus Connect v2.4",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )
        Text(
          text = "Connect. Learn. Grow.",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
          color = MaterialTheme.colorScheme.primary
        )
      }
    }
  }
}

@Composable
private fun DrawerMenuItem(
  icon: ImageVector,
  label: String,
  subtitle: String,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color.Transparent,
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable(onClick = onClick)
      .testTag(testTag)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 12.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .background(
            Brush.linearGradient(
              listOf(
                accentColor,
                accentColor.copy(alpha = 0.75f)
              )
            ),
            RoundedCornerShape(14.dp)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(14.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = label,
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = subtitle,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
      }
    }
  }
}
