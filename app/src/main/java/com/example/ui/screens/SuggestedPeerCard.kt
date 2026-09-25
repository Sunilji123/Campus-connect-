package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile

/**
 * Beautifully Redesigned Student Discovery Card:
 * Features rich visual styling, avatar halo, match score gauge,
 * contextual compatibility tags, and direct connection affordances.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestedPeerCard(
  peer: StudentProfile,
  matchScore: Int,
  currentUser: StudentProfile,
  isConnected: Boolean,
  isTopMatch: Boolean = false,
  onConnectClick: () -> Unit,
  onChatClick: () -> Unit,
  onViewDetailsClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val sameCollege = peer.collegeId == currentUser.collegeId ||
    peer.collegeName.equals(currentUser.collegeName, ignoreCase = true)
  val sharedInterests = peer.interests.intersect(currentUser.interests.toSet())
  val sharedSkills = peer.skills.intersect(currentUser.skills.toSet())
  val sharedGoals = peer.connectionPreferences.intersect(currentUser.connectionPreferences.toSet())

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("peer_card_${peer.id}"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.elevatedCardElevation(
      defaultElevation = if (isTopMatch) 6.dp else 2.dp
    )
  ) {
    Column(modifier = Modifier.fillMaxWidth()) {
      // Top Gradient Accent Strip or Featured Banner
      if (isTopMatch) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFFFF007A),
                  Color(0xFF7928CA),
                  Color(0xFF00DFD8)
                )
              )
            )
            .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Stars,
                contentDescription = null,
                tint = Color(0xFFFFD600),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "TOP MATCH SPOTLIGHT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp,
                color = Color.White
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.25f)
            ) {
              Text(
                text = "🌟 Highest Academic Synergy",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }
      } else {
        // Subtle top gradient line
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
            .background(
              Brush.horizontalGradient(
                listOf(
                  Color(0xFF651FFF).copy(alpha = 0.4f),
                  Color(0xFFFF4081).copy(alpha = 0.4f),
                  Color(0xFF00B0FF).copy(alpha = 0.4f)
                )
              )
            )
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        // Top Row: Avatar with Glowing Halo + Student Info + Match Gauge
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.Top
        ) {
          // Avatar with Halo Border & Online Status Dot
          Box(
            modifier = Modifier.size(56.dp),
            contentAlignment = Alignment.Center
          ) {
            // Gradient Ring Halo
            Box(
              modifier = Modifier
                .size(56.dp)
                .background(
                  Brush.sweepGradient(
                    listOf(
                      Color(0xFF651FFF),
                      Color(0xFFFF4081),
                      Color(0xFF00E5FF),
                      Color(0xFF651FFF)
                    )
                  ),
                  CircleShape
                )
            )
            // Inner Avatar
            Box(
              modifier = Modifier
                .size(48.dp)
                .background(Color(peer.avatarColorHex), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = peer.name.take(1).uppercase(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = Color.White
              )
            }

            // Green Active Dot
            Box(
              modifier = Modifier
                .size(13.dp)
                .align(Alignment.BottomEnd)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .background(Color(0xFF00E676), CircleShape)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          // Student Core Info
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = peer.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (peer.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = "Verified Student",
                  tint = Color(0xFF00C853),
                  modifier = Modifier.size(16.dp)
                )
              }
            }

            Text(
              text = "${peer.year} · ${peer.age} yrs · ${peer.gender}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // College Badge
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(top = 2.dp)
            ) {
              Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = if (sameCollege) Color(0xFF651FFF) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = peer.collegeName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = if (sameCollege) Color(0xFF651FFF) else MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          // Match Score Pill Badge
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (matchScore >= 85) Color(0xFF651FFF).copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant,
            border = androidx.compose.foundation.BorderStroke(
              1.dp,
              if (matchScore >= 85) Color(0xFF651FFF).copy(alpha = 0.45f) else Color.Transparent
            ),
            modifier = Modifier.clickable { onViewDetailsClick() }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = if (matchScore >= 85) Color(0xFF651FFF) else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "$matchScore%",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = if (matchScore >= 85) Color(0xFF651FFF) else MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Course & Department
        Text(
          text = "${peer.course} · ${peer.department}",
          style = MaterialTheme.typography.bodySmall,
          fontWeight = FontWeight.Medium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // "Why You Match" Smart Compatibility Pill
        val reasons = mutableListOf<String>()
        if (sameCollege) reasons.add("Same College")
        if (sharedGoals.isNotEmpty()) reasons.add("Both want ${sharedGoals.first()}")
        if (sharedInterests.isNotEmpty()) reasons.add("Shared: ${sharedInterests.take(2).joinToString(", ")}")
        else if (sharedSkills.isNotEmpty()) reasons.add("Shared: ${sharedSkills.take(2).joinToString(", ")}")

        if (reasons.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF00BFA5).copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00BFA5).copy(alpha = 0.25f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFF00897B),
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = reasons.joinToString(" · "),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = Color(0xFF00796B),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Student Bio / Status Quote
        if (peer.bio.isNotBlank()) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "“${peer.bio}”",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 17.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        // Connection Preferences (Study Partner, Project, Friends)
        if (peer.connectionPreferences.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            peer.connectionPreferences.take(3).forEach { pref ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
              ) {
                Text(
                  text = when (pref) {
                    "Study Partner" -> "📚 Study Partner"
                    "Project Partner" -> "💻 Hackathons"
                    "Friendship" -> "🤝 Friends"
                    else -> "🎯 $pref"
                  },
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }

        // Shared & Peer Skills Chips
        if (peer.skills.isNotEmpty() || peer.interests.isNotEmpty()) {
          Spacer(modifier = Modifier.height(8.dp))
          FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
          ) {
            val tags = (peer.skills.take(2) + peer.interests.take(2)).distinct()
            tags.forEach { tag ->
              val isShared = currentUser.interests.contains(tag) || currentUser.skills.contains(tag)
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isShared) Color(0xFF00BFA5).copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
              ) {
                Text(
                  text = if (isShared) "★ $tag" else tag,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isShared) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (isShared) Color(0xFF00796B) else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row: Connect, Chat, and Synergy Details
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Connect Button with Gradient
          Button(
            onClick = onConnectClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isConnected) Color(0xFF00C853) else Color(0xFF651FFF)
            ),
            modifier = Modifier
              .weight(1.2f)
              .testTag("connect_btn_${peer.id}")
          ) {
            Icon(
              imageVector = if (isConnected) Icons.Default.Check else Icons.Default.PersonAdd,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (isConnected) "Connected ✓" else "Connect",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          // Message Button
          OutlinedButton(
            onClick = onChatClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .weight(1.1f)
              .testTag("chat_peer_btn_${peer.id}")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Chat,
              contentDescription = null,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Chat",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }

          // Synergy Info Button
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .clickable { onViewDetailsClick() }
          ) {
            Box(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 9.dp),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "View Synergy Details",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
