package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.StudentProfile
import com.example.ui.components.campusAmbientMesh

/**
 * Dedicated Project Partner & Hackathon Recruitment Screen:
 * - Tech chips (Python, AI/ML, React, Flutter, Java, Data Science, etc.)
 * - Required skills & members needed
 * - Realtime updates
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProjectPartnerView(
  feedItems: List<FeedItem>,
  currentUser: StudentProfile,
  onBack: () -> Unit,
  onCreateProjectPost: (title: String, stack: String, roles: String, deadline: String, note: String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedTechFilter by remember { mutableStateOf("All") }
  var showCreateDialog by remember { mutableStateOf(false) }

  val projectItems = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.PROJECT_PARTNER }
  }

  val techFilterOptions = listOf("All", "Python", "AI/ML", "React", "Flutter", "Java", "Data Science")

  val filteredItems = remember(projectItems, selectedTechFilter) {
    if (selectedTechFilter == "All") projectItems
    else projectItems.filter {
      it.techStack?.contains(selectedTechFilter, ignoreCase = true) == true ||
        it.content.contains(selectedTechFilter, ignoreCase = true)
    }
  }

  Scaffold(
    topBar = {
      TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.surface
        ),
        navigationIcon = {
          IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("project_partner_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          Column {
            Text(
              text = "Project & Hackathon Teams",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${projectItems.size} active project recruitments",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      )
    },
    floatingActionButton = {
      ExtendedFloatingActionButton(
        onClick = { showCreateDialog = true },
        containerColor = Color(0xFF1565C0),
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_create_project_partner")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Recruit Teammates", fontWeight = FontWeight.Bold)
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .campusAmbientMesh()
        .padding(padding)
    ) {
      // Tech filter chips
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        techFilterOptions.forEach { tech ->
          FilterChip(
            selected = selectedTechFilter == tech,
            onClick = { selectedTechFilter = tech },
            label = { Text(tech, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }

      if (filteredItems.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
              modifier = Modifier
                .size(72.dp)
                .background(Color(0xFF1565C0).copy(alpha = 0.12f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Code,
                contentDescription = null,
                tint = Color(0xFF1565C0),
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "No $selectedTechFilter Projects",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Start a project squad or hackathon team and recruit skilled student developers.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        LazyColumn(
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
          verticalArrangement = Arrangement.spacedBy(14.dp),
          modifier = Modifier.fillMaxSize()
        ) {
          item {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                  androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(
                      Color(0xFF311B92), // Deep Violet
                      Color(0xFF651FFF), // Electric Violet
                      Color(0xFF00B0FF), // Cyber Cyan
                      Color(0xFF00E5FF)  // Bright Turquoise
                    )
                  )
                )
                .padding(16.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.28f)
                  ) {
                    Text(
                      text = "⚡ HACKATHON & BUILD SQUADS",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Build Cool Tech Together.",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Recruit developers, designers, and AI creators across all university faculties.",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                  modifier = Modifier
                    .size(52.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text("💻", fontSize = 28.sp)
                }
              }
            }
          }

          items(filteredItems, key = { it.id }) { item ->
            ProjectPartnerCard(
              item = item,
              currentUser = currentUser,
              onConnectClick = { onNavigateToChat(item.authorId) }
            )
          }
          item {
            Spacer(modifier = Modifier.height(70.dp))
          }
        }
      }
    }
  }

  if (showCreateDialog) {
    CreateProjectPartnerDialog(
      currentUser = currentUser,
      onDismiss = { showCreateDialog = false },
      onSubmit = { title, stack, roles, deadline, note ->
        onCreateProjectPost(title, stack, roles, deadline, note)
        showCreateDialog = false
      }
    )
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProjectPartnerCard(
  item: FeedItem,
  currentUser: StudentProfile,
  onConnectClick: () -> Unit
) {
  val isAuthor = item.authorId == currentUser.id

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("project_partner_card_${item.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(40.dp)
              .background(Color(item.authorColorHex), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.authorName.take(1).uppercase(),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.authorName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = "${item.authorFaculty} · ${item.authorYear}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color(0xFF1565C0).copy(alpha = 0.12f)
        ) {
          Text(
            text = "💻 Project Team",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF1565C0),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Project Title Banner
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1565C0).copy(alpha = 0.08f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = item.projectTitle ?: "Campus Innovation Project",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF0D47A1)
          )

          if (!item.rolesNeeded.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Group, contentDescription = null, tint = Color(0xFF1565C0), modifier = Modifier.size(14.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Looking for: ${item.rolesNeeded}",
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Tech Stack Chips
      if (!item.techStack.isNullOrBlank()) {
        val techTokens = item.techStack.split(",", "•", "|").map { it.trim() }.filter { it.isNotBlank() }
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          techTokens.forEach { token ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            ) {
              Text(
                text = token,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
        Spacer(modifier = Modifier.height(10.dp))
      }

      // Content description
      Text(
        text = item.content,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
      )

      if (!item.deadline.isNullOrBlank()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Target / Deadline: ${item.deadline}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.error
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Connect Button
      if (!isAuthor) {
        Button(
          onClick = onConnectClick,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("btn_connect_project_${item.id}")
        ) {
          Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Join Team · Connect", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}

@Composable
private fun CreateProjectPartnerDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (title: String, stack: String, roles: String, deadline: String, note: String) -> Unit
) {
  var projectTitle by remember { mutableStateOf("") }
  var selectedTechs by remember { mutableStateOf(setOf<String>()) }
  var customTech by remember { mutableStateOf("") }
  var rolesNeeded by remember { mutableStateOf("") }
  var deadline by remember { mutableStateOf("") }
  var note by remember { mutableStateOf("") }

  val popularTechs = listOf("Python", "AI/ML", "React", "Flutter", "Java", "Data Science", "Kotlin", "Node.js", "Firebase", "Figma")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_create_project_partner")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Recruit Project / Hackathon Team",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = projectTitle,
          onValueChange = { projectTitle = it },
          label = { Text("Project Title") },
          placeholder = { Text("e.g. AI Smart Campus Assistant, FinTech App") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_project_title")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "TECHNOLOGIES",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          popularTechs.take(4).forEach { tech ->
            FilterChip(
              selected = selectedTechs.contains(tech),
              onClick = {
                selectedTechs = if (selectedTechs.contains(tech)) selectedTechs - tech else selectedTechs + tech
              },
              label = { Text(tech, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          popularTechs.drop(4).take(4).forEach { tech ->
            FilterChip(
              selected = selectedTechs.contains(tech),
              onClick = {
                selectedTechs = if (selectedTechs.contains(tech)) selectedTechs - tech else selectedTechs + tech
              },
              label = { Text(tech, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = rolesNeeded,
          onValueChange = { rolesNeeded = it },
          label = { Text("Roles / Members Needed") },
          placeholder = { Text("e.g. 1 Frontend Dev, 1 UI/UX Designer, 1 ML Engineer") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_project_roles")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = deadline,
          onValueChange = { deadline = it },
          label = { Text("Target Event / Deadline") },
          placeholder = { Text("e.g. Smart India Hackathon 2026, Semester Project") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_project_deadline")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = note,
          onValueChange = { note = it },
          label = { Text("Project Pitch & Description") },
          placeholder = { Text("Briefly explain your idea and what you plan to build…") },
          minLines = 3,
          maxLines = 4,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_project_note")
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(12.dp)
          ) {
            Text("Cancel")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              if (projectTitle.isNotBlank() && note.isNotBlank()) {
                val fullTechStack = (selectedTechs + customTech.split(",").map { it.trim() }.filter { it.isNotBlank() }).joinToString(", ")
                onSubmit(projectTitle, fullTechStack, rolesNeeded, deadline, note)
              }
            },
            enabled = projectTitle.isNotBlank() && note.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
            modifier = Modifier.testTag("btn_submit_project_partner")
          ) {
            Text("Publish Recruitment", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
