package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CollegeInfo
import com.example.data.model.StudentProfile
import com.example.data.repository.MgugRealtimeRepository
import com.example.ui.components.AdminDashboardDialog
import com.example.ui.components.EditProfileDialog
import com.example.ui.components.NotificationSettingsDialog
import com.example.ui.components.SearchUserDialog
import com.example.ui.components.StudentVerificationDialog
import com.example.ui.components.SupportReportDialog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
  currentUser: StudentProfile,
  allProfiles: List<StudentProfile>,
  onUserSwitch: (String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  onGoogleSignInClick: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showVerificationDialog by remember { mutableStateOf(false) }
  var showSearchUserDialog by remember { mutableStateOf(false) }
  var showSupportReportDialog by remember { mutableStateOf(false) }
  var showAdminDashboardDialog by remember { mutableStateOf(false) }
  var showNotificationSettingsDialog by remember { mutableStateOf(false) }
  var showCollegeSwitcherDialog by remember { mutableStateOf(false) }
  var showLogoutConfirmDialog by remember { mutableStateOf(false) }

  val isLoggedIn = currentUser.isGoogleUser || currentUser.email.endsWith("@gmail.com")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("profile_screen_lazy_column"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Digital Student Smart ID Card (Gen-Z Clean Design)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("student_id_smart_card"),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column {
          // Smart Card Banner
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  listOf(Color(0xFF651FFF), Color(0xFF00B0FF))
                )
              )
              .padding(horizontal = 18.dp, vertical = 14.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = currentUser.collegeName.uppercase(),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                  ),
                  color = Color.White
                )
                Text(
                  text = "Verified Student Smart Card · Campus Connect ID",
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                  color = Color.White.copy(alpha = 0.85f)
                )
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.25f)
              ) {
                Text(
                  text = if (currentUser.isVerified) "VERIFIED" else "STUDENT",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
          }

          // Smart Card Body
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(76.dp)
                .background(Color(currentUser.avatarColorHex), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = currentUser.name.take(1),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = currentUser.name,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                if (currentUser.isVerified) {
                  Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = "Verified Student",
                    tint = Color(0xFF00B0FF),
                    modifier = Modifier.size(18.dp)
                  )
                }
              }

              Text(
                text = "${currentUser.course} · ${currentUser.year}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
              )

              Text(
                text = "${currentUser.department} · ${currentUser.collegeName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )

              Spacer(modifier = Modifier.height(4.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = "${currentUser.age} yrs · ${currentUser.gender}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                  Text(
                    text = "${currentUser.statusEmoji} ${currentUser.statusText}",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                  )
                }
              }
            }
          }

          HorizontalDivider(modifier = Modifier.padding(horizontal = 18.dp))

          // Bio, Social Handles, and Connection Preferences
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Student Bio",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = currentUser.bio,
              style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
              color = MaterialTheme.colorScheme.onSurface
            )

            // Social Handles (Instagram & Snapchat)
            if (!currentUser.instagramHandle.isNullOrBlank() || !currentUser.snapchatHandle.isNullOrBlank()) {
              Spacer(modifier = Modifier.height(10.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!currentUser.instagramHandle.isNullOrBlank()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE1306C).copy(alpha = 0.12f)
                  ) {
                    Text(
                      text = "📷 @${currentUser.instagramHandle}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = Color(0xFFC13584),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
                if (!currentUser.snapchatHandle.isNullOrBlank()) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFFC00).copy(alpha = 0.25f)
                  ) {
                    Text(
                      text = "👻 @${currentUser.snapchatHandle}",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = Color(0xFF424242),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }

            // Connection Preferences
            if (currentUser.connectionPreferences.isNotEmpty()) {
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Looking For (Connection Preferences):",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.outline
              )
              Spacer(modifier = Modifier.height(6.dp))
              FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
              ) {
                currentUser.connectionPreferences.forEach { pref ->
                  Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                  ) {
                    Text(
                      text = "✨ $pref",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      color = MaterialTheme.colorScheme.onSecondaryContainer,
                      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Interests & Passions",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              currentUser.interests.forEach { tag ->
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                ) {
                  Text(
                    text = tag,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons: Edit Profile & Student Verification
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              OutlinedButton(
                onClick = { showEditProfileDialog = true },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("edit_profile_button")
              ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
              }

              ElevatedButton(
                onClick = { showVerificationDialog = true },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                  containerColor = if (currentUser.isVerified) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFF651FFF),
                  contentColor = if (currentUser.isVerified) Color(0xFF00796B) else Color.White
                ),
                modifier = Modifier
                  .weight(1.1f)
                  .testTag("verify_id_button")
              ) {
                Icon(
                  imageVector = if (currentUser.isVerified) Icons.Default.CheckCircle else Icons.Default.Verified,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = if (currentUser.isVerified) "Verified Student" else "Verify ID",
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }
      }
    }

    // 2. Multi-College Student Directory & Search Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Inter-College Student Directory",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "${allProfiles.size} Students",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Search peers across all colleges by campus, degree, skills, and collaboration goals.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { showSearchUserDialog = true },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("profile_search_students_button")
          ) {
            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Search All Students", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = "Active Students Across Campuses:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.outline
          )

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            items(allProfiles, key = { it.id }) { profile ->
              val isCurrent = profile.id == currentUser.id
              Surface(
                shape = RoundedCornerShape(14.dp),
                color = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                  .clip(RoundedCornerShape(14.dp))
                  .clickable {
                    if (!isCurrent) {
                      onNavigateToChat(profile.id)
                    }
                  }
                  .testTag("profile_peer_chip_${profile.id}")
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .background(Color(profile.avatarColorHex), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = profile.name.take(1),
                      color = Color.White,
                      fontWeight = FontWeight.Bold,
                      fontSize = 12.sp
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = profile.name.split(" ").first(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                      )
                      if (isCurrent) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "· You",
                          style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary
                          )
                        )
                      }
                    }
                    Text(
                      text = profile.collegeName.split(" ").first(),
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
    }

    // 3. Settings & Account Management (Section 8, 20 & 21: Settings belongs in Profile)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("profile_settings_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Settings,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Settings & Account",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Campus Switcher Item
          SettingsItemRow(
            icon = Icons.Default.School,
            title = "My College / University",
            subtitle = currentUser.collegeName,
            onClick = { showCollegeSwitcherDialog = true },
            testTag = "settings_switch_campus"
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Notification Preferences
          SettingsItemRow(
            icon = Icons.Default.Notifications,
            title = "Notification Preferences",
            subtitle = "Push alerts, chats, study partner mentions",
            onClick = { showNotificationSettingsDialog = true },
            testTag = "settings_notifications"
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Help & Support / Report Content
          SettingsItemRow(
            icon = Icons.Default.HelpOutline,
            title = "Help, Support & Report Content",
            subtitle = "Contact student safety team or report violation",
            onClick = { showSupportReportDialog = true },
            testTag = "settings_support_report"
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Admin Moderation Console
          SettingsItemRow(
            icon = Icons.Default.AdminPanelSettings,
            title = "Admin & Safety Console",
            subtitle = "Campus metrics, reports queue & safety oversight",
            onClick = { showAdminDashboardDialog = true },
            testTag = "settings_admin_dashboard"
          )

          HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

          // Switch / Log out Account
          SettingsItemRow(
            icon = Icons.Default.Logout,
            title = "Account Management & Logout",
            subtitle = "Securely sign out or manage session",
            onClick = { showLogoutConfirmDialog = true },
            testTag = "settings_account_logout",
            iconTint = Color(0xFFD32F2F)
          )
        }
      }
    }
  }

  // Modals & Dialogs
  if (showEditProfileDialog) {
    EditProfileDialog(
      currentUser = currentUser,
      onDismiss = { showEditProfileDialog = false },
      onSave = { name, course, year, bio, emoji, status, interests, skills, avatarColorHex, colName, dept, age, gender, insta, snap, prefs ->
        MgugRealtimeRepository.updateProfile(
          name = name,
          course = course,
          year = year,
          bio = bio,
          statusEmoji = emoji,
          statusText = status,
          interests = interests,
          skills = skills,
          avatarColorHex = avatarColorHex,
          collegeName = colName,
          department = dept,
          age = age,
          gender = gender,
          instagramHandle = insta,
          snapchatHandle = snap,
          connectionPreferences = prefs
        )
      }
    )
  }

  if (showVerificationDialog) {
    StudentVerificationDialog(
      currentUser = currentUser,
      onDismiss = { showVerificationDialog = false },
      onVerify = { method, data ->
        MgugRealtimeRepository.submitVerificationRequest(method, data)
        showVerificationDialog = false
      }
    )
  }

  if (showSearchUserDialog) {
    SearchUserDialog(
      currentUser = currentUser,
      onDismiss = { showSearchUserDialog = false },
      onSelectStudentToChat = onNavigateToChat,
      onSwitchUser = { profile -> onUserSwitch(profile.id) }
    )
  }

  if (showSupportReportDialog) {
    SupportReportDialog(
      onDismiss = { showSupportReportDialog = false }
    )
  }

  if (showAdminDashboardDialog) {
    AdminDashboardDialog(
      onDismiss = { showAdminDashboardDialog = false }
    )
  }

  if (showNotificationSettingsDialog) {
    NotificationSettingsDialog(
      currentConfig = MgugRealtimeRepository.notificationConfig.value,
      onDismiss = { showNotificationSettingsDialog = false },
      onSave = { config ->
        MgugRealtimeRepository.updateNotificationConfig(config)
        showNotificationSettingsDialog = false
      }
    )
  }

  // College Switcher Dialog
  if (showCollegeSwitcherDialog) {
    AlertDialog(
      onDismissRequest = { showCollegeSwitcherDialog = false },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Select Your Institution", fontWeight = FontWeight.Bold)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            "Switch your primary campus affiliation to discover students, clubs, and study squads in your college:",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          MgugRealtimeRepository.SUPPORTED_COLLEGES.forEach { col ->
            val isCurrent = col.name == currentUser.collegeName
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isCurrent) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = if (isCurrent) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
              modifier = Modifier
                .fillMaxWidth()
                .clickable {
                  MgugRealtimeRepository.updateUserCollege(col)
                  showCollegeSwitcherDialog = false
                }
            ) {
              Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "🎓",
                  fontSize = 18.sp
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = col.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "${col.city}, ${col.state} · ${col.studentCount} students",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
                if (isCurrent) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showCollegeSwitcherDialog = false }) {
          Text("Done")
        }
      }
    )
  }

  // Logout Confirm Dialog (Requirement 8 & 21: Kept strictly inside Profile -> Settings -> Account)
  if (showLogoutConfirmDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutConfirmDialog = false },
      title = { Text("Log Out of Student Account?", fontWeight = FontWeight.Bold) },
      text = {
        Text(
          "Your posts, messages, and student badges will remain safe in Cloud Firestore. You can log back in at any time.",
          style = MaterialTheme.typography.bodyMedium
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showLogoutConfirmDialog = false
            // Switch to default student or trigger Google sign in
            onGoogleSignInClick()
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
        ) {
          Text("Confirm Log Out", fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutConfirmDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun SettingsItemRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  title: String,
  subtitle: String,
  onClick: () -> Unit,
  testTag: String,
  iconTint: Color = MaterialTheme.colorScheme.primary
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(vertical = 8.dp, horizontal = 4.dp)
      .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Box(
      modifier = Modifier
        .size(38.dp)
        .background(iconTint.copy(alpha = 0.12f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(20.dp)
      )
    }
    Spacer(modifier = Modifier.width(12.dp))
    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
