package com.example.ui.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatUtils {

  fun formatRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    if (diff < 0) return "Just now"

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
      seconds < 60 -> "Just now"
      minutes < 60 -> "${minutes}m ago"
      hours < 24 -> "${hours}h ago"
      days == 1L -> "Yesterday"
      days < 7 -> "${days}d ago"
      else -> {
        val sdf = SimpleDateFormat("MMM d", Locale.getDefault())
        sdf.format(Date(timestamp))
      }
    }
  }

  fun formatFullDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
  }
}
