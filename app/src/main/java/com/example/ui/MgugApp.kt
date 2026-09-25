package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.repository.MgugRealtimeRepository
import com.example.ui.components.CampusDrawerContent
import com.example.ui.components.ContactUsDialog
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.GoogleSignInDialog
import com.example.ui.components.MgugTopAppBar
import com.example.ui.components.SearchUserDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.EventsView
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LostFoundView
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProjectPartnerView
import com.example.ui.screens.StudyPartnerView
import com.example.ui.screens.SuggestedScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class DrawerDestination {
  NONE,
  LOST_FOUND,
  STUDY_PARTNER,
  PROJECT_PARTNER,
  EVENTS
}

@Composable
fun MgugApp() {
  val context = LocalContext.current
  LaunchedEffect(context) {
    MgugRealtimeRepository.setContext(context)
  }

  val currentUser by MgugRealtimeRepository.currentUser.collectAsState()
  val allProfiles by MgugRealtimeRepository.allProfiles.collectAsState()
  val connectionStatus by MgugRealtimeRepository.connectionStatus.collectAsState()
  val feedItems by MgugRealtimeRepository.feedItems.collectAsState()
  val polls by MgugRealtimeRepository.polls.collectAsState()
  val events by MgugRealtimeRepository.events.collectAsState()
  val notifications by MgugRealtimeRepository.notifications.collectAsState()

  val unreadNotificationsCount = remember(notifications, currentUser) {
    notifications.count { it.userId == currentUser.id && !it.isRead }
  }

  val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
  val coroutineScope = rememberCoroutineScope()

  // Exactly 4 bottom tabs: 0 = Home, 1 = Suggested, 2 = Chat, 3 = Profile
  var selectedTab by remember { mutableIntStateOf(0) }
  var drawerDestination by remember { mutableStateOf(DrawerDestination.NONE) }
  var isViewingNotifications by remember { mutableStateOf(false) }
  var directChatRecipientId by remember { mutableStateOf<String?>(null) }
  var showGoogleSignInDialog by remember { mutableStateOf(false) }
  var showSearchUserDialog by remember { mutableStateOf(false) }
  var showContactUsDialog by remember { mutableStateOf(false) }
  var showFeedbackDialog by remember { mutableStateOf(false) }
  var showSettingsDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }

  // Real-time Event Stream Listener (shows immediate feedback when live operations happen across peers)
  LaunchedEffect(Unit) {
    MgugRealtimeRepository.eventStream.collectLatest { eventMsg ->
      snackbarHostState.showSnackbar(
        message = eventMsg,
        withDismissAction = true
      )
    }
  }

  ModalNavigationDrawer(
    drawerState = drawerState,
    gesturesEnabled = drawerDestination == DrawerDestination.NONE,
    drawerContent = {
      CampusDrawerContent(
        currentUser = currentUser,
        onSelectLostFound = {
          coroutineScope.launch { drawerState.close() }
          drawerDestination = DrawerDestination.LOST_FOUND
          isViewingNotifications = false
        },
        onSelectStudyPartner = {
          coroutineScope.launch { drawerState.close() }
          drawerDestination = DrawerDestination.STUDY_PARTNER
          isViewingNotifications = false
        },
        onSelectProjectPartner = {
          coroutineScope.launch { drawerState.close() }
          drawerDestination = DrawerDestination.PROJECT_PARTNER
          isViewingNotifications = false
        },
        onSelectEvents = {
          coroutineScope.launch { drawerState.close() }
          drawerDestination = DrawerDestination.EVENTS
          isViewingNotifications = false
        },
        onSelectContactUs = {
          coroutineScope.launch { drawerState.close() }
          showContactUsDialog = true
        },
        onSelectFeedback = {
          coroutineScope.launch { drawerState.close() }
          showFeedbackDialog = true
        },
        onSelectSettings = {
          coroutineScope.launch { drawerState.close() }
          showSettingsDialog = true
        },
        onProfileClick = {
          coroutineScope.launch { drawerState.close() }
          selectedTab = 3
          drawerDestination = DrawerDestination.NONE
          isViewingNotifications = false
        }
      )
    }
  ) {
    Scaffold(
      topBar = {
        if (drawerDestination == DrawerDestination.NONE) {
          MgugTopAppBar(
            currentUser = currentUser,
            connectionStatus = connectionStatus,
            unreadNotifications = unreadNotificationsCount,
            onMenuClick = {
              coroutineScope.launch { drawerState.open() }
            },
            onNotificationsClick = {
              isViewingNotifications = !isViewingNotifications
            },
            onProfileClick = {
              selectedTab = 3
              isViewingNotifications = false
              drawerDestination = DrawerDestination.NONE
            },
            onToggleConnection = {
              MgugRealtimeRepository.toggleNetworkSimulation()
            },
            onGoogleSignInClick = {
              showGoogleSignInDialog = true
            },
            onSearchUserClick = {
              showSearchUserDialog = true
            }
          )
        }
      },
      bottomBar = {
        if (drawerDestination == DrawerDestination.NONE) {
          NavigationBar(
            modifier = Modifier.testTag("mgug_bottom_navigation"),
            containerColor = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
          ) {
            // 1. Home
            NavigationBarItem(
              selected = selectedTab == 0 && !isViewingNotifications,
              onClick = {
                selectedTab = 0
                isViewingNotifications = false
              },
              icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
              label = { Text("Home", fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag("nav_home")
            )
            // 2. Suggested
            NavigationBarItem(
              selected = selectedTab == 1 && !isViewingNotifications,
              onClick = {
                selectedTab = 1
                isViewingNotifications = false
              },
              icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "Suggested") },
              label = { Text("Suggested", fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag("nav_suggested")
            )
            // 3. Chat
            NavigationBarItem(
              selected = selectedTab == 2 && !isViewingNotifications,
              onClick = {
                selectedTab = 2
                isViewingNotifications = false
              },
              icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat") },
              label = { Text("Chat", fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag("nav_chat")
            )
            // 4. Profile
            NavigationBarItem(
              selected = selectedTab == 3 && !isViewingNotifications,
              onClick = {
                selectedTab = 3
                isViewingNotifications = false
              },
              icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
              label = { Text("Profile", fontWeight = FontWeight.SemiBold) },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag("nav_profile")
            )
          }
        }
      },
      snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when {
          drawerDestination == DrawerDestination.LOST_FOUND -> {
            LostFoundView(
              feedItems = feedItems,
              currentUser = currentUser,
              onBack = { drawerDestination = DrawerDestination.NONE },
              onCreateLostFoundPost = { type, loc, con, note ->
                MgugRealtimeRepository.createLostFoundPost(type, loc, con, note)
              },
              onMarkResolved = { id ->
                MgugRealtimeRepository.markLostFoundResolved(id)
              },
              onNavigateToChat = { recId ->
                directChatRecipientId = recId
                selectedTab = 2
                drawerDestination = DrawerDestination.NONE
              },
              onDeletePost = { postId ->
                MgugRealtimeRepository.deletePost(postId)
              },
              onUpdateStatus = { postId, status ->
                MgugRealtimeRepository.markLostFoundStatus(postId, status)
              },
              onEditPost = { postId, title, desc, cat, loc, date, type, status ->
                MgugRealtimeRepository.updateLostFoundPost(postId, title, desc, cat, loc, date, type, status)
              },
              onReportPost = { postId, reason ->
                MgugRealtimeRepository.reportPost(postId, reason)
              },
              onCreateDetailedPost = { type, title, desc, cat, loc, date ->
                MgugRealtimeRepository.createLostFoundPost(type, title, desc, cat, loc, date)
              }
            )
          }

          drawerDestination == DrawerDestination.STUDY_PARTNER -> {
            StudyPartnerView(
              feedItems = feedItems,
              currentUser = currentUser,
              onBack = { drawerDestination = DrawerDestination.NONE },
              onCreateStudyPost = { subj, target, hours, loc, note ->
                MgugRealtimeRepository.createStudyPartnerPost(subj, target, hours, loc, note)
              },
              onNavigateToChat = { recId ->
                directChatRecipientId = recId
                selectedTab = 2
                drawerDestination = DrawerDestination.NONE
              }
            )
          }

          drawerDestination == DrawerDestination.PROJECT_PARTNER -> {
            ProjectPartnerView(
              feedItems = feedItems,
              currentUser = currentUser,
              onBack = { drawerDestination = DrawerDestination.NONE },
              onCreateProjectPost = { title, stack, roles, deadline, note ->
                MgugRealtimeRepository.createProjectPartnerPost(title, stack, roles, deadline, note)
              },
              onNavigateToChat = { recId ->
                directChatRecipientId = recId
                selectedTab = 2
                drawerDestination = DrawerDestination.NONE
              }
            )
          }

          drawerDestination == DrawerDestination.EVENTS -> {
            EventsView(
              events = events,
              currentUser = currentUser,
              onBack = { drawerDestination = DrawerDestination.NONE },
              onToggleEventRsvp = { id ->
                MgugRealtimeRepository.toggleEventRsvp(id)
              },
              onCreateEvent = { t, d, c, dt, tm, v, o ->
                MgugRealtimeRepository.createEvent(t, d, c, dt, tm, v, o, currentUser.email)
              }
            )
          }

          isViewingNotifications -> {
            NotificationsScreen(
              notifications = notifications.filter { it.userId == currentUser.id },
              onMarkAllRead = {
                MgugRealtimeRepository.markNotificationsRead()
              },
              onDismissNotification = { notifId ->
                MgugRealtimeRepository.dismissNotification(notifId)
              },
              onClearAll = {
                MgugRealtimeRepository.clearAllNotifications()
              }
            )
          }

          else -> {
            when (selectedTab) {
              0 -> HomeScreen(
                feedItems = feedItems,
                polls = polls,
                events = events,
                currentUser = currentUser,
                connectionStatus = connectionStatus,
                onLikeClick = { postId -> MgugRealtimeRepository.toggleLike(postId) },
                onAddComment = { postId, content -> MgugRealtimeRepository.addComment(postId, content) },
                getComments = { postId -> MgugRealtimeRepository.getCommentsForPost(postId) },
                onVotePoll = { pollId, optIdx -> MgugRealtimeRepository.votePoll(pollId, optIdx) },
                onCreatePoll = { q, opts, cat -> MgugRealtimeRepository.createPoll(q, opts, cat) },
                onToggleEventRsvp = { eventId -> MgugRealtimeRepository.toggleEventRsvp(eventId) },
                onCreateEvent = { t, d, c, dt, tm, v, o ->
                  MgugRealtimeRepository.createEvent(t, d, c, dt, tm, v, o, currentUser.email)
                },
                onCreateGeneralPost = { content -> MgugRealtimeRepository.createGeneralPost(content) },
                onCreateStudyPost = { s, t, h, l, n -> MgugRealtimeRepository.createStudyPartnerPost(s, t, h, l, n) },
                onCreateProjectPost = { t, s, r, d, n -> MgugRealtimeRepository.createProjectPartnerPost(t, s, r, d, n) },
                onCreateLostFoundPost = { type, loc, con, n -> MgugRealtimeRepository.createLostFoundPost(type, loc, con, n) },
                onDeletePost = { postId -> MgugRealtimeRepository.deletePost(postId) },
                onReportPost = { postId, reason -> MgugRealtimeRepository.reportPost(postId, reason) },
                onMarkResolved = { postId -> MgugRealtimeRepository.markLostFoundResolved(postId) },
                onNavigateToChat = { recipientId ->
                  directChatRecipientId = recipientId
                  selectedTab = 2
                },
                snackbarHostState = snackbarHostState,
                onGoogleSignInClick = { showGoogleSignInDialog = true },
                onSearchUserClick = { showSearchUserDialog = true },
                onNavigateToLostFound = { drawerDestination = DrawerDestination.LOST_FOUND },
                onNavigateToStudy = { drawerDestination = DrawerDestination.STUDY_PARTNER },
                onNavigateToProjects = { drawerDestination = DrawerDestination.PROJECT_PARTNER },
                onNavigateToEvents = { drawerDestination = DrawerDestination.EVENTS },
                onNavigateToSuggested = { selectedTab = 1 }
              )

              1 -> SuggestedScreen(
                currentUser = currentUser,
                allProfiles = allProfiles,
                onConnectClick = { targetProfile ->
                  MgugRealtimeRepository.connectWithPeer(targetProfile.id)
                },
                onNavigateToChat = { targetId ->
                  directChatRecipientId = targetId
                  selectedTab = 2
                }
              )

              2 -> ChatScreen(
                currentUser = currentUser,
                initialRecipientId = directChatRecipientId,
                onSendMessage = { recId, content ->
                  MgugRealtimeRepository.sendDirectMessage(recId, content)
                }
              )

              3 -> ProfileScreen(
                currentUser = currentUser,
                allProfiles = allProfiles,
                onUserSwitch = { newProfileId ->
                  val found = allProfiles.find { it.id == newProfileId }
                  if (found != null) {
                    MgugRealtimeRepository.switchUser(found)
                  }
                },
                onGoogleSignInClick = { showGoogleSignInDialog = true },
                onNavigateToChat = { recipientId ->
                  directChatRecipientId = recipientId
                  selectedTab = 2
                }
              )
            }
          }
        }
      }

      if (showGoogleSignInDialog) {
        GoogleSignInDialog(
          currentUser = currentUser,
          onDismiss = { showGoogleSignInDialog = false }
        )
      }

      if (showSearchUserDialog) {
        SearchUserDialog(
          currentUser = currentUser,
          onDismiss = { showSearchUserDialog = false },
          onSelectStudentToChat = { recipientId ->
            directChatRecipientId = recipientId
            selectedTab = 2
          },
          onSwitchUser = { newProfile ->
            MgugRealtimeRepository.switchUser(newProfile)
          }
        )
      }

      if (showContactUsDialog) {
        ContactUsDialog(
          currentUser = currentUser,
          onDismiss = { showContactUsDialog = false },
          onSubmit = { subject, category, message, attachment ->
            MgugRealtimeRepository.submitContactRequest(subject, category, message, attachment)
          }
        )
      }

      if (showFeedbackDialog) {
        FeedbackDialog(
          currentUser = currentUser,
          onDismiss = { showFeedbackDialog = false },
          onSubmit = { type, message, hasScreenshot ->
            MgugRealtimeRepository.submitFeedback(type, message, hasScreenshot)
          }
        )
      }

      if (showSettingsDialog) {
        SettingsDialog(
          currentUser = currentUser,
          onDismiss = { showSettingsDialog = false },
          onEditProfileClick = {
            selectedTab = 3
          },
          onLogoutClick = {
            MgugRealtimeRepository.logOut()
          }
        )
      }
    }
  }
}
