package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.ConnectionStatus
import com.example.data.model.Event
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LostFoundType
import com.example.data.model.Poll
import com.example.data.model.StudentProfile
import com.example.ui.components.CommentsBottomSheet
import com.example.ui.components.CampusHeroArt
import com.example.ui.components.CampusQuickActionGrid
import com.example.ui.components.CreateEventDialog
import com.example.ui.components.campusAmbientMesh
import com.example.ui.components.CreatePollDialog
import com.example.ui.components.CreatePostDialog
import com.example.ui.components.EventCard
import com.example.ui.components.EventsCarousel
import com.example.ui.components.FeedCard
import com.example.ui.components.PollCard
import com.example.ui.components.PollsCarousel
import com.example.ui.components.ReportDialog
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
  feedItems: List<FeedItem>,
  polls: List<Poll>,
  events: List<Event>,
  currentUser: StudentProfile,
  connectionStatus: ConnectionStatus,
  onLikeClick: (String) -> Unit,
  onAddComment: (postId: String, content: String) -> Unit,
  getComments: (postId: String) -> List<com.example.data.model.Comment>,
  onVotePoll: (pollId: String, optionIndex: Int) -> Unit,
  onCreatePoll: (question: String, options: List<String>, category: String) -> Unit,
  onToggleEventRsvp: (eventId: String) -> Unit,
  onCreateEvent: (title: String, desc: String, cat: String, date: String, time: String, venue: String, org: String) -> Unit,
  onCreateGeneralPost: (content: String) -> Unit,
  onCreateStudyPost: (subject: String, target: String, hours: String, loc: String, note: String) -> Unit,
  onCreateProjectPost: (title: String, stack: String, roles: String, deadline: String, note: String) -> Unit,
  onCreateLostFoundPost: (type: LostFoundType, loc: String, contact: String, note: String) -> Unit,
  onDeletePost: (String) -> Unit,
  onReportPost: (postId: String, reason: String) -> Unit,
  onMarkResolved: (String) -> Unit,
  onNavigateToChat: (recipientId: String) -> Unit,
  snackbarHostState: SnackbarHostState,
  onGoogleSignInClick: () -> Unit = {},
  onSearchUserClick: () -> Unit = {},
  onNavigateToLostFound: () -> Unit = {},
  onNavigateToStudy: () -> Unit = {},
  onNavigateToProjects: () -> Unit = {},
  onNavigateToEvents: () -> Unit = {},
  onNavigateToSuggested: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("All") }
  val filterCategories = listOf("All", "Posts", "Polls", "Events", "Study", "Projects", "Lost & Found", "Updates")

  var showCreatePostDialog by remember { mutableStateOf(false) }
  var showCreatePollDialog by remember { mutableStateOf(false) }
  var showCreateEventDialog by remember { mutableStateOf(false) }
  var activeCommentPost by remember { mutableStateOf<FeedItem?>(null) }
  var activeReportPostId by remember { mutableStateOf<String?>(null) }

  val coroutineScope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  // Track if user is currently at the top of the feed
  val isAtTop by remember {
    derivedStateOf {
      listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset < 150
    }
  }

  // Displayed items snapshot to prevent jumping users while reading lower in the feed
  var displayedFeedItems by remember { mutableStateOf(feedItems) }
  var pendingNewItemsCount by remember { mutableIntStateOf(0) }

  LaunchedEffect(feedItems) {
    if (isAtTop) {
      displayedFeedItems = feedItems
      pendingNewItemsCount = 0
    } else {
      val diff = feedItems.size - displayedFeedItems.size
      if (diff > 0) {
        pendingNewItemsCount = diff
      } else {
        displayedFeedItems = feedItems
      }
    }
  }

  LaunchedEffect(isAtTop) {
    if (isAtTop && pendingNewItemsCount > 0) {
      displayedFeedItems = feedItems
      pendingNewItemsCount = 0
    }
  }

  // Filter feed items based on selected category
  val filteredItems = remember(displayedFeedItems, selectedFilter) {
    when (selectedFilter) {
      "Posts" -> displayedFeedItems.filter { it.type == FeedItemType.GENERAL }
      "Study" -> displayedFeedItems.filter { it.type == FeedItemType.STUDY_PARTNER }
      "Projects" -> displayedFeedItems.filter { it.type == FeedItemType.PROJECT_PARTNER }
      "Lost & Found" -> displayedFeedItems.filter { it.type == FeedItemType.LOST_FOUND }
      "Updates" -> displayedFeedItems.filter { it.type == FeedItemType.CAMPUS_UPDATE }
      else -> displayedFeedItems
    }
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
        .testTag("home_feed_lazy_column"),
      contentPadding = PaddingValues(bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Campus Hero Visual Graphic Art
      item {
        CampusHeroArt(
          modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .testTag("campus_hero_art")
        )
      }

      // 2. Welcome & University Header Card
      item {
        HomeWelcomeHeader(
          currentUser = currentUser,
          onSearchUserClick = onSearchUserClick
        )
      }

      // 3. Quick Action Colorful Grid
      item {
        CampusQuickActionGrid(
          onSelectLostFound = onNavigateToLostFound,
          onSelectStudyPartner = onNavigateToStudy,
          onSelectProjectPartner = onNavigateToProjects,
          onSelectEvents = onNavigateToEvents
        )
      }

      // 4. Filter Chips Row
      item {
        FilterChipsRow(
          filters = filterCategories,
          selectedFilter = selectedFilter,
          onFilterSelected = { selectedFilter = it }
        )
      }

      // 3. Compact Highlights Strip on "All" filter to keep feed immediately accessible
      if (selectedFilter == "All") {
        item {
          CampusHighlightsStrip(
            pollsCount = polls.count { it.isActive },
            eventsCount = events.count { it.isUpcoming },
            onViewPolls = { selectedFilter = "Polls" },
            onViewEvents = { selectedFilter = "Events" }
          )
        }

        // AI Suggested Peers Spotlight Banner
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 2.dp)
              .clip(RoundedCornerShape(20.dp))
              .clickable { onNavigateToSuggested() }
              .testTag("home_suggested_peers_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(
                  Brush.horizontalGradient(
                    listOf(
                      Color(0xFF651FFF).copy(alpha = 0.08f),
                      Color(0xFFFF4081).copy(alpha = 0.08f)
                    )
                  )
                )
                .padding(14.dp)
            ) {
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
                      .size(42.dp)
                      .background(
                        Brush.linearGradient(
                          listOf(Color(0xFF7C4DFF), Color(0xFFFF4081))
                        ),
                        CircleShape
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(
                      imageVector = Icons.Default.AutoAwesome,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(22.dp)
                    )
                  }

                  Spacer(modifier = Modifier.width(12.dp))

                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "AI Suggested Peers",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                      )
                      Spacer(modifier = Modifier.width(6.dp))
                      Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFFF4081)
                      ) {
                        Text(
                          text = "AI RADAR",
                          fontSize = 9.sp,
                          fontWeight = FontWeight.Black,
                          color = Color.White,
                          modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                      }
                    }
                    Text(
                      text = "Find study buddies & project partners matching your skills",
                      style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color(0xFF651FFF)
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Text(
                      text = "Explore",
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.White
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                      imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                      contentDescription = null,
                      tint = Color.White,
                      modifier = Modifier.size(14.dp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Active Polls Carousel & Vertical List (shown when "Polls" is selected)
      if (selectedFilter == "Polls") {
        item {
          PollsCarousel(
            polls = polls,
            currentUser = currentUser,
            onVote = onVotePoll,
            onCreatePollClick = { showCreatePollDialog = true }
          )
        }

        item {
          Text(
            text = "Active Campus Polls (${polls.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
          )
        }

        items(polls, key = { "poll_${it.id}" }) { poll ->
          Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            PollCard(
              poll = poll,
              currentUserId = currentUser.id,
              onVote = { optIdx -> onVotePoll(poll.id, optIdx) },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      // 5. Upcoming Campus Events Carousel & Vertical List (shown when "Events" is selected)
      if (selectedFilter == "Events") {
        item {
          EventsCarousel(
            events = events,
            currentUser = currentUser,
            onToggleRsvp = onToggleEventRsvp,
            onCreateEventClick = { showCreateEventDialog = true }
          )
        }

        item {
          Text(
            text = "Upcoming Campus Events (${events.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
          )
        }

        items(events, key = { "event_${it.id}" }) { event ->
          Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            EventCard(
              event = event,
              currentUserId = currentUser.id,
              onToggleRsvp = { onToggleEventRsvp(event.id) },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }

      // 6. Feed Section Header
      if (selectedFilter != "Polls" && selectedFilter != "Events") {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (selectedFilter == "All") "Live Campus Feed" else "$selectedFilter Feed",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = "${filteredItems.size} items",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
          }
        }

        // Feed Items
        if (filteredItems.isEmpty()) {
          item {
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
              shape = RoundedCornerShape(16.dp)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  imageVector = Icons.Default.MenuBook,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.outline,
                  modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "No posts in this category yet.",
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        } else {
          if (selectedFilter == "All") {
            itemsIndexed(filteredItems, key = { _, item -> item.id }) { index, item ->
              Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FeedCard(
                  item = item,
                  currentUser = currentUser,
                  onLikeClick = { onLikeClick(item.id) },
                  onCommentClick = { activeCommentPost = item },
                  onPartnerActionClick = { onNavigateToChat(item.authorId) },
                  onMarkResolvedClick = { onMarkResolved(item.id) },
                  onDeleteClick = { onDeletePost(item.id) },
                  onReportClick = { activeReportPostId = item.id }
                )
              }

              // Dynamic content: Interleave trending poll into the live campus feed
              if (index == 1 && polls.any { it.isActive }) {
                val featuredPoll = polls.first { it.isActive }
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                  FeedCard(
                    poll = featuredPoll,
                    currentUserId = currentUser.id,
                    onVote = { optIdx -> onVotePoll(featuredPoll.id, optIdx) },
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }

              // Dynamic content: Interleave upcoming campus event into the live campus feed
              if (index == 3 && events.isNotEmpty()) {
                val featuredEvent = events.firstOrNull { it.isUpcoming } ?: events.first()
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                  FeedCard(
                    event = featuredEvent,
                    currentUserId = currentUser.id,
                    onToggleRsvp = { onToggleEventRsvp(featuredEvent.id) },
                    modifier = Modifier.fillMaxWidth()
                  )
                }
              }
            }
          } else {
            items(filteredItems, key = { it.id }) { item ->
              Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                FeedCard(
                  item = item,
                  currentUser = currentUser,
                  onLikeClick = { onLikeClick(item.id) },
                  onCommentClick = { activeCommentPost = item },
                  onPartnerActionClick = { onNavigateToChat(item.authorId) },
                  onMarkResolvedClick = { onMarkResolved(item.id) },
                  onDeleteClick = { onDeletePost(item.id) },
                  onReportClick = { activeReportPostId = item.id }
                )
              }
            }
          }
        }
      }
    }

    // Floating Smart Real-time Feed Pill: "3 new updates ↑" (Requirement 5)
    AnimatedVisibility(
      visible = pendingNewItemsCount > 0,
      enter = fadeIn() + slideInVertically(),
      exit = fadeOut() + slideOutVertically(),
      modifier = Modifier
        .align(Alignment.TopCenter)
        .padding(top = 16.dp)
    ) {
      Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF651FFF),
        shadowElevation = 6.dp,
        modifier = Modifier
          .clip(RoundedCornerShape(20.dp))
          .clickable {
            displayedFeedItems = feedItems
            pendingNewItemsCount = 0
            coroutineScope.launch {
              listState.animateScrollToItem(0)
            }
          }
          .testTag("floating_new_updates_pill")
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.ArrowUpward,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "$pendingNewItemsCount new updates ↑",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
      }
    }

    // Floating Action Button to create a post
    ExtendedFloatingActionButton(
      onClick = { showCreatePostDialog = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("home_fab_create_post"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = Color.White
    ) {
      Icon(Icons.Default.Add, contentDescription = "Create Post")
      Spacer(modifier = Modifier.width(6.dp))
      Text("Post / Ask", fontWeight = FontWeight.Bold)
    }

    // Modals
    if (showCreatePostDialog) {
      CreatePostDialog(
        currentUser = currentUser,
        onDismiss = { showCreatePostDialog = false },
        onCreateGeneral = onCreateGeneralPost,
        onCreateStudyPartner = onCreateStudyPost,
        onCreateProjectPartner = onCreateProjectPost,
        onCreateLostFound = onCreateLostFoundPost
      )
    }

    if (showCreatePollDialog) {
      CreatePollDialog(
        onDismiss = { showCreatePollDialog = false },
        onCreatePoll = onCreatePoll
      )
    }

    if (showCreateEventDialog) {
      CreateEventDialog(
        onDismiss = { showCreateEventDialog = false },
        onCreateEvent = { t, d, c, dt, tm, v, o ->
          onCreateEvent(t, d, c, dt, tm, v, o)
        }
      )
    }

    activeCommentPost?.let { post ->
      CommentsBottomSheet(
        post = post,
        comments = getComments(post.id),
        currentUser = currentUser,
        onAddComment = { content -> onAddComment(post.id, content) },
        onDismiss = { activeCommentPost = null }
      )
    }

    activeReportPostId?.let { postId ->
      ReportDialog(
        postId = postId,
        onDismiss = { activeReportPostId = null },
        onConfirmReport = { reason -> onReportPost(postId, reason) }
      )
    }
  }
}

@Composable
fun HomeWelcomeHeader(
  currentUser: StudentProfile,
  onSearchUserClick: () -> Unit = {}
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 4.dp),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            listOf(
              Color(0xFF651FFF), // Electric Violet
              Color(0xFF3D5AFE), // Royal Indigo
              Color(0xFF00B0FF)  // Cyber Cyan
            )
          )
        )
        .padding(18.dp)
    ) {
      Column {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.Black.copy(alpha = 0.25f)
          ) {
            Text(
              text = if (currentUser.collegeName.isNotBlank()) currentUser.collegeName.uppercase() else "ALL-CAMPUS STUDENT HUB",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.8.sp
              ),
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(10.dp),
            color = Color.White.copy(alpha = 0.2f)
          ) {
            Text(
              text = "⚡ Campus Live",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "Connect. Learn. Grow. Together.",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            fontSize = 19.sp
          ),
          color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "Hey ${currentUser.name.split(" ").first()}! ${currentUser.statusEmoji} ${currentUser.statusText}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.95f)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Quick Student Directory Search bar
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = Color.White.copy(alpha = 0.2f),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onSearchUserClick() }
            .testTag("home_search_students_bar")
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search Students",
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Search students, college, branch, skills...",
              style = MaterialTheme.typography.bodySmall,
              color = Color.White.copy(alpha = 0.85f)
            )
          }
        }
      }
    }
  }
}

@Composable
fun FilterChipsRow(
  filters: List<String>,
  selectedFilter: String,
  onFilterSelected: (String) -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .horizontalScroll(rememberScrollState())
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    filters.forEach { filter ->
      val isSelected = selectedFilter == filter
      FilterChip(
        selected = isSelected,
        onClick = { onFilterSelected(filter) },
        label = {
          Text(
            text = filter,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 13.sp
          )
        },
        shape = RoundedCornerShape(12.dp),
        colors = FilterChipDefaults.filterChipColors(
          selectedContainerColor = MaterialTheme.colorScheme.primary,
          selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          labelColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
      )
    }
  }
}

@Composable
fun CampusHighlightsStrip(
  pollsCount: Int,
  eventsCount: Int,
  onViewPolls: () -> Unit,
  onViewEvents: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    HighlightCard(
      title = "Live Polls",
      subtitle = "$pollsCount active votes",
      icon = Icons.Default.HowToVote,
      gradient = listOf(Color(0xFFFF6D00), Color(0xFFFF9100)),
      onClick = onViewPolls,
      modifier = Modifier.weight(1f)
    )

    HighlightCard(
      title = "Campus Events",
      subtitle = "$eventsCount upcoming",
      icon = Icons.Default.Event,
      gradient = listOf(Color(0xFF00B0FF), Color(0xFF00E5FF)),
      onClick = onViewEvents,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun HighlightCard(
  title: String,
  subtitle: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  gradient: List<Color>,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier.clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.horizontalGradient(gradient))
        .padding(12.dp)
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(34.dp)
            .background(Color.White.copy(alpha = 0.25f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
          Text(
            text = subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.9f)
          )
        }
      }
    }
  }
}
