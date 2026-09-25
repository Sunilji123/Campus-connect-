package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Event
import com.example.data.model.FeedItem
import com.example.data.model.FeedItemType
import com.example.data.model.LostFoundType
import com.example.data.model.Poll
import com.example.data.model.StudentProfile
import com.example.ui.util.FormatUtils

/**
 * Unified model representing dynamic content types that can be rendered in the feed.
 */
sealed interface FeedContent {
  data class Post(val item: FeedItem) : FeedContent
  data class PollItem(val poll: Poll) : FeedContent
  data class EventItem(val event: Event) : FeedContent
}

/**
 * Material 3 FeedCard component that dynamically renders posts, polls, and events
 * with modern rounded corners, subtle shadows, and distinct category styling.
 */
@Composable
fun FeedCard(
  content: FeedContent,
  currentUser: StudentProfile,
  onLikeClick: (String) -> Unit = {},
  onCommentClick: (FeedItem) -> Unit = {},
  onPartnerActionClick: (String) -> Unit = {},
  onMarkResolvedClick: (String) -> Unit = {},
  onDeleteClick: (String) -> Unit = {},
  onReportClick: (String) -> Unit = {},
  onVotePoll: (pollId: String, optionIndex: Int) -> Unit = { _, _ -> },
  onToggleEventRsvp: (eventId: String) -> Unit = {},
  modifier: Modifier = Modifier
) {
  when (content) {
    is FeedContent.Post -> {
      FeedCard(
        item = content.item,
        currentUser = currentUser,
        onLikeClick = { onLikeClick(content.item.id) },
        onCommentClick = { onCommentClick(content.item) },
        onPartnerActionClick = { onPartnerActionClick(content.item.authorId) },
        onMarkResolvedClick = { onMarkResolvedClick(content.item.id) },
        onDeleteClick = { onDeleteClick(content.item.id) },
        onReportClick = { onReportClick(content.item.id) },
        modifier = modifier
      )
    }
    is FeedContent.PollItem -> {
      FeedCard(
        poll = content.poll,
        currentUserId = currentUser.id,
        onVote = { optIdx -> onVotePoll(content.poll.id, optIdx) },
        modifier = modifier
      )
    }
    is FeedContent.EventItem -> {
      FeedCard(
        event = content.event,
        currentUserId = currentUser.id,
        onToggleRsvp = { onToggleEventRsvp(content.event.id) },
        modifier = modifier
      )
    }
  }
}

/**
 * FeedCard overload for standard feed posts (General, Study, Project, Lost & Found, Campus Notice).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedCard(
  item: FeedItem,
  currentUser: StudentProfile,
  onLikeClick: () -> Unit,
  onCommentClick: () -> Unit,
  onPartnerActionClick: () -> Unit,
  onMarkResolvedClick: () -> Unit,
  onDeleteClick: () -> Unit,
  onReportClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showMenu by remember { mutableStateOf(false) }
  val isLiked = item.isLikedBy(currentUser.id)
  val isAuthor = item.authorId == currentUser.id

  val heartColor by animateColorAsState(
    targetValue = if (isLiked) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant,
    label = "heart_color"
  )

  // Distinct background and border styling based on category
  val isNotice = item.isOfficialNotice || item.type == FeedItemType.CAMPUS_UPDATE
  val cardContainerColor = when {
    isNotice -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.22f)
    item.type == FeedItemType.STUDY_PARTNER -> MaterialTheme.colorScheme.surface
    item.type == FeedItemType.PROJECT_PARTNER -> MaterialTheme.colorScheme.surface
    else -> MaterialTheme.colorScheme.surface
  }

  val cardBorderStroke = when {
    isNotice -> androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
    item.type == FeedItemType.STUDY_PARTNER -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.2f))
    item.type == FeedItemType.PROJECT_PARTNER -> androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.2f))
    else -> null
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("feed_card_${item.id}"),
    shape = RoundedCornerShape(22.dp),
    border = cardBorderStroke,
    colors = CardDefaults.cardColors(containerColor = cardContainerColor),
    elevation = CardDefaults.cardElevation(
      defaultElevation = 2.dp,
      pressedElevation = 4.dp
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // 1. Author Header Row
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
              .background(Color(item.authorColorHex), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = item.authorName.take(1),
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 17.sp
            )
          }

          Spacer(modifier = Modifier.width(10.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.authorName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Spacer(modifier = Modifier.width(4.dp))
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Verified Student",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
              )
            }
            Text(
              text = "${item.authorFaculty} · ${item.authorYear}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              overflow = TextOverflow.Ellipsis
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = FormatUtils.formatRelativeTime(item.timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
          )

          Box {
            IconButton(
              onClick = { showMenu = true },
              modifier = Modifier
                .size(36.dp)
                .testTag("feed_more_menu_${item.id}")
            ) {
              Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More Options",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
              )
            }

            DropdownMenu(
              expanded = showMenu,
              onDismissRequest = { showMenu = false }
            ) {
              if (isAuthor) {
                if (item.type == FeedItemType.LOST_FOUND && !item.isResolved) {
                  DropdownMenuItem(
                    text = { Text("Mark as Found/Returned") },
                    leadingIcon = {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF2E7D32)
                      )
                    },
                    onClick = {
                      showMenu = false
                      onMarkResolvedClick()
                    }
                  )
                }
                DropdownMenuItem(
                  text = { Text("Delete Post") },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Delete,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.error
                    )
                  },
                  onClick = {
                    showMenu = false
                    onDeleteClick()
                  }
                )
              } else {
                DropdownMenuItem(
                  text = { Text("Report Content") },
                  leadingIcon = {
                    Icon(
                      imageVector = Icons.Default.Report,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.error
                    )
                  },
                  onClick = {
                    showMenu = false
                    onReportClick()
                  }
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // 2. Distinct Category Badge
      when (item.type) {
        FeedItemType.STUDY_PARTNER -> {
          TypeChip(
            label = "Study Partner Request",
            icon = Icons.Default.MenuBook,
            containerColor = Color(0xFFE8F5E9),
            contentColor = Color(0xFF2E7D32)
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
        FeedItemType.PROJECT_PARTNER -> {
          TypeChip(
            label = "Project / Hackathon Squad",
            icon = Icons.Default.Code,
            containerColor = Color(0xFFE3F2FD),
            contentColor = Color(0xFF1565C0)
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
        FeedItemType.LOST_FOUND -> {
          val isLost = item.lostFoundType == LostFoundType.LOST
          TypeChip(
            label = if (isLost) "LOST ITEM on Campus" else "FOUND ITEM on Campus",
            icon = Icons.Default.Search,
            containerColor = if (isLost) Color(0xFFFFEBEE) else Color(0xFFE0F2F1),
            contentColor = if (isLost) Color(0xFFC62828) else Color(0xFF00695C)
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
        FeedItemType.CAMPUS_UPDATE -> {
          TypeChip(
            label = "Official Campus Announcement",
            icon = Icons.Default.Campaign,
            containerColor = Color(0xFFFFF3E0),
            contentColor = Color(0xFFE65100)
          )
          Spacer(modifier = Modifier.height(8.dp))
        }
        FeedItemType.GENERAL -> { /* standard post */ }
      }

      // 3. Post Content Text
      Text(
        text = item.content,
        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 21.sp),
        color = MaterialTheme.colorScheme.onSurface
      )

      // 4. Distinct Category Cards
      when (item.type) {
        FeedItemType.STUDY_PARTNER -> {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF2E7D32).copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              item.subject?.let {
                Text(
                  text = "📚 Subject: $it",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              item.targetExam?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "🎯 Target: $it",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              item.studyHours?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "⏰ Hours: $it",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              item.studyLocation?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "📍 Spot: $it",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = onPartnerActionClick,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(38.dp)
                  .testTag("study_connect_button_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
              ) {
                Icon(
                  imageVector = Icons.Default.Handshake,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Join Study Squad · Message",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }

        FeedItemType.PROJECT_PARTNER -> {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1565C0).copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.2f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              item.projectTitle?.let {
                Text(
                  text = "🚀 $it",
                  style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                  color = MaterialTheme.colorScheme.onSurface
                )
              }
              item.techStack?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Tech: $it",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
              item.rolesNeeded?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Roles: $it",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                  color = Color(0xFF1565C0)
                )
              }
              item.deadline?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "Deadline: $it",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.outline
                )
              }

              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = onPartnerActionClick,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(38.dp)
                  .testTag("project_apply_button_${item.id}"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0))
              ) {
                Icon(
                  imageVector = Icons.Default.Handshake,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "Join Project Team",
                  style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
            }
          }
        }

        FeedItemType.LOST_FOUND -> {
          Spacer(modifier = Modifier.height(10.dp))
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (item.isResolved) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                item.campusLocation?.let {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.LocationOn,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.secondary,
                      modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = it,
                      style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                      color = MaterialTheme.colorScheme.onSurface
                    )
                  }
                }

                if (item.isResolved) {
                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF2E7D32)
                  ) {
                    Text(
                      text = "RESOLVED",
                      color = Color.White,
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }
              }

              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "Safe & Private: Reply via in-app comments or chat",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF2E7D32)
              )

              if (!item.isResolved && isAuthor) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                  onClick = onMarkResolvedClick,
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .testTag("mark_resolved_button_${item.id}"),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  Text("Mark as Claimed / Returned", fontSize = 12.sp)
                }
              }
            }
          }
        }

        else -> { /* standard view */ }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // 5. Actions Footer (Like, Comment, Share)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onLikeClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("like_button_${item.id}")
        ) {
          Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            contentDescription = "Like Post",
            tint = heartColor,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (item.likeCount > 0) item.likeCount.toString() else "Like",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (isLiked) Color(0xFFE53935) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onCommentClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("comment_button_${item.id}")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Comment,
            contentDescription = "Comments",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (item.commentsCount > 0) item.commentsCount.toString() else "Comment",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable {
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "${item.authorName} on Campus Connect:\n\"${item.content}\"")
                type = "text/plain"
              }
              val shareIntent = Intent.createChooser(sendIntent, "Share Post")
              context.startActivity(shareIntent)
            }
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag("share_button_${item.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Share",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(19.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Share",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

/**
 * FeedCard overload for campus polls with interactive voting, percentage progress bars,
 * and active status indicators.
 */
@Composable
fun FeedCard(
  poll: Poll,
  currentUserId: String,
  onVote: (optionIndex: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val hasVoted = poll.hasVoted(currentUserId)
  val userVotedIdx = poll.getUserVotedOptionIndex(currentUserId)
  val totalVotes = poll.totalVotes

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("poll_card_${poll.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp, pressedElevation = 4.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Header: Category badge & Active status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.HowToVote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Text(
              text = poll.category,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(if (poll.isActive) Color(0xFF00E676) else Color.Gray, CircleShape)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (poll.isActive) "Live Poll" else "Closed",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (poll.isActive) Color(0xFF2E7D32) else Color.Gray
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Question
      Text(
        text = poll.question,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, lineHeight = 20.sp),
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = 3,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Poll Options
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        poll.options.forEachIndexed { index, option ->
          val isThisUserVote = index == userVotedIdx
          val percentage = if (totalVotes > 0) (option.voteCount.toFloat() / totalVotes) else 0f
          val animatedProgress by animateFloatAsState(
            targetValue = if (hasVoted) percentage else 0f,
            label = "poll_progress_$index"
          )

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isThisUserVote) {
              MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            } else {
              MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            },
            border = if (isThisUserVote) {
              androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
            } else null,
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .clickable(enabled = poll.isActive) { onVote(index) }
              .testTag("poll_option_${poll.id}_$index")
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  modifier = Modifier.weight(1f)
                ) {
                  if (isThisUserVote) {
                    Icon(
                      imageVector = Icons.Default.Check,
                      contentDescription = "Your Vote",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                  }
                  Text(
                    text = option.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                      fontWeight = if (isThisUserVote) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                  )
                }

                if (hasVoted) {
                  Text(
                    text = "${(percentage * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (isThisUserVote) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }

              if (hasVoted) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                  progress = { animatedProgress },
                  modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                  color = if (isThisUserVote) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                  trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Footer: Total votes & Poll author
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "$totalVotes student votes · by ${poll.creatorName.split(" ").first()}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )

        if (hasVoted) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFE8F5E9)
          ) {
            Text(
              text = "✓ Voted",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF2E7D32),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }
      }
    }
  }
}

/**
 * FeedCard overload for campus events with gradient banner, date & time, venue,
 * attendee counts, and interactive RSVP button.
 */
@Composable
fun FeedCard(
  event: Event,
  currentUserId: String,
  onToggleRsvp: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isRsvpd = event.isRegistered(currentUserId)

  val bannerGradients = listOf(
    Brush.horizontalGradient(listOf(Color(0xFF651FFF), Color(0xFF00B0FF))),
    Brush.horizontalGradient(listOf(Color(0xFF00695C), Color(0xFF00BFA5))),
    Brush.horizontalGradient(listOf(Color(0xFFD84315), Color(0xFFFF9100))),
    Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFFAB47BC)))
  )
  val gradient = bannerGradients[event.bannerGradientIndex % bannerGradients.size]

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("event_card_${event.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp, pressedElevation = 4.dp)
  ) {
    Column {
      // Event Banner
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(76.dp)
          .background(gradient)
          .padding(12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.3f)
          ) {
            Text(
              text = event.category,
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.95f)
          ) {
            Text(
              text = "${event.attendeeCount} Attending",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF1E1C1B),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }
      }

      // Event Details
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = event.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
          text = event.description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Date, Time & Venue
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${event.date} · ${event.time}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.weight(1f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = event.venue,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // RSVP Action Button
        Button(
          onClick = onToggleRsvp,
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isRsvpd) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("event_rsvp_button_${event.id}")
        ) {
          Icon(
            imageVector = if (isRsvpd) Icons.Default.Check else Icons.Default.Event,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isRsvpd) "Attending (RSVP Confirmed)" else "RSVP to Event",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

@Composable
fun TypeChip(
  label: String,
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  containerColor: Color,
  contentColor: Color
) {
  Surface(
    shape = RoundedCornerShape(8.dp),
    color = containerColor
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = contentColor,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(5.dp))
      Text(
        text = label,
        color = contentColor,
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
      )
    }
  }
}
