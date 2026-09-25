package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Event
import com.example.data.model.StudentProfile
import com.example.ui.components.campusAmbientMesh

/**
 * Dedicated Campus Events Screen:
 * - Cover banner gradient/image
 * - Categories: Academic, Tech, Cultural, Sports, Career, Other
 * - Date, Time, Venue, Organizer, Description
 * - Realtime RSVP action with live count
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsView(
  events: List<Event>,
  currentUser: StudentProfile,
  onBack: () -> Unit,
  onToggleEventRsvp: (String) -> Unit,
  onCreateEvent: (title: String, desc: String, cat: String, date: String, time: String, venue: String, org: String) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedCategory by remember { mutableStateOf("All") }
  var showCreateDialog by remember { mutableStateOf(false) }

  val categories = listOf("All", "🎓 Academic", "💻 Tech", "🎨 Cultural", "🏆 Sports", "💼 Career", "🔥 Other")

  val filteredEvents = remember(events, selectedCategory) {
    if (selectedCategory == "All") events
    else {
      val rawCategory = selectedCategory.substringAfter(" ").trim()
      events.filter { it.category.contains(rawCategory, ignoreCase = true) }
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
            modifier = Modifier.testTag("events_back_button")
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
        },
        title = {
          Column {
            Text(
              text = "Campus Events & Fests",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "${events.count { it.isUpcoming }} upcoming gatherings",
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
        containerColor = Color(0xFF7B1FA2),
        contentColor = Color.White,
        modifier = Modifier.testTag("fab_create_event")
      ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Host Event", fontWeight = FontWeight.Bold)
      }
    }
  ) { padding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .campusAmbientMesh()
        .padding(padding)
    ) {
      // Category chips row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.take(4).forEach { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        categories.drop(4).forEach { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { selectedCategory = cat },
            label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
          )
        }
      }

      if (filteredEvents.isEmpty()) {
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
                .background(Color(0xFF7B1FA2).copy(alpha = 0.12f), CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = Color(0xFF7B1FA2),
                modifier = Modifier.size(36.dp)
              )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
              text = "No Events in this Category",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Check back soon or organize an event for fellow campus students.",
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
                      Color(0xFFE65100), // Deep Orange
                      Color(0xFFFF9800), // Orange
                      Color(0xFFE91E63), // Pink
                      Color(0xFF7B1FA2)  // Purple
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
                      text = "🎉 CAMPUS HAPPENINGS & FESTS",
                      style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                      color = Color.White,
                      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                  }
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "Live Campus Life to the Fullest.",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Black,
                      color = Color.White
                    )
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Text(
                    text = "Tech hackathons, cultural concerts, sports leagues & guest lectures across campus.",
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
                  Text("🎪", fontSize = 28.sp)
                }
              }
            }
          }

          items(filteredEvents, key = { it.id }) { event ->
            CampusEventDetailedCard(
              event = event,
              currentUser = currentUser,
              onToggleRsvp = { onToggleEventRsvp(event.id) }
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
    CreateEventDialog(
      currentUser = currentUser,
      onDismiss = { showCreateDialog = false },
      onSubmit = { t, d, c, dt, tm, v, o ->
        onCreateEvent(t, d, c, dt, tm, v, o)
        showCreateDialog = false
      }
    )
  }
}

@Composable
private fun CampusEventDetailedCard(
  event: Event,
  currentUser: StudentProfile,
  onToggleRsvp: () -> Unit
) {
  val isRsvpd = event.isRegistered(currentUser.id)

  val bannerGradients = listOf(
    Brush.horizontalGradient(listOf(Color(0xFFD84315), Color(0xFFF57F17))),
    Brush.horizontalGradient(listOf(Color(0xFF00695C), Color(0xFF00897B))),
    Brush.horizontalGradient(listOf(Color(0xFF283593), Color(0xFF5C6BC0))),
    Brush.horizontalGradient(listOf(Color(0xFF880E4F), Color(0xFFAD1457))),
    Brush.horizontalGradient(listOf(Color(0xFF4A148C), Color(0xFF7B1FA2)))
  )
  val gradient = bannerGradients[event.bannerGradientIndex % bannerGradients.size]

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("event_card_${event.id}"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.5.dp)
  ) {
    Column {
      // Cover Header
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(84.dp)
          .background(gradient)
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxSize(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.25f)
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
            color = Color.White.copy(alpha = 0.9f)
          ) {
            Text(
              text = "${event.attendeeCount} Registered",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
              color = Color(0xFF1E1C1B),
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }
      }

      // Details
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = event.title,
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "${event.date} · ${event.time}",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = event.venue,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = event.description,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "Organized by ${event.organizer}",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(14.dp))

        // RSVP Button
        if (isRsvpd) {
          OutlinedButton(
            onClick = onToggleRsvp,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("rsvp_btn_${event.id}")
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Registered (RSVP'd)", fontWeight = FontWeight.Bold)
          }
        } else {
          Button(
            onClick = onToggleRsvp,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(42.dp)
              .testTag("rsvp_btn_${event.id}")
          ) {
            Text("Register / RSVP Free", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}

@Composable
private fun CreateEventDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onSubmit: (title: String, desc: String, cat: String, date: String, time: String, venue: String, org: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Tech & Hackathons") }
  var date by remember { mutableStateOf("Tomorrow") }
  var time by remember { mutableStateOf("5:00 PM") }
  var venue by remember { mutableStateOf("Campus Main Auditorium") }
  var organizer by remember { mutableStateOf(currentUser.name) }

  val catOptions = listOf("Tech & Hackathons", "Academic & Seminars", "Cultural Fest", "Sports & Tournaments", "Career & Placement")

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(24.dp),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 6.dp,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("dialog_create_event")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Host Campus Event",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Event Name") },
          placeholder = { Text("e.g. Annual Campus Hackathon 2026") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth().testTag("input_event_title")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
          text = "CATEGORY",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          catOptions.take(2).forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          catOptions.drop(2).take(2).forEach { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, style = MaterialTheme.typography.labelSmall) }
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("Date") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )
          OutlinedTextField(
            value = time,
            onValueChange = { time = it },
            label = { Text("Time") },
            modifier = Modifier.weight(1f),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = venue,
          onValueChange = { venue = it },
          label = { Text("Venue / Location") },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description & Schedule") },
          minLines = 3,
          maxLines = 4,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
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
              if (title.isNotBlank() && description.isNotBlank()) {
                onSubmit(title, description, category, date, time, venue, organizer)
              }
            },
            enabled = title.isNotBlank() && description.isNotBlank(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7B1FA2)),
            modifier = Modifier.testTag("btn_submit_event")
          ) {
            Text("Create Event", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
