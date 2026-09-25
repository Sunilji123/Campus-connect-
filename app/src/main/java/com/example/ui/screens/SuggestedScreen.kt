package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import com.example.data.model.StudentProfile
import com.example.data.repository.MgugRealtimeRepository
import com.example.ui.components.campusAmbientMesh

/**
 * SuggestedScreen (Campus Student Discovery & Synergy Matcher):
 * Redesigned with AI Radar animations, vibrant electric gradients,
 * rich match scoring, filter chips, trending topics, and full synergy profiles.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestedScreen(
  currentUser: StudentProfile,
  allProfiles: List<StudentProfile>,
  onConnectClick: (StudentProfile) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedScope by remember { mutableStateOf("All Campuses") }
  val connectedUserIds = remember { mutableStateMapOf<String, Boolean>() }
  var remainingConnects by remember { mutableIntStateOf(currentUser.freeSuggestionsRemaining) }
  var selectedSynergyPeer by remember { mutableStateOf<Pair<StudentProfile, Int>?>(null) }

  val listState = rememberLazyListState()

  val scopes = listOf(
    "All Campuses",
    "Same College",
    "Same Course",
    "Study Partners",
    "Project Partners",
    "Friends",
    "High Match (85%+)"
  )

  val trendingTags = listOf(
    "Python",
    "AI/ML",
    "DSA",
    "Web Dev",
    "UI/UX",
    "Hackathons",
    "Robotics",
    "Placement"
  )

  // Filter peers excluding current user
  val otherProfiles = remember(allProfiles, currentUser.id) {
    allProfiles.filter { it.id != currentUser.id }
  }

  // Calculate matching scores and filter
  val rankedProfiles = remember(otherProfiles, searchQuery, selectedScope, currentUser) {
    otherProfiles
      .map { peer ->
        var score = 50 // Base match score

        // Matching college bonus
        if (peer.collegeId == currentUser.collegeId || peer.collegeName.equals(currentUser.collegeName, ignoreCase = true)) {
          score += 25
        }

        // Matching course bonus
        if (peer.course.equals(currentUser.course, ignoreCase = true)) {
          score += 15
        }

        // Shared interests bonus
        val commonInterests = peer.interests.intersect(currentUser.interests.toSet())
        score += commonInterests.size * 6

        // Shared skills bonus
        val commonSkills = peer.skills.intersect(currentUser.skills.toSet())
        score += commonSkills.size * 6

        // Shared connection preferences bonus
        val commonGoals = peer.connectionPreferences.intersect(currentUser.connectionPreferences.toSet())
        score += commonGoals.size * 5

        val finalScore = score.coerceIn(58, 99)
        Pair(peer, finalScore)
      }
      .filter { (peer, matchScore) ->
        val matchesSearch = if (searchQuery.isBlank()) true else {
          val query = searchQuery.trim().lowercase()
          peer.name.lowercase().contains(query) ||
            peer.collegeName.lowercase().contains(query) ||
            peer.course.lowercase().contains(query) ||
            peer.department.lowercase().contains(query) ||
            peer.interests.any { it.lowercase().contains(query) } ||
            peer.skills.any { it.lowercase().contains(query) }
        }

        val matchesScope = when (selectedScope) {
          "Same College" -> peer.collegeId == currentUser.collegeId || peer.collegeName.equals(currentUser.collegeName, ignoreCase = true)
          "Same Course" -> peer.course.equals(currentUser.course, ignoreCase = true)
          "Study Partners" -> peer.connectionPreferences.contains("Study Partner")
          "Project Partners" -> peer.connectionPreferences.contains("Project Partner")
          "Friends" -> peer.connectionPreferences.contains("Friendship")
          "High Match (85%+)" -> matchScore >= 85
          else -> true
        }

        matchesSearch && matchesScope
      }
      .sortedByDescending { it.second }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .campusAmbientMesh()
  ) {
    LazyColumn(
      state = listState,
      modifier = Modifier
        .fillMaxSize()
        .testTag("suggested_screen"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 48.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Animated Campus AI Radar Hero Art
      item {
        SuggestedRadarHeroArt(studentCount = rankedProfiles.size)
      }

      // 2. Daily Connect Pass Energy Card
      item {
        SuggestedDailyPassWidget(
          remainingConnects = remainingConnects,
          onAddMoreConnects = {
            remainingConnects = (remainingConnects + 10).coerceAtMost(30)
            MgugRealtimeRepository.purchaseExtraSuggestions()
          }
        )
      }

      // 3. Search Bar with Gradient Accent & Clear Action
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search peers by name, skills, college…") },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFF651FFF)
              )
            },
            trailingIcon = {
              if (searchQuery.isNotEmpty()) {
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { searchQuery = "" }
                ) {
                  Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Clear",
                    modifier = Modifier
                      .padding(4.dp)
                      .size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            },
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedContainerColor = MaterialTheme.colorScheme.surface,
              unfocusedContainerColor = MaterialTheme.colorScheme.surface,
              focusedBorderColor = Color(0xFF651FFF),
              unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("suggested_search_input")
          )

          // Quick Trending Skill & Topic Chips
          Spacer(modifier = Modifier.height(10.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            trendingTags.forEach { tag ->
              val isTagActive = searchQuery.equals(tag, ignoreCase = true)
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isTagActive) Color(0xFF651FFF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.clickable {
                  searchQuery = if (isTagActive) "" else tag
                }
              ) {
                Text(
                  text = "#$tag",
                  fontSize = 11.sp,
                  fontWeight = if (isTagActive) FontWeight.Bold else FontWeight.Medium,
                  color = if (isTagActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }

      // 4. Scope Selection Tabs with Icons
      item {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          scopes.forEach { scope ->
            val isSelected = selectedScope == scope
            val icon = when (scope) {
              "Same College" -> "🏛️"
              "Same Course" -> "🎓"
              "Study Partners" -> "📚"
              "Project Partners" -> "💻"
              "Friends" -> "🤝"
              "High Match (85%+)" -> "⚡"
              else -> "✨"
            }

            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isSelected) Color(0xFF651FFF) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
              border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
              ),
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { selectedScope = scope }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(text = icon, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = scope,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                  fontSize = 12.sp,
                  color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }

      // 5. Results Counter and Sorting indicator
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = null,
              tint = Color(0xFF651FFF),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Suggested Students (${rankedProfiles.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFF651FFF).copy(alpha = 0.1f)
          ) {
            Text(
              text = "⚡ Ranked by AI Synergy",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF651FFF),
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      // 6. Empty State if no students found
      if (rankedProfiles.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .background(
                    Brush.linearGradient(
                      listOf(Color(0xFF651FFF), Color(0xFFFF4081))
                    ),
                    CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.School,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(28.dp)
                )
              }
              Spacer(modifier = Modifier.height(14.dp))
              Text(
                text = "No matching students found",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Try clearing your search query or selecting 'All Campuses' to discover students from neighboring universities.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp)
              )
              OutlinedButton(
                onClick = {
                  searchQuery = ""
                  selectedScope = "All Campuses"
                },
                shape = RoundedCornerShape(14.dp)
              ) {
                Text("Reset Filters & View All", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      // 7. Student Discovery Cards with Top Match Highlight
      val firstTopMatchId = rankedProfiles.firstOrNull { it.second >= 85 }?.first?.id

      items(rankedProfiles, key = { "suggested_${it.first.id}" }) { (peer, matchScore) ->
        val isConnected = connectedUserIds[peer.id] == true
        val isTopMatch = peer.id == firstTopMatchId && searchQuery.isBlank() && selectedScope == "All Campuses"

        SuggestedPeerCard(
          peer = peer,
          matchScore = matchScore,
          currentUser = currentUser,
          isConnected = isConnected,
          isTopMatch = isTopMatch,
          onConnectClick = {
            if (!isConnected) {
              connectedUserIds[peer.id] = true
              if (remainingConnects > 0) {
                remainingConnects--
              }
              MgugRealtimeRepository.connectWithPeer(peer.id)
              onConnectClick(peer)
            }
          },
          onChatClick = { onNavigateToChat(peer.id) },
          onViewDetailsClick = {
            selectedSynergyPeer = Pair(peer, matchScore)
          }
        )
      }
    }
  }

  // Synergy Modal Dialog
  selectedSynergyPeer?.let { (peer, matchScore) ->
    SuggestedSynergyDialog(
      peer = peer,
      currentUser = currentUser,
      matchScore = matchScore,
      isConnected = connectedUserIds[peer.id] == true,
      onDismiss = { selectedSynergyPeer = null },
      onConnect = {
        if (connectedUserIds[peer.id] != true) {
          connectedUserIds[peer.id] = true
          if (remainingConnects > 0) {
            remainingConnects--
          }
          MgugRealtimeRepository.connectWithPeer(peer.id)
          onConnectClick(peer)
        }
      },
      onChat = {
        onNavigateToChat(peer.id)
      }
    )
  }
}
