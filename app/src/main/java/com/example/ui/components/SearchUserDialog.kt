package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudentProfile
import com.example.data.repository.MgugRealtimeRepository

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchUserDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSelectStudentToChat: (recipientId: String) -> Unit,
  onSwitchUser: (StudentProfile) -> Unit
) {
  val allProfiles by MgugRealtimeRepository.allProfiles.collectAsState()
  var searchQuery by remember { mutableStateOf("") }
  var selectedFilter by remember { mutableStateOf("All") }

  val filters = listOf("All", "B.Tech CSE", "Nursing", "Pharmacy", "Management", "Verified")

  val filteredProfiles = remember(allProfiles, searchQuery, selectedFilter) {
    val q = searchQuery.trim().lowercase()
    allProfiles.filter { profile ->
      val matchesQuery = if (q.isBlank()) {
        true
      } else {
        profile.name.lowercase().contains(q) ||
          profile.course.lowercase().contains(q) ||
          profile.faculty.lowercase().contains(q) ||
          profile.rollNo.lowercase().contains(q) ||
          profile.email.lowercase().contains(q) ||
          profile.skills.any { it.lowercase().contains(q) } ||
          profile.interests.any { it.lowercase().contains(q) } ||
          profile.bio.lowercase().contains(q)
      }

      val matchesFilter = when (selectedFilter) {
        "B.Tech CSE" -> profile.course.contains("B.Tech", ignoreCase = true) || profile.course.contains("CSE", ignoreCase = true)
        "Nursing" -> profile.course.contains("Nursing", ignoreCase = true)
        "Pharmacy" -> profile.course.contains("Pharm", ignoreCase = true)
        "Management" -> profile.faculty.contains("Commerce", ignoreCase = true) || profile.course.contains("B.Com", ignoreCase = true) || profile.course.contains("BBA", ignoreCase = true)
        "Verified" -> profile.isVerified
        else -> true
      }

      matchesQuery && matchesFilter
    }
  }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.88f)
        .testTag("search_user_dialog"),
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Search MGUG Students",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer
              ) {
                Text(
                  text = "${filteredProfiles.size} found",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Text(
              text = "Find peers by name, roll no, branch, or skills",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          IconButton(
            onClick = onDismiss,
            modifier = Modifier.testTag("close_search_user_dialog")
          ) {
            Icon(Icons.Default.Clear, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { searchQuery = it },
          placeholder = { Text("Search by name, roll no, #skills, branch...") },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.primary)
          },
          trailingIcon = {
            if (searchQuery.isNotBlank()) {
              IconButton(onClick = { searchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear search", modifier = Modifier.size(18.dp))
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_user_text_field")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Quick branch / filter chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          filters.forEach { filter ->
            val isSelected = selectedFilter == filter
            FilterChip(
              selected = isSelected,
              onClick = { selectedFilter = filter },
              label = {
                Text(
                  text = filter,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                  )
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(12.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
        Spacer(modifier = Modifier.height(8.dp))

        // Student Results List
        if (filteredProfiles.isEmpty()) {
          Box(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth(),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "🔍",
                fontSize = 40.sp
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = "No students found",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Try searching with a different name, course, or clear filters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        } else {
          LazyColumn(
            modifier = Modifier
              .weight(1f)
              .fillMaxWidth()
              .testTag("search_user_results_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
          ) {
            items(filteredProfiles, key = { it.id }) { student ->
              val isCurrentUser = student.id == currentUser.id

              Card(
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("user_result_card_${student.id}"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                  containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                  } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                  }
                ),
                border = androidx.compose.foundation.BorderStroke(
                  width = 1.dp,
                  color = if (isCurrentUser) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent
                )
              ) {
                Column(modifier = Modifier.padding(12.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                  ) {
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      modifier = Modifier.weight(1f)
                    ) {
                      // Avatar
                      Box(
                        modifier = Modifier
                          .size(44.dp)
                          .background(Color(student.avatarColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                      ) {
                        Text(
                          text = student.name.take(1),
                          color = Color.White,
                          fontWeight = FontWeight.Bold,
                          fontSize = 18.sp
                        )
                      }

                      Spacer(modifier = Modifier.width(10.dp))

                      Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                          Text(
                            text = student.name,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                          )
                          if (student.isVerified) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                              imageVector = Icons.Default.Verified,
                              contentDescription = "Verified MGUG Student",
                              tint = Color(0xFF00B0FF),
                              modifier = Modifier.size(15.dp)
                            )
                          }
                          if (isCurrentUser) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Surface(
                              shape = RoundedCornerShape(8.dp),
                              color = MaterialTheme.colorScheme.primary
                            ) {
                              Text(
                                text = "YOU",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black, fontSize = 9.sp),
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                              )
                            }
                          }
                        }

                        Text(
                          text = "${student.course} · ${student.year}",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                          color = MaterialTheme.colorScheme.primary,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )

                        Text(
                          text = "Roll: ${student.rollNo} · ${student.faculty}",
                          style = MaterialTheme.typography.labelSmall,
                          color = MaterialTheme.colorScheme.onSurfaceVariant,
                          maxLines = 1,
                          overflow = TextOverflow.Ellipsis
                        )
                      }
                    }

                    // Live Status pill
                    Surface(
                      shape = RoundedCornerShape(8.dp),
                      color = MaterialTheme.colorScheme.surface
                    ) {
                      Text(
                        text = "${student.statusEmoji} ${student.statusText}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.SemiBold),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                      )
                    }
                  }

                  if (student.bio.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = student.bio,
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = MaterialTheme.colorScheme.onSurfaceVariant,
                      maxLines = 2,
                      overflow = TextOverflow.Ellipsis
                    )
                  }

                  // Skills & Interests tags
                  if (student.skills.isNotEmpty() || student.interests.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                      horizontalArrangement = Arrangement.spacedBy(4.dp),
                      verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      (student.skills.take(2) + student.interests.take(2)).distinct().forEach { tag ->
                        Surface(
                          shape = RoundedCornerShape(8.dp),
                          color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                          Text(
                            text = "#$tag",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                  }

                  Spacer(modifier = Modifier.height(8.dp))

                  // Actions
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    if (!isCurrentUser) {
                      Button(
                        onClick = {
                          onDismiss()
                          onSelectStudentToChat(student.id)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                          .weight(1f)
                          .testTag("chat_with_${student.id}"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                      ) {
                        Icon(
                          imageVector = Icons.AutoMirrored.Filled.Chat,
                          contentDescription = null,
                          modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Chat", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                      }

                      OutlinedButton(
                        onClick = {
                          onSwitchUser(student)
                          onDismiss()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                          .weight(1f)
                          .testTag("switch_to_user_${student.id}"),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.SwapHoriz,
                          contentDescription = null,
                          modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Switch Identity", fontSize = 12.sp)
                      }
                    } else {
                      Text(
                        text = "⭐ Active Session Profile",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(4.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
