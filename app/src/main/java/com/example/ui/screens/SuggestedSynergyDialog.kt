package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiObjects
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.StudentProfile

/**
 * Detailed Student Synergy Modal Dialog:
 * Shows deep compatibility metrics, icebreakers, and verified student credentials.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestedSynergyDialog(
  peer: StudentProfile,
  currentUser: StudentProfile,
  matchScore: Int,
  isConnected: Boolean,
  onDismiss: () -> Unit,
  onConnect: () -> Unit,
  onChat: () -> Unit
) {
  val commonInterests = peer.interests.intersect(currentUser.interests.toSet())
  val commonSkills = peer.skills.intersect(currentUser.skills.toSet())
  val commonGoals = peer.connectionPreferences.intersect(currentUser.connectionPreferences.toSet())
  val sameCollege = peer.collegeId == currentUser.collegeId ||
    peer.collegeName.equals(currentUser.collegeName, ignoreCase = true)
  val sameCourse = peer.course.equals(currentUser.course, ignoreCase = true)

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(28.dp)),
      shape = RoundedCornerShape(28.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        // Vibrant Top Header with Avatar & Match Meter
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.linearGradient(
                listOf(
                  Color(0xFF4A148C),
                  Color(0xFF651FFF),
                  Color(0xFFFF4081)
                )
              )
            )
            .padding(20.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              // Glowing Avatar
              Box(
                modifier = Modifier
                  .size(64.dp)
                  .border(
                    width = 2.5.dp,
                    color = Color.White,
                    shape = CircleShape
                  )
                  .background(Color(peer.avatarColorHex), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = peer.name.take(1).uppercase(),
                  fontSize = 26.sp,
                  fontWeight = FontWeight.Black,
                  color = Color.White
                )
              }

              Spacer(modifier = Modifier.width(14.dp))

              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = peer.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  if (peer.isVerified) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                      imageVector = Icons.Default.Verified,
                      contentDescription = "Verified",
                      tint = Color(0xFF00E5FF),
                      modifier = Modifier.size(18.dp)
                    )
                  }
                }
                Text(
                  text = "${peer.age} yrs · ${peer.gender}",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.85f)
                )
                Text(
                  text = peer.collegeName,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = Color(0xFF00E5FF)
                )
              }
            }

            IconButton(
              onClick = onDismiss,
              modifier = Modifier
                .size(32.dp)
                .background(Color.Black.copy(alpha = 0.25f), CircleShape)
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = Color.White,
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Match Synergy Score Banner
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.AutoAwesome,
                  contentDescription = null,
                  tint = Color(0xFFFFD600),
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "AI Match Synergy",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.8f)
                  )
                  Text(
                    text = "$matchScore% Compatible",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFD600)
              ) {
                Text(
                  text = if (matchScore >= 85) "High Synergy" else "Good Match",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1B0040),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }

        // Body Content
        Column(modifier = Modifier.padding(20.dp)) {
          // Bio Section
          if (peer.bio.isNotBlank()) {
            Text(
              text = "About",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "“${peer.bio}”",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(14.dp)
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
          }

          // Why You Match Breakdown Cards
          Text(
            text = "Synergy Breakdown",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(8.dp))

          // 1. Academic Alignment
          SynergyPointRow(
            icon = Icons.Default.School,
            iconTint = Color(0xFF651FFF),
            title = "Academic Alignment",
            subtitle = if (sameCollege && sameCourse) {
              "Same College & Same Course (${peer.course})"
            } else if (sameCollege) {
              "Same Campus (${peer.collegeName}) · ${peer.course}"
            } else {
              "${peer.collegeName} · ${peer.course}"
            }
          )

          Spacer(modifier = Modifier.height(8.dp))

          // 2. Shared Goals
          if (commonGoals.isNotEmpty()) {
            SynergyPointRow(
              icon = Icons.Default.Handshake,
              iconTint = Color(0xFFFF4081),
              title = "Shared Connection Goals",
              subtitle = "Both seeking: ${commonGoals.joinToString(", ")}"
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          // 3. Shared Skills & Interests
          if (commonSkills.isNotEmpty() || commonInterests.isNotEmpty()) {
            val allCommon = (commonSkills + commonInterests).distinct()
            SynergyPointRow(
              icon = Icons.Default.Star,
              iconTint = Color(0xFF00BFA5),
              title = "Shared Skills & Hobbies",
              subtitle = "${allCommon.size} common topics: ${allCommon.joinToString(", ")}"
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          // All Skills & Interests Chips
          if (peer.skills.isNotEmpty() || peer.interests.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "Skills & Hobbies",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp),
              verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              (peer.skills + peer.interests).distinct().forEach { tag ->
                val isShared = currentUser.skills.contains(tag) || currentUser.interests.contains(tag)
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = if (isShared) Color(0xFF00BFA5).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                ) {
                  Text(
                    text = if (isShared) "★ $tag" else tag,
                    fontSize = 11.sp,
                    fontWeight = if (isShared) FontWeight.Bold else FontWeight.Normal,
                    color = if (isShared) Color(0xFF00796B) else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Icebreaker Suggestions
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF651FFF).copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF651FFF).copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(14.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.EmojiObjects,
                contentDescription = null,
                tint = Color(0xFF651FFF),
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Conversation Starter",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF651FFF)
                )
                Text(
                  text = if (commonInterests.isNotEmpty()) {
                    "“Hey ${peer.name.split(" ").firstOrNull() ?: peer.name}! Saw we both love ${commonInterests.first()}. Would love to collaborate!”"
                  } else {
                    "“Hi ${peer.name.split(" ").firstOrNull() ?: peer.name}! Fellow student at ${peer.collegeName}. Would love to connect!”"
                  },
                  style = MaterialTheme.typography.bodySmall,
                  fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Action Buttons
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                onConnect()
                onDismiss()
              },
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isConnected) Color(0xFF00C853) else Color(0xFF651FFF)
              ),
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = if (isConnected) Icons.Default.Check else Icons.Default.PersonAdd,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (isConnected) "Connected ✓" else "Connect Now",
                fontWeight = FontWeight.Bold
              )
            }

            OutlinedButton(
              onClick = {
                onDismiss()
                onChat()
              },
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier.weight(1f)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.Chat,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(text = "Message", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun SynergyPointRow(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconTint: Color,
  title: String,
  subtitle: String
) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .fillMaxWidth()
      .background(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        RoundedCornerShape(12.dp)
      )
      .padding(10.dp)
  ) {
    Box(
      modifier = Modifier
        .size(34.dp)
        .background(iconTint.copy(alpha = 0.15f), CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconTint,
        modifier = Modifier.size(18.dp)
      )
    }

    Spacer(modifier = Modifier.width(10.dp))

    Column(modifier = Modifier.weight(1f)) {
      Text(
        text = title,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
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
