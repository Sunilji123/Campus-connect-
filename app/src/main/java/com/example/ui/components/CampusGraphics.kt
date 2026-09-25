package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Ambient background modifier drawing soft colorful glowing orbs
 * to give every screen a modern, lively, Gen-Z aesthetic.
 */
fun Modifier.campusAmbientMesh(): Modifier = this.drawBehind {
  val width = size.width
  val height = size.height

  // 1. Top Right Electric Violet / Pink ambient glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x227C4DFF),
        Color(0x11FF4081),
        Color.Transparent
      ),
      center = Offset(width * 0.9f, height * 0.05f),
      radius = width * 0.65f
    ),
    center = Offset(width * 0.9f, height * 0.05f),
    radius = width * 0.65f
  )

  // 2. Top Left Cyber Cyan / Turquoise ambient glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x1E00B0FF),
        Color(0x0C00E5FF),
        Color.Transparent
      ),
      center = Offset(width * 0.1f, height * 0.18f),
      radius = width * 0.55f
    ),
    center = Offset(width * 0.1f, height * 0.18f),
    radius = width * 0.55f
  )

  // 3. Middle Right Neon Coral / Sunrise ambient glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x18FF5722),
        Color(0x08FF9100),
        Color.Transparent
      ),
      center = Offset(width * 0.85f, height * 0.55f),
      radius = width * 0.5f
    ),
    center = Offset(width * 0.85f, height * 0.55f),
    radius = width * 0.5f
  )

  // 4. Bottom Left Emerald / Lime glow
  drawCircle(
    brush = Brush.radialGradient(
      colors = listOf(
        Color(0x1500BFA5),
        Color.Transparent
      ),
      center = Offset(width * 0.15f, height * 0.88f),
      radius = width * 0.5f
    ),
    center = Offset(width * 0.15f, height * 0.88f),
    radius = width * 0.5f
  )
}

/**
 * Eye-catching campus graphic hero art created with dynamic Compose Canvas:
 * Geometric college skyline, glowing connections, energetic waves & floating stars.
 */
@Composable
fun CampusHeroArt(
  modifier: Modifier = Modifier
) {
  val transition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by transition.animateFloat(
    initialValue = 0.92f,
    targetValue = 1.08f,
    animationSpec = infiniteRepeatable(
      animation = tween(2400, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(130.dp)
      .clip(RoundedCornerShape(22.dp))
      .background(
        Brush.linearGradient(
          colors = listOf(
            Color(0xFF4A148C), // Deep Indigo
            Color(0xFF651FFF), // Electric Violet
            Color(0xFF304FFE), // Royal Blue
            Color(0xFF00B0FF)  // Cyber Cyan
          )
        )
      )
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val w = size.width
      val h = size.height

      // Stylized background skyline
      val skylinePath = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * 0.65f)
        lineTo(w * 0.08f, h * 0.65f)
        lineTo(w * 0.08f, h * 0.45f)
        lineTo(w * 0.16f, h * 0.45f)
        lineTo(w * 0.16f, h * 0.7f)
        lineTo(w * 0.25f, h * 0.7f)
        lineTo(w * 0.25f, h * 0.35f)
        lineTo(w * 0.35f, h * 0.35f)
        lineTo(w * 0.35f, h * 0.55f)
        lineTo(w * 0.44f, h * 0.55f)
        lineTo(w * 0.44f, h * 0.3f)
        lineTo(w * 0.52f, h * 0.3f)
        lineTo(w * 0.52f, h * 0.65f)
        lineTo(w * 0.62f, h * 0.65f)
        lineTo(w * 0.62f, h * 0.4f)
        lineTo(w * 0.72f, h * 0.4f)
        lineTo(w * 0.72f, h * 0.75f)
        lineTo(w * 0.82f, h * 0.75f)
        lineTo(w * 0.82f, h * 0.5f)
        lineTo(w * 0.92f, h * 0.5f)
        lineTo(w * 0.92f, h * 0.7f)
        lineTo(w, h * 0.7f)
        lineTo(w, h)
        close()
      }
      drawPath(
        path = skylinePath,
        color = Color.White.copy(alpha = 0.12f)
      )

      // Dynamic bottom wave curves
      val wave1 = Path().apply {
        moveTo(0f, h * 0.8f)
        cubicTo(w * 0.25f, h * 0.65f, w * 0.55f, h * 0.95f, w, h * 0.75f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = wave1,
        color = Color(0xFFFF4081).copy(alpha = 0.25f)
      )

      val wave2 = Path().apply {
        moveTo(0f, h * 0.88f)
        cubicTo(w * 0.35f, h * 0.98f, w * 0.7f, h * 0.75f, w, h * 0.85f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
      }
      drawPath(
        path = wave2,
        color = Color(0xFF00E5FF).copy(alpha = 0.35f)
      )

      // Glowing campus network connection nodes
      val node1 = Offset(w * 0.22f, h * 0.42f)
      val node2 = Offset(w * 0.48f, h * 0.25f)
      val node3 = Offset(w * 0.78f, h * 0.38f)

      drawLine(
        color = Color(0xFF00E5FF).copy(alpha = 0.5f),
        start = node1,
        end = node2,
        strokeWidth = 2.5f
      )
      drawLine(
        color = Color(0xFFFF4081).copy(alpha = 0.5f),
        start = node2,
        end = node3,
        strokeWidth = 2.5f
      )

      // Glowing pulsing circles at nodes
      drawCircle(
        color = Color(0xFF00E5FF).copy(alpha = 0.3f),
        radius = 16f * pulseScale,
        center = node1
      )
      drawCircle(
        color = Color(0xFF00E5FF),
        radius = 6f,
        center = node1
      )

      drawCircle(
        color = Color(0xFFFFD600).copy(alpha = 0.3f),
        radius = 20f * pulseScale,
        center = node2
      )
      drawCircle(
        color = Color(0xFFFFD600),
        radius = 8f,
        center = node2
      )

      drawCircle(
        color = Color(0xFFFF4081).copy(alpha = 0.3f),
        radius = 16f * pulseScale,
        center = node3
      )
      drawCircle(
        color = Color(0xFFFF4081),
        radius = 6f,
        center = node3
      )

      // Floating ambient star particles
      drawCircle(Color.White.copy(alpha = 0.8f), radius = 3.5f, center = Offset(w * 0.15f, h * 0.25f))
      drawCircle(Color.White.copy(alpha = 0.6f), radius = 2.5f, center = Offset(w * 0.38f, h * 0.15f))
      drawCircle(Color.White.copy(alpha = 0.75f), radius = 3f, center = Offset(w * 0.65f, h * 0.2f))
      drawCircle(Color(0xFFFFD600).copy(alpha = 0.85f), radius = 3f, center = Offset(w * 0.88f, h * 0.22f))
    }

    // Overlay content for branding
    Row(
      modifier = Modifier
        .fillMaxSize()
        .padding(18.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = Color.Black.copy(alpha = 0.3f)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("✨", fontSize = 11.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "GEN-Z STUDENT COMMUNITY",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
              color = Color(0xFF80D8FF),
              letterSpacing = 1.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Find Squads. Share Items.\nMake Campus Memories.",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Black,
            lineHeight = 20.sp
          ),
          color = Color.White
        )
      }

      // 3D Avatar circle badge
      Box(
        modifier = Modifier
          .size(56.dp)
          .background(
            Brush.sweepGradient(
              listOf(
                Color(0xFFFF5722),
                Color(0xFFFF4081),
                Color(0xFF00E5FF),
                Color(0xFF7C4DFF),
                Color(0xFFFF5722)
              )
            ),
            CircleShape
          )
          .padding(3.dp),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B1B22), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text("🎓", fontSize = 26.sp)
        }
      }
    }
  }
}

/**
 * 4 colorful visual quick-action cards for the Home Feed.
 * Directly links users to the dedicated feature modules with lovely micro-graphics!
 */
@Composable
fun CampusQuickActionGrid(
  onSelectLostFound: () -> Unit,
  onSelectStudyPartner: () -> Unit,
  onSelectProjectPartner: () -> Unit,
  onSelectEvents: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    QuickActionTile(
      title = "Lost & Found",
      emoji = "🔎",
      subtitle = "Report / Claim",
      gradientColors = listOf(Color(0xFFB71C1C), Color(0xFFFF5722)),
      onClick = onSelectLostFound,
      modifier = Modifier.weight(1f)
    )

    QuickActionTile(
      title = "Study Squad",
      emoji = "📚",
      subtitle = "Exam prep",
      gradientColors = listOf(Color(0xFF00695C), Color(0xFF00BFA5)),
      onClick = onSelectStudyPartner,
      modifier = Modifier.weight(1f)
    )

    QuickActionTile(
      title = "Projects",
      emoji = "💻",
      subtitle = "Hackathons",
      gradientColors = listOf(Color(0xFF4A148C), Color(0xFF7C4DFF)),
      onClick = onSelectProjectPartner,
      modifier = Modifier.weight(1f)
    )

    QuickActionTile(
      title = "Events",
      emoji = "🎉",
      subtitle = "Fests & Talks",
      gradientColors = listOf(Color(0xFFE65100), Color(0xFFFFB300)),
      onClick = onSelectEvents,
      modifier = Modifier.weight(1f)
    )
  }
}

@Composable
private fun QuickActionTile(
  title: String,
  emoji: String,
  subtitle: String,
  gradientColors: List<Color>,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() },
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(Brush.verticalGradient(gradientColors))
        .padding(10.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
      ) {
        // Glowing circular icon container
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(Color.White.copy(alpha = 0.25f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Text(emoji, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = title,
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
          color = Color.White,
          maxLines = 1
        )

        Text(
          text = subtitle,
          style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
          color = Color.White.copy(alpha = 0.85f),
          maxLines = 1
        )
      }
    }
  }
}
