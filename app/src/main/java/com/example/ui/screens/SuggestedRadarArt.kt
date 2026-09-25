package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated AI Synergy Radar Canvas Header:
 * Concentric pulsing scanner rings, rotating sweeping radar beam,
 * glowing student beacon nodes, and rich cyberpunk/electric aesthetic.
 */
@Composable
fun SuggestedRadarHeroArt(
  modifier: Modifier = Modifier,
  studentCount: Int = 12
) {
  val infiniteTransition = rememberInfiniteTransition(label = "radar")

  // Radar sweep angle: 0 to 360 degrees
  val sweepAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 3600, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "radarSweep"
  )

  // Pulsing ring scale
  val ringPulse by infiniteTransition.animateFloat(
    initialValue = 0.85f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 2000, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "ringPulse"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .height(156.dp)
      .clip(RoundedCornerShape(24.dp)),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF160D2D)),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      // Dynamic Canvas with animated radar
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val center = Offset(w * 0.82f, h * 0.5f)
        val maxRadius = h * 0.78f

        // Rich backdrop gradient
        drawRect(
          brush = Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF1E0A3C),
              Color(0xFF2C1052),
              Color(0xFF381564),
              Color(0xFF1A0A38)
            )
          )
        )

        // Concentric radar rings
        val rings = listOf(0.3f, 0.6f, 0.9f)
        rings.forEach { ratio ->
          drawCircle(
            color = Color(0xFF00E5FF).copy(alpha = 0.18f),
            radius = maxRadius * ratio * ringPulse,
            center = center,
            style = Stroke(width = 1.5f)
          )
        }

        // Radar crosshairs
        drawLine(
          color = Color(0xFF00E5FF).copy(alpha = 0.15f),
          start = Offset(center.x - maxRadius, center.y),
          end = Offset(center.x + maxRadius, center.y),
          strokeWidth = 1f
        )
        drawLine(
          color = Color(0xFF00E5FF).copy(alpha = 0.15f),
          start = Offset(center.x, center.y - maxRadius),
          end = Offset(center.x, center.y + maxRadius),
          strokeWidth = 1f
        )

        // Rotating radar beam
        val radians = Math.toRadians(sweepAngle.toDouble())
        val beamEnd = Offset(
          x = (center.x + maxRadius * cos(radians)).toFloat(),
          y = (center.y + maxRadius * sin(radians)).toFloat()
        )
        drawLine(
          brush = Brush.linearGradient(
            colors = listOf(
              Color(0xFFFF007A),
              Color(0xFF7C4DFF),
              Color(0xFF00E5FF)
            ),
            start = center,
            end = beamEnd
          ),
          start = center,
          end = beamEnd,
          strokeWidth = 3f,
          cap = StrokeCap.Round
        )

        // Center hub glow
        drawCircle(
          color = Color(0xFF00E5FF),
          radius = 5f,
          center = center
        )
        drawCircle(
          color = Color(0xFF00E5FF).copy(alpha = 0.35f),
          radius = 12f * ringPulse,
          center = center
        )

        // Student beacon dots on radar
        val beaconDots = listOf(
          Offset(center.x - maxRadius * 0.45f, center.y - maxRadius * 0.35f) to Color(0xFFFF4081),
          Offset(center.x + maxRadius * 0.55f, center.y - maxRadius * 0.25f) to Color(0xFF00E5FF),
          Offset(center.x - maxRadius * 0.25f, center.y + maxRadius * 0.55f) to Color(0xFFFFD600),
          Offset(center.x + maxRadius * 0.35f, center.y + maxRadius * 0.45f) to Color(0xFF00E676),
          Offset(center.x - maxRadius * 0.65f, center.y + maxRadius * 0.15f) to Color(0xFFE040FB)
        )

        beaconDots.forEach { (pos, color) ->
          drawCircle(
            color = color.copy(alpha = 0.35f),
            radius = 8f * ringPulse,
            center = pos
          )
          drawCircle(
            color = color,
            radius = 3.5f,
            center = pos
          )
        }
      }

      // Foreground content & typography
      Row(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(
          modifier = Modifier.weight(1f),
          verticalArrangement = Arrangement.Center
        ) {
          // Pill badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFF4081).copy(alpha = 0.2f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF4081).copy(alpha = 0.4f))
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color(0xFFFF4081),
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "CAMPUS AI RADAR",
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
                color = Color(0xFFFF80AB)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Smart Student Match",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Black,
              fontSize = 20.sp
            ),
            color = Color.White
          )

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = "$studentCount potential partners scanning active courses, hackathons & interests",
            style = MaterialTheme.typography.bodySmall.copy(
              fontSize = 11.sp,
              lineHeight = 15.sp
            ),
            color = Color(0xFFD1C4E9),
            maxLines = 2
          )
        }

        Spacer(modifier = Modifier.width(90.dp))
      }
    }
  }
}

/**
 * Redesigned Daily Free Pass Widget:
 * Energy gauge, daily progress bar, and instant connect booster.
 */
@Composable
fun SuggestedDailyPassWidget(
  remainingConnects: Int,
  onAddMoreConnects: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("suggested_pass_card"),
    shape = RoundedCornerShape(22.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              Color(0xFF651FFF).copy(alpha = 0.07f),
              Color(0xFFFF4081).copy(alpha = 0.05f),
              Color(0xFF00B0FF).copy(alpha = 0.07f)
            )
          )
        )
        .border(
          width = 1.dp,
          brush = Brush.horizontalGradient(
            listOf(
              Color(0xFF651FFF).copy(alpha = 0.25f),
              Color(0xFFFF4081).copy(alpha = 0.25f)
            )
          ),
          shape = RoundedCornerShape(22.dp)
        )
        .padding(16.dp)
    ) {
      Column(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            // Glowing Energy Bolt Icon Container
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF7C4DFF), Color(0xFFFF4081))
                  ),
                  CircleShape
                ),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ElectricBolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "Daily Connect Passes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Safe & curated student outreach",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          // Passes badge
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = if (remainingConnects > 3) Color(0xFF651FFF) else Color(0xFFFF5252)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(2.dp))
              Text(
                text = "$remainingConnects Left",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Progress indicator with gradient
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "$remainingConnects of 15 remaining today",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "Auto-renews at 00:00",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF651FFF)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          LinearProgressIndicator(
            progress = { (remainingConnects / 15f).coerceIn(0f, 1f) },
            modifier = Modifier
              .fillMaxWidth()
              .height(8.dp)
              .clip(RoundedCornerShape(4.dp)),
            color = Color(0xFF651FFF),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
          )
        }

        // Booster banner if <= 4 connects remaining
        if (remainingConnects <= 4) {
          Spacer(modifier = Modifier.height(12.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Color(0xFF651FFF).copy(alpha = 0.08f),
                RoundedCornerShape(14.dp)
              )
              .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "⚡ Need to connect with more peers?",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "Instant booster pack of 10 connections",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Button(
              onClick = onAddMoreConnects,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF651FFF)
              ),
              contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Text("+10 Extra (₹20)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  }
}
