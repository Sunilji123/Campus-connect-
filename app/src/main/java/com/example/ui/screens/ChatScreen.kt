package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ChatGroup
import com.example.data.model.ChatMessage
import com.example.data.model.StudentProfile
import com.example.data.repository.MgugRealtimeRepository
import com.example.ui.components.CreateGroupChatDialog
import com.example.ui.components.SearchUserDialog
import com.example.ui.util.FormatUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
  currentUser: StudentProfile,
  initialRecipientId: String? = null,
  onSendMessage: (receiverId: String, content: String) -> Unit,
  modifier: Modifier = Modifier
) {
  val allProfiles by MgugRealtimeRepository.allProfiles.collectAsState()
  val chatGroups by MgugRealtimeRepository.chatGroups.collectAsState()
  val chatMessages by MgugRealtimeRepository.chatMessages.collectAsState()
  val typingStatus by MgugRealtimeRepository.typingStatus.collectAsState()

  val peers = remember(allProfiles, currentUser) {
    allProfiles.filter { it.id != currentUser.id }
  }

  // Active chat tab: 0 = Direct 1-on-1 Chats, 1 = Squad/Group Chats
  var chatTab by remember { mutableIntStateOf(if (initialRecipientId != null) 0 else 0) }
  var showCreateGroupDialog by remember { mutableStateOf(false) }

  // Direct Chat selection
  var selectedPeer by remember(peers, initialRecipientId) {
    mutableStateOf(
      if (initialRecipientId != null) {
        peers.firstOrNull { it.id == initialRecipientId } ?: peers.firstOrNull() ?: currentUser
      } else {
        peers.firstOrNull() ?: currentUser
      }
    )
  }

  var peerSearchQuery by remember { mutableStateOf("") }
  var showFullUserSearchDialog by remember { mutableStateOf(false) }

  val filteredPeers = remember(peers, peerSearchQuery) {
    val q = peerSearchQuery.trim().lowercase()
    if (q.isBlank()) {
      peers
    } else {
      peers.filter {
        it.name.lowercase().contains(q) ||
          it.course.lowercase().contains(q) ||
          it.faculty.lowercase().contains(q) ||
          it.rollNo.lowercase().contains(q) ||
          it.skills.any { s -> s.lowercase().contains(q) } ||
          it.interests.any { i -> i.lowercase().contains(q) }
      }
    }
  }

  // Group Chat selection
  var selectedGroup by remember(chatGroups) {
    mutableStateOf(chatGroups.firstOrNull())
  }

  var messageInput by remember { mutableStateOf("") }
  val listState = rememberLazyListState()

  // Auto typing state handler
  LaunchedEffect(messageInput) {
    val activeKey = if (chatTab == 0) selectedPeer.id else selectedGroup?.id
    if (activeKey != null) {
      MgugRealtimeRepository.setTyping(activeKey, messageInput.isNotBlank())
    }
  }

  // Active messages based on tab
  val activeMessages = remember(chatTab, selectedPeer, selectedGroup, chatMessages) {
    if (chatTab == 0) {
      MgugRealtimeRepository.getDirectConversation(currentUser.id, selectedPeer.id)
    } else {
      selectedGroup?.let { MgugRealtimeRepository.getGroupConversation(it.id) } ?: emptyList()
    }
  }

  // Mark as read
  LaunchedEffect(activeMessages.size, chatTab, selectedPeer, selectedGroup) {
    val activeConvId = if (chatTab == 0) {
      MgugRealtimeRepository.getDirectConversationId(currentUser.id, selectedPeer.id)
    } else {
      selectedGroup?.id
    }
    if (activeConvId != null) {
      MgugRealtimeRepository.markConversationAsRead(activeConvId)
    }
    if (activeMessages.isNotEmpty()) {
      listState.animateScrollToItem(activeMessages.size - 1)
    }
  }

  // Who is typing right now?
  val currentTypers = remember(typingStatus, chatTab, selectedPeer, selectedGroup, currentUser) {
    val key = if (chatTab == 0) currentUser.id else selectedGroup?.id
    val userIds = (typingStatus[key] ?: emptySet()) - currentUser.id
    userIds.mapNotNull { MgugRealtimeRepository.getProfileById(it)?.name?.split(" ")?.first() }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("chat_screen")
  ) {
    // 1. Gen Z Header & Chat Type Tabs
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 3.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column {
        TabRow(
          selectedTabIndex = chatTab,
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(horizontal = 8.dp)
        ) {
          Tab(
            selected = chatTab == 0,
            onClick = { chatTab = 0 },
            text = {
              Text(
                text = "⚡ Direct Chats",
                fontWeight = if (chatTab == 0) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
              )
            },
            modifier = Modifier.testTag("tab_direct_chats")
          )
          Tab(
            selected = chatTab == 1,
            onClick = { chatTab = 1 },
            text = {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "🔥 Squads & Groups",
                  fontWeight = if (chatTab == 1) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Surface(
                  shape = CircleShape,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Text(
                      text = "${chatGroups.size}",
                      color = Color.White,
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                }
              }
            },
            modifier = Modifier.testTag("tab_group_chats")
          )
        }

        // Sub-selector Bar
        if (chatTab == 0) {
          // Search Existing Peers Bar
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            OutlinedTextField(
              value = peerSearchQuery,
              onValueChange = { peerSearchQuery = it },
              placeholder = { Text("Search students to chat...", fontSize = 12.sp) },
              leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", modifier = Modifier.size(16.dp))
              },
              trailingIcon = {
                if (peerSearchQuery.isNotBlank()) {
                  IconButton(onClick = { peerSearchQuery = "" }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                  }
                }
              },
              singleLine = true,
              shape = RoundedCornerShape(14.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
              ),
              modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .testTag("chat_search_peer_input")
            )

            Spacer(modifier = Modifier.width(6.dp))

            Surface(
              shape = RoundedCornerShape(14.dp),
              color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
              modifier = Modifier
                .clip(RoundedCornerShape(14.dp))
                .clickable { showFullUserSearchDialog = true }
                .testTag("chat_open_directory_button")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = "All Students Directory",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Directory",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.primary
                )
              }
            }
          }

          // Horizontal Peer avatars selector
          if (filteredPeers.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "No students matching \"$peerSearchQuery\"",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
          } else {
            LazyRow(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              items(filteredPeers, key = { it.id }) { peer ->
                val isSelected = peer.id == selectedPeer.id
                Surface(
                  shape = RoundedCornerShape(18.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                  modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { selectedPeer = peer }
                    .testTag("chat_peer_${peer.id}")
                ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .background(Color(peer.avatarColorHex), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = peer.name.take(1),
                      color = Color.White,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = peer.name.split(" ").first(),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                      )
                      if (peer.isVerified) {
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                          imageVector = Icons.Default.Verified,
                          contentDescription = "Verified",
                          tint = MaterialTheme.colorScheme.primary,
                          modifier = Modifier.size(11.dp)
                        )
                      }
                    }
                    Text(
                      text = peer.statusEmoji + " " + peer.statusText,
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                      color = MaterialTheme.colorScheme.outline,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis
                    )
                  }
                }
              }
            }
          }
        }
      } else {
        // Group Squads Carousel with "+ New Squad"
          LazyRow(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            item {
              ElevatedButton(
                onClick = { showCreateGroupDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                  containerColor = MaterialTheme.colorScheme.primary,
                  contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("create_new_group_button")
              ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Squad", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }

            items(chatGroups, key = { it.id }) { group ->
              val isSelected = group.id == selectedGroup?.id
              Surface(
                shape = RoundedCornerShape(18.dp),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier
                  .clip(RoundedCornerShape(18.dp))
                  .clickable { selectedGroup = group }
                  .testTag("chat_group_${group.id}")
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                  Text(text = group.iconEmoji, fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = group.name,
                      style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                      color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                      text = "${group.memberIds.size} members · ${group.category}",
                      style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
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

    // 2. Chat Target Sub-header banner
    Surface(
      color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        if (chatTab == 0) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(Color(0xFF00E676), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${selectedPeer.name} (${selectedPeer.course} · ${selectedPeer.year})",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        } else {
          selectedGroup?.let { grp ->
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "${grp.iconEmoji} #${grp.name} · ${grp.description}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        // Live badge
        Surface(
          shape = RoundedCornerShape(6.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        ) {
          Text(
            text = "E2E Student Network",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }

    // 3. Message Stream
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 12.dp)
        .testTag("chat_messages_list"),
      verticalArrangement = Arrangement.spacedBy(10.dp),
      contentPadding = PaddingValues(vertical = 12.dp)
    ) {
      if (activeMessages.isEmpty()) {
        item {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(260.dp),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(text = if (chatTab == 0) "💬" else "🚀", fontSize = 28.sp)
                }
              }
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = if (chatTab == 0) "Start a conversation with ${selectedPeer.name.split(" ").first()}!" else "Welcome to #${selectedGroup?.name}!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Coordinate study sessions, hackathons, or campus notes in real time.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
              )
            }
          }
        }
      } else {
        items(activeMessages, key = { it.id }) { msg ->
          val isFromMe = msg.senderId == currentUser.id
          val isSystem = msg.senderId == "system"

          if (isSystem) {
            // System announcement bubble
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
              contentAlignment = Alignment.Center
            ) {
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
              ) {
                Text(
                  text = msg.content,
                  style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                  color = MaterialTheme.colorScheme.onSurfaceVariant,
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
              }
            }
          } else {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = if (isFromMe) Arrangement.End else Arrangement.Start
            ) {
              if (!isFromMe) {
                val senderProfile = MgugRealtimeRepository.getProfileById(msg.senderId)
                Box(
                  modifier = Modifier
                    .size(28.dp)
                    .background(Color(senderProfile?.avatarColorHex ?: 0xFF7C4DFF), CircleShape),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = msg.senderName.take(1),
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                  )
                }
                Spacer(modifier = Modifier.width(6.dp))
              }

              Column(horizontalAlignment = if (isFromMe) Alignment.End else Alignment.Start) {
                if (chatTab == 1 && !isFromMe) {
                  Text(
                    text = msg.senderName,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                  )
                }

                Surface(
                  shape = RoundedCornerShape(
                    topStart = 18.dp,
                    topEnd = 18.dp,
                    bottomStart = if (isFromMe) 18.dp else 4.dp,
                    bottomEnd = if (isFromMe) 4.dp else 18.dp
                  ),
                  color = if (isFromMe) Color(0xFF651FFF) else MaterialTheme.colorScheme.surfaceVariant,
                  shadowElevation = 1.dp,
                  modifier = Modifier.fillMaxWidth(0.80f)
                ) {
                  Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(
                      text = msg.content,
                      style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                      color = if (isFromMe) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.End,
                      modifier = Modifier.align(Alignment.End)
                    ) {
                      Text(
                        text = FormatUtils.formatRelativeTime(msg.timestamp),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = if (isFromMe) Color.White.copy(alpha = 0.75f) else MaterialTheme.colorScheme.outline
                      )
                      if (isFromMe) {
                        Spacer(modifier = Modifier.width(4.dp))
                        // Read receipt checkmarks
                        val isReadByOthers = if (chatTab == 0) {
                          msg.isRead || msg.readBy.contains(selectedPeer.id)
                        } else {
                          msg.readBy.any { it != currentUser.id }
                        }

                        if (isReadByOthers) {
                          Icon(
                            imageVector = Icons.Default.DoneAll,
                            contentDescription = "Read",
                            tint = Color(0xFF00E5FF), // Cyan double check mark for read receipts
                            modifier = Modifier.size(13.dp)
                          )
                        } else {
                          Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Delivered",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(12.dp)
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

    // 4. Typing Indicator Bubble
    AnimatedVisibility(
      visible = currentTypers.isNotEmpty(),
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .padding(horizontal = 16.dp, vertical = 4.dp)
          .align(Alignment.Start)
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "✍️ ${currentTypers.joinToString()} is typing...",
            style = MaterialTheme.typography.labelSmall.copy(
              fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
              color = MaterialTheme.colorScheme.primary
            )
          )
        }
      }
    }

    // 5. Input Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 4.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = messageInput,
          onValueChange = { messageInput = it },
          placeholder = {
            Text(
              text = if (chatTab == 0) "DM ${selectedPeer.name.split(" ").first()}..." else "Message #${selectedGroup?.name ?: "squad"}...",
              fontSize = 13.sp
            )
          },
          modifier = Modifier
            .weight(1f)
            .testTag("chat_input_field"),
          shape = RoundedCornerShape(24.dp),
          singleLine = true,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
          )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
          shape = CircleShape,
          color = if (messageInput.isNotBlank()) Color(0xFF651FFF) else MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(enabled = messageInput.isNotBlank()) {
              if (messageInput.isNotBlank()) {
                if (chatTab == 0) {
                  onSendMessage(selectedPeer.id, messageInput.trim())
                } else {
                  selectedGroup?.let {
                    MgugRealtimeRepository.sendGroupMessage(it.id, messageInput.trim())
                  }
                }
                messageInput = ""
              }
            }
            .testTag("chat_send_button")
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Send,
              contentDescription = "Send",
              tint = if (messageInput.isNotBlank()) Color.White else MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    }
  }

  // Create Group Chat Dialog
  if (showCreateGroupDialog) {
    CreateGroupChatDialog(
      currentUser = currentUser,
      allProfiles = allProfiles,
      onDismiss = { showCreateGroupDialog = false },
      onCreateGroup = { name, desc, cat, emoji, members ->
        val grp = MgugRealtimeRepository.createGroupChat(name, desc, cat, emoji, members)
        selectedGroup = grp
        chatTab = 1
      }
    )
  }

  // Student Directory & Search Dialog
  if (showFullUserSearchDialog) {
    SearchUserDialog(
      currentUser = currentUser,
      onDismiss = { showFullUserSearchDialog = false },
      onSelectStudentToChat = { recId ->
        val target = allProfiles.firstOrNull { it.id == recId }
        if (target != null) {
          selectedPeer = target
          chatTab = 0
        }
      },
      onSwitchUser = { newProfile ->
        MgugRealtimeRepository.switchUser(newProfile)
      }
    )
  }
}
