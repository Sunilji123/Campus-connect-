package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LOST_FOUND_CATEGORIES
import com.example.data.model.LostFoundStatus
import com.example.data.model.LostFoundType
import com.example.data.model.StudentProfile
import com.example.ui.components.campusAmbientMesh
import com.example.ui.util.FormatUtils

/**
 * Dedicated Campus Lost & Found screen with:
 * - Permanent active post persistence (never auto-deletes).
 * - Multi-criteria search (item name, location, description, college).
 * - Category & status filter chips.
 * - Strict backend & UI owner permissions (only creator/admin can edit, mark resolved, or delete).
 * - Privacy-first contact actions (in-app chat connection, no phone/email exposure).
 * - Three-dots More menu (Edit, Mark as Found/Returned, Delete for owner; Report for others).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostFoundView(
  feedItems: List<FeedItem>,
  currentUser: StudentProfile,
  onBack: () -> Unit,
  onCreateLostFoundPost: (type: LostFoundType, location: String, contact: String, note: String) -> Unit,
  onMarkResolved: (String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  modifier: Modifier = Modifier,
  onDeletePost: ((String) -> Unit)? = null,
  onUpdateStatus: ((String, LostFoundStatus) -> Unit)? = null,
  onEditPost: ((postId: String, itemName: String, description: String, category: String, location: String, itemDate: String, type: LostFoundType, status: LostFoundStatus) -> Unit)? = null,
  onReportPost: ((postId: String, reason: String) -> Unit)? = null,
  onCreateDetailedPost: ((type: LostFoundType, itemName: String, description: String, category: String, location: String, itemDate: String) -> Unit)? = null
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedStatusFilter by remember { mutableStateOf("All") }
  var selectedCategoryFilter by remember { mutableStateOf("All") }

  var showCreateDialog by remember { mutableStateOf(false) }
  var editingItem by remember { mutableStateOf<FeedItem?>(null) }
  var deletingItem by remember { mutableStateOf<FeedItem?>(null) }
  var reportingItem by remember { mutableStateOf<FeedItem?>(null) }

  val lostFoundItems = remember(feedItems) {
    feedItems.filter { it.type == FeedItemType.LOST_FOUND }
  }

  // Filter by search query, status/type, and category
  val filteredItems = remember(lostFoundItems, searchQuery, selectedStatusFilter, selectedCategoryFilter) {
    lostFoundItems.filter { item ->
      // Status filter
      val matchesStatus = when (selectedStatusFilter) {
        "Lost" -> item.lostFoundType == LostFoundType.LOST
        "Found" -> item.lostFoundType == LostFoundType.FOUND
        "Active" -> item.lostFoundStatus == LostFoundStatus.ACTIVE && !item.isResolved
        "Resolved / Returned" -> item.lostFoundStatus == LostFoundStatus.FOUND_RETURNED || item.isResolved
        else -> true
      }

      // Category filter
      val matchesCategory = if (selectedCategoryFilter == "All") {
        true
      } else {
        item.lostFoundCategory?.contains(selectedCategoryFilter.filter { it.isLetter() }, ignoreCase = true) == true ||
          item.content.contains(selectedCategoryFilter.filter { it.isLetter() }, ignoreCase = true) ||
          item.itemName?.contains(selectedCategoryFilter.filter { it.isLetter() }, ignoreCase = true) == true
      }

      // Search query filter
      val matchesSearch = if (searchQuery.isBlank()) {
        true
      } else {
        val q = searchQuery.trim().lowercase()
        (item.itemName?.lowercase()?.contains(q) == true) ||
          item.content.lowercase().contains(q) ||
          (item.campusLocation?.lowercase()?.contains(q) == true) ||
          (item.collegeName?.lowercase()?.contains(q) == true) ||
          item.authorName.lowercase().contains(q)
      }

      matchesStatus && matchesCategory && matchesSearch
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
            modifier = Modifier.testTag("lost_found_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          Column {
            Text(
              text = "Campus Lost & Found",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${lostFoundItems.count { it.lostFoundStatus == LostFoundStatus.ACTIVE && !it.isResolved }} active posts · Permanently kept",
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
        containerColor = Color(0xFFE53935),
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_create_lost_found")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Report Item", fontWeight = FontWeight.Bold)
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .campusAmbientMesh()
        .padding(padding)
    ) {
      // 1. Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        placeholder = { Text("Search by item name, location, college…", fontSize = 13.sp) },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { searchQuery = "" }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
          .testTag("input_search_lost_found")
      )

      // 2. Primary Status / Type Filters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        listOf("All", "Active", "Lost", "Found", "Resolved / Returned").forEach { filter ->
          FilterChip(
            selected = selectedStatusFilter == filter,
            onClick = { selectedStatusFilter = filter },
            label = {
              Text(
                text = when (filter) {
                  "Active" -> "⚡ Active"
                  "Lost" -> "🔍 Lost"
                  "Found" -> "🎁 Found"
                  "Resolved / Returned" -> "✅ Found / Returned"
                  else -> "All Items"
                },
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = if (selectedStatusFilter == filter) FontWeight.Bold else FontWeight.Normal)
              )
            },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("lf_filter_$filter")
          )
        }
      }

      // 3. Category Filter Chips Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState())
          .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        LOST_FOUND_CATEGORIES.forEach { category ->
          val isSelected = (selectedCategoryFilter == "All" && category == "All") || selectedCategoryFilter == category
          FilterChip(
            selected = isSelected,
            onClick = {
              selectedCategoryFilter = if (category == "All") "All" else category
            },
            label = { Text(category, fontSize = 11.sp) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("lf_cat_$category")
          )
        }
      }

      // 4. Content Feed
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
                .background(Color(0xFFE53935).copy(alpha = 0.12f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = if (searchQuery.isNotBlank()) "No items match \"$searchQuery\"" else "No $selectedStatusFilter Items Found",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Posts are stored permanently until deleted or resolved by the creator.",
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
          if (searchQuery.isBlank()) {
            item {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(22.dp))
                  .background(
                    Brush.linearGradient(
                      listOf(
                        Color(0xFFB71C1C), // Deep Crimson
                        Color(0xFFE53935), // Ruby Red
                        Color(0xFFFF5722), // Coral
                        Color(0xFFFF9800)  // Sunrise Amber
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
                      Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                      ) {
                        Text("✨", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                          text = "100% PERMANENT & SECURE",
                          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                          color = Color.White
                        )
                      }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                      text = "Lost something on campus?\nOur community has your back.",
                      style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 20.sp
                      )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = "Posts never expire · Chat in-app safely to reclaim",
                      style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Box(
                    modifier = Modifier
                      .size(54.dp)
                      .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text("🔍", fontSize = 28.sp)
                  }
                }
              }
            }
          }

          items(filteredItems, key = { it.id }) { item ->
            LostFoundCard(
              item = item,
              currentUser = currentUser,
              onMarkResolved = {
                if (onUpdateStatus != null) {
                  val newStatus = if (item.lostFoundStatus == LostFoundStatus.FOUND_RETURNED) {
                    LostFoundStatus.ACTIVE
                  } else {
                    LostFoundStatus.FOUND_RETURNED
                  }
                  onUpdateStatus(item.id, newStatus)
                } else {
                  onMarkResolved(item.id)
                }
              },
              onConnectClick = { onNavigateToChat(item.authorId) },
              onEditClick = { editingItem = item },
              onDeleteClick = { deletingItem = item },
              onReportClick = { reportingItem = item }
            )
          }
          item {
            Spacer(modifier = Modifier.height(72.dp))
          }
        }
      }
    }
  }

  // Create Dialog
  if (showCreateDialog) {
    CreateLostFoundDialog(
      currentUser = currentUser,
      onDismiss = { showCreateDialog = false },
      onSubmit = { type, title, desc, cat, loc, date ->
        if (onCreateDetailedPost != null) {
          onCreateDetailedPost(type, title, desc, cat, loc, date)
        } else {
          onCreateLostFoundPost(type, loc, "In-App Connect", "[$title] $desc ($cat · $date)")
        }
        showCreateDialog = false
      }
    )
  }

  // Edit Dialog
  editingItem?.let { item ->
    EditLostFoundDialog(
      item = item,
      onDismiss = { editingItem = null },
      onSubmit = { title, desc, cat, loc, date, type, status ->
        onEditPost?.invoke(item.id, title, desc, cat, loc, date, type, status)
        editingItem = null
      }
    )
  }

  // Delete Confirmation Dialog
  deletingItem?.let { item ->
    AlertDialog(
      onDismissRequest = { deletingItem = null },
      title = { Text("Delete Post Permanently?", fontWeight = FontWeight.Bold) },
      text = {
        Text("Are you sure you want to delete \"${item.itemName ?: item.content.take(30)}\"? This action cannot be undone.")
      },
      confirmButton = {
        Button(
          onClick = {
            onDeletePost?.invoke(item.id)
            deletingItem = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
          modifier = Modifier.testTag("confirm_delete_lost_found")
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { deletingItem = null }) {
          Text("Cancel")
        }
      }
    )
  }

  // Report Dialog
  reportingItem?.let { item ->
    var reportReason by remember { mutableStateOf("Inappropriate Content") }
    AlertDialog(
      onDismissRequest = { reportingItem = null },
      title = { Text("Report Post to Campus Moderators", fontWeight = FontWeight.Bold) },
      text = {
        Column {
          Text("Select the reason for reporting this post:", style = MaterialTheme.typography.bodySmall)
          Spacer(modifier = Modifier.height(10.dp))
          listOf("Inappropriate Content", "Spam / Fake Item", "Incorrect Details", "Already Claimed / Closed").forEach { r ->
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            ) {
              RadioButton(
                selected = reportReason == r,
                onClick = { reportReason = r }
              )
              Text(r, style = MaterialTheme.typography.bodyMedium)
            }
          }
        }
      },
      confirmButton = {
        Button(
          onClick = {
            onReportPost?.invoke(item.id, reportReason)
            reportingItem = null
          }
        ) {
          Text("Submit Report")
        }
      },
      dismissButton = {
        TextButton(onClick = { reportingItem = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
private fun LostFoundCard(
  item: FeedItem,
  currentUser: StudentProfile,
  onMarkResolved: () -> Unit,
  onConnectClick: () -> Unit,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onReportClick: () -> Unit
) {
  val isLost = item.lostFoundType == LostFoundType.LOST
  val isAuthor = item.authorId == currentUser.id || currentUser.email.contains("admin", ignoreCase = true)
  val isResolved = item.isResolved || item.lostFoundStatus == LostFoundStatus.FOUND_RETURNED
  var menuExpanded by remember { mutableStateOf(false) }

  val displayTitle = when {
    !item.itemName.isNullOrBlank() -> item.itemName
    item.content.startsWith("[") && item.content.contains("]") -> {
      item.content.substringAfter("[").substringBefore("]")
    }
    else -> if (isLost) "Lost Item" else "Found Item"
  }

  val displayDescription = when {
    item.content.startsWith("[") && item.content.contains("]") -> {
      item.content.substringAfter("]").trim()
    }
    else -> item.content
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("lost_found_card_${item.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
  ) {
    Column {
      // 1. Visual Banner Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(58.dp)
          .background(
            Brush.horizontalGradient(
              colors = if (isLost) {
                listOf(Color(0xFFB71C1C), Color(0xFFE53935))
              } else {
                listOf(Color(0xFF00695C), Color(0xFF00897B))
              }
            )
          )
          .padding(horizontal = 14.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.Black.copy(alpha = 0.28f)
            ) {
              Text(
                text = if (isLost) "🔍 LOST ITEM" else "🎁 FOUND ITEM",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }

            if (!item.lostFoundCategory.isNullOrBlank()) {
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.22f)
              ) {
                Text(
                  text = item.lostFoundCategory,
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                )
              }
            }
          }

          // Status Badge
          if (isResolved) {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.White.copy(alpha = 0.95f)
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.CheckCircle,
                  contentDescription = null,
                  tint = Color(0xFF2E7D32),
                  modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "FOUND / RETURNED",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF2E7D32)
                )
              }
            }
          } else {
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.Black.copy(alpha = 0.3f)
            ) {
              Text(
                text = "⚡ ACTIVE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF80DEEA),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }
      }

      Column(modifier = Modifier.padding(16.dp)) {
        // Author details & Three-dots Menu
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .background(Color(item.authorColorHex), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = item.authorName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = item.authorName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${item.authorFaculty} · ${item.collegeName ?: "Campus"}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // STRICT UI RULE: ⋮ More menu
          Box {
            IconButton(
              onClick = { menuExpanded = true },
              modifier = Modifier.testTag("btn_more_menu_${item.id}")
            ) {
              Icon(Icons.Default.MoreVert, contentDescription = "More Options")
            }

            DropdownMenu(
              expanded = menuExpanded,
              onDismissRequest = { menuExpanded = false }
            ) {
              if (isAuthor) {
                DropdownMenuItem(
                  text = { Text("Edit Post") },
                  leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                  onClick = {
                    menuExpanded = false
                    onEditClick()
                  },
                  modifier = Modifier.testTag("menu_edit_${item.id}")
                )
                DropdownMenuItem(
                  text = {
                    Text(if (isResolved) "Mark as Active" else "Mark as Found/Returned")
                  },
                  leadingIcon = { Icon(Icons.Default.Check, contentDescription = null) },
                  onClick = {
                    menuExpanded = false
                    onMarkResolved()
                  },
                  modifier = Modifier.testTag("menu_mark_status_${item.id}")
                )
                DropdownMenuItem(
                  text = { Text("Delete Post", color = MaterialTheme.colorScheme.error) },
                  leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                  onClick = {
                    menuExpanded = false
                    onDeleteClick()
                  },
                  modifier = Modifier.testTag("menu_delete_${item.id}")
                )
              } else {
                DropdownMenuItem(
                  text = { Text("Report Post") },
                  leadingIcon = { Icon(Icons.Default.Report, contentDescription = null) },
                  onClick = {
                    menuExpanded = false
                    onReportClick()
                  },
                  modifier = Modifier.testTag("menu_report_${item.id}")
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prominent Item Name
        Text(
          text = displayTitle,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Content Description
        Text(
          text = displayDescription,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Location & Date Box (Privacy compliant: NEVER shows phone/email)
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            if (!item.campusLocation.isNullOrBlank()) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.LocationOn,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Campus Spot: ${item.campusLocation}",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(13.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = item.itemDate ?: FormatUtils.formatRelativeTime(item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Privacy Assurance Note
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Safe & Private: Contact via in-app chat only",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF2E7D32),
                fontWeight = FontWeight.Medium
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Action Buttons Row (NO large Delete button here — Delete is in ⋮ More menu!)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically
        ) {
          if (isAuthor && !isResolved) {
            OutlinedButton(
              onClick = onMarkResolved,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("btn_mark_resolved_${item.id}")
            ) {
              Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Mark as Returned", fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          if (!isAuthor) {
            Button(
              onClick = onConnectClick,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (isLost) Color(0xFFE53935) else Color(0xFF00897B)
              ),
              modifier = Modifier.testTag("btn_contact_finder_${item.id}")
            ) {
              Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(15.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                if (isLost) "I Found This · Chat" else "Claim Item · Chat",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CreateLostFoundDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (type: LostFoundType, title: String, desc: String, category: String, location: String, date: String) -> Unit
) {
  var lostFoundType by remember { mutableStateOf(LostFoundType.LOST) }
  var itemName by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("📱 Electronics") }
  var campusLocation by remember { mutableStateOf("") }
  var itemDate by remember { mutableStateOf("Today, just now") }
  var description by remember { mutableStateOf("") }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_create_lost_found")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Report Lost or Found Item",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )
        Text(
          text = "Posts are kept permanently active until resolved or deleted by you.",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Toggle Lost / Found
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            RadioButton(
              selected = lostFoundType == LostFoundType.LOST,
              onClick = { lostFoundType = LostFoundType.LOST }
            )
            Text("I Lost an Item", style = MaterialTheme.typography.bodyMedium)
          }

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            RadioButton(
              selected = lostFoundType == LostFoundType.FOUND,
              onClick = { lostFoundType = LostFoundType.FOUND }
            )
            Text("I Found an Item", style = MaterialTheme.typography.bodyMedium)
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = itemName,
          onValueChange = { itemName = it },
          label = { Text("Item Name") },
          placeholder = { Text("e.g. Casio Calculator, Black Wallet, Keys") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_lf_item_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category Selection
        Text("Category:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          LOST_FOUND_CATEGORIES.filter { it != "All" }.forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = campusLocation,
          onValueChange = { campusLocation = it },
          label = { Text("Campus Spot / Location") },
          placeholder = { Text("e.g. Central Library 2nd Floor, Cafeteria") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_lf_location")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = itemDate,
          onValueChange = { itemDate = it },
          label = { Text("Date & Time") },
          placeholder = { Text("e.g. Today around 11:30 AM") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description & Distinguishing Features") },
          placeholder = { Text("Include stickers, color, condition, model...") },
          minLines = 2,
          maxLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_lf_description")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Privacy Banner
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFF2E7D32).copy(alpha = 0.08f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              "Privacy Protected: In-app chat is used for replies. Phone number & email remain strictly private.",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF2E7D32)
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

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
              if (itemName.isNotBlank() && description.isNotBlank()) {
                onSubmit(lostFoundType, itemName.trim(), description.trim(), category, campusLocation.trim(), itemDate.trim())
              }
            },
            enabled = itemName.isNotBlank() && description.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (lostFoundType == LostFoundType.LOST) Color(0xFFE53935) else Color(0xFF00897B)
            ),
            modifier = Modifier.testTag("btn_submit_lost_found")
          ) {
            Text("Post Notice", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun EditLostFoundDialog(
  item: FeedItem,
  onDismiss: () -> Unit,
  onSubmit: (title: String, desc: String, category: String, location: String, date: String, type: LostFoundType, status: LostFoundStatus) -> Unit
) {
  var itemName by remember {
    mutableStateOf(
      item.itemName ?: if (item.content.startsWith("[") && item.content.contains("]")) {
        item.content.substringAfter("[").substringBefore("]")
      } else "Item"
    )
  }
  var description by remember {
    mutableStateOf(
      if (item.content.startsWith("[") && item.content.contains("]")) {
        item.content.substringAfter("]").trim()
      } else item.content
    )
  }
  var category by remember { mutableStateOf(item.lostFoundCategory ?: "📱 Electronics") }
  var campusLocation by remember { mutableStateOf(item.campusLocation ?: "") }
  var itemDate by remember { mutableStateOf(item.itemDate ?: "Today") }
  var type by remember { mutableStateOf(item.lostFoundType ?: LostFoundType.LOST) }
  var status by remember { mutableStateOf(item.lostFoundStatus) }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_edit_lost_found")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Edit Lost & Found Report",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Status Selection
        Text("Status:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(
            LostFoundStatus.ACTIVE to "⚡ Active",
            LostFoundStatus.FOUND_RETURNED to "✅ Returned",
            LostFoundStatus.CLOSED to "🔒 Closed"
          ).forEach { (st, label) ->
            FilterChip(
              selected = status == st,
              onClick = { status = st },
              label = { Text(label, fontSize = 11.sp) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = itemName,
          onValueChange = { itemName = it },
          label = { Text("Item Name") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_edit_item_name")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = campusLocation,
          onValueChange = { campusLocation = it },
          label = { Text("Campus Spot / Location") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = itemDate,
          onValueChange = { itemDate = it },
          label = { Text("Date & Time") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          minLines = 2,
          maxLines = 3,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

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
              onSubmit(itemName.trim(), description.trim(), category, campusLocation.trim(), itemDate.trim(), type, status)
            },
            enabled = itemName.isNotBlank() && description.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("btn_save_edit_lost_found")
          ) {
            Text("Save Changes", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
