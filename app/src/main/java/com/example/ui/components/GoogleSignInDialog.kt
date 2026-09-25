package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.firebase.FirebaseAuthManager
import com.example.data.model.StudentProfile
import com.example.data.repository.MgugRealtimeRepository

@Composable
fun GoogleSignInDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val firebaseUser by FirebaseAuthManager.currentUser.collectAsState()
  val isLoading by FirebaseAuthManager.isLoading.collectAsState()
  val errorMessage by FirebaseAuthManager.errorMessage.collectAsState()

  var customGmail by remember { mutableStateOf("chauhansunil3410@gmail.com") }
  var customName by remember { mutableStateOf("Sunil Chauhan") }
  var isEnteringCustomEmail by remember { mutableStateOf(false) }

  Dialog(
    onDismissRequest = { if (!isLoading) onDismiss() },
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxWidth(0.92f)
        .clip(RoundedCornerShape(24.dp))
        .testTag("google_sign_in_dialog"),
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 8.dp,
      shape = RoundedCornerShape(24.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                  Brush.linearGradient(
                    listOf(Color(0xFF4285F4), Color(0xFF34A853), Color(0xFFFBBC05), Color(0xFFEA4335))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Box(
                modifier = Modifier
                  .size(36.dp)
                  .clip(CircleShape)
                  .background(Color.White),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "G",
                  fontWeight = FontWeight.Black,
                  fontSize = 20.sp,
                  color = Color(0xFF4285F4)
                )
              }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = "Google Sign-In",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "MGUG Campus Cloud",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }

          IconButton(
            onClick = onDismiss,
            enabled = !isLoading,
            modifier = Modifier.testTag("btn_close_google_dialog")
          ) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State Check: Already signed in or not
        val isSignedIn = firebaseUser != null || currentUser.isGoogleUser
        val activeEmail = firebaseUser?.email ?: if (currentUser.isGoogleUser) currentUser.email else null

        if (isSignedIn && activeEmail != null) {
          // Already signed in view
          Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
              containerColor = Color(0xFFE8F5E9)
            ),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF81C784))
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(32.dp)
              )
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(
                  text = "Connected with Google",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color(0xFF1B5E20)
                )
                Text(
                  text = activeEmail,
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF2E7D32),
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Name: ${currentUser.name}",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF388E3C)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Your campus posts, real-time chats, polls, and student profile are continuously synchronized with Cloud Firestore.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedButton(
            onClick = {
              FirebaseAuthManager.signOut(context)
              onDismiss()
            },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("btn_google_sign_out"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.error
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
          ) {
            Text("Sign Out of Google", fontWeight = FontWeight.SemiBold)
          }
        } else {
          // Sign In Actions
          Text(
            text = "Log in with your Gmail account to unlock real-time campus sync across devices, verified profile badges, and cloud persistence.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )

          if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
              ),
              shape = RoundedCornerShape(12.dp)
            ) {
              Text(
                text = errorMessage ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(12.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          if (isLoading) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 16.dp)
            ) {
              CircularProgressIndicator(modifier = Modifier.size(36.dp))
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Connecting to Google...",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          } else {
            // Interactive Credential Manager Google Sign-In Button
            Button(
              onClick = {
                FirebaseAuthManager.signInWithGoogle(context) { success ->
                  if (success) {
                    onDismiss()
                  }
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_continue_with_google"),
              shape = RoundedCornerShape(14.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4285F4),
                contentColor = Color.White
              ),
              elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                  contentAlignment = Alignment.Center
                ) {
                  Text(
                    text = "G",
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = Color(0xFF4285F4)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = "Continue with Google",
                  fontWeight = FontWeight.Bold,
                  fontSize = 15.sp
                )
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick 1-tap Gmail login option (especially convenient for preview & emulator environments)
            Card(
              modifier = Modifier.fillMaxWidth(),
              colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
              ),
              shape = RoundedCornerShape(16.dp),
              border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
              Column(modifier = Modifier.padding(14.dp)) {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      Icons.Default.Email,
                      contentDescription = null,
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                      text = "Fast Gmail Connect",
                      style = MaterialTheme.typography.labelLarge,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }
                  Text(
                    text = "Instant",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (!isEnteringCustomEmail) {
                  Text(
                    text = "Log in instantly with your verified student account:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Spacer(modifier = Modifier.height(6.dp))
                  Text(
                    text = "chauhansunil3410@gmail.com",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                  )
                  Text(
                    text = "Sunil Chauhan · Faculty of Science & Tech",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                      onClick = {
                        MgugRealtimeRepository.loginWithGmail("chauhansunil3410@gmail.com", "Sunil Chauhan")
                        onDismiss()
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("btn_fast_gmail_sunil"),
                      shape = RoundedCornerShape(10.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                      )
                    ) {
                      Text("Log in as Sunil", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                      onClick = { isEnteringCustomEmail = true },
                      shape = RoundedCornerShape(10.dp),
                      modifier = Modifier.testTag("btn_other_gmail")
                    ) {
                      Text("Other Gmail")
                    }
                  }
                } else {
                  OutlinedTextField(
                    value = customGmail,
                    onValueChange = { customGmail = it },
                    label = { Text("Gmail Address") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("input_custom_gmail")
                  )

                  Spacer(modifier = Modifier.height(8.dp))

                  OutlinedTextField(
                    value = customName,
                    onValueChange = { customName = it },
                    label = { Text("Your Full Name") },
                    leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                      .fillMaxWidth()
                      .testTag("input_custom_name")
                  )

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                      onClick = {
                        if (customGmail.isNotBlank()) {
                          MgugRealtimeRepository.loginWithGmail(customGmail, customName)
                          onDismiss()
                        }
                      },
                      modifier = Modifier
                        .weight(1f)
                        .testTag("btn_confirm_custom_gmail"),
                      shape = RoundedCornerShape(10.dp)
                    ) {
                      Text("Log In", fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    TextButton(onClick = { isEnteringCustomEmail = false }) {
                      Text("Back")
                    }
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            Icons.Default.Lock,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "Secured via Firebase Authentication & Cloud Firestore",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 11.sp
          )
        }
      }
    }
  }
}
