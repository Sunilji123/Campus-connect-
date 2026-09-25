package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentProfile
import com.example.data.model.VerificationMethod

@Composable
fun StudentVerificationDialog(
  currentUser: StudentProfile,
  onDismiss: () -> Unit,
  onVerify: (method: VerificationMethod, data: String) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var emailInput by remember { mutableStateOf(currentUser.email) }
  var otpInput by remember { mutableStateOf("") }
  var otpSent by remember { mutableStateOf(false) }
  var idCardNumber by remember { mutableStateOf(currentUser.rollNo) }
  var selectedDocName by remember { mutableStateOf<String?>(null) }
  var isSubmitting by remember { mutableStateOf(false) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {},
    dismissButton = {},
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(
              Brush.linearGradient(listOf(Color(0xFF651FFF), Color(0xFF00B0FF))),
              CircleShape
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "Verify MGUG Student Status",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Unlock verified badge, 1-on-1 & squad chat",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.clip(RoundedCornerShape(12.dp))
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = { selectedTab = 0 },
            text = { Text("College Email", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
            icon = { Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
          Tab(
            selected = selectedTab == 1,
            onClick = { selectedTab = 1 },
            text = { Text("ID Card Upload", fontWeight = FontWeight.SemiBold, fontSize = 12.sp) },
            icon = { Icon(Icons.Default.Badge, contentDescription = null, modifier = Modifier.size(16.dp)) }
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedTab == 0) {
          // College Email OTP Verification
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
              text = "Enter your official Gorakhnath University email address (@mgug.ac.in):",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
              value = emailInput,
              onValueChange = { emailInput = it },
              label = { Text("MGUG Student Email") },
              placeholder = { Text("yourname@mgug.ac.in") },
              modifier = Modifier
                .fillMaxWidth()
                .testTag("verification_email_input"),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            if (!otpSent) {
              Button(
                onClick = { otpSent = true },
                enabled = emailInput.contains("@mgug.ac.in") || emailInput.contains("@"),
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("send_otp_button"),
                shape = RoundedCornerShape(12.dp)
              ) {
                Text("Send 6-Digit Verification Code")
              }
            } else {
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
              ) {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "Code sent to $emailInput! (Demo code: 829401)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }

              OutlinedTextField(
                value = otpInput,
                onValueChange = { if (it.length <= 6) otpInput = it },
                label = { Text("6-Digit OTP") },
                placeholder = { Text("829401") },
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("verification_otp_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
              )

              Button(
                onClick = {
                  isSubmitting = true
                  onVerify(VerificationMethod.COLLEGE_EMAIL_OTP, emailInput)
                },
                enabled = otpInput.length >= 4,
                modifier = Modifier
                  .fillMaxWidth()
                  .testTag("confirm_otp_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color(0xFF651FFF)
                )
              ) {
                if (isSubmitting) {
                  CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                  Text("Confirm & Verify Student Status ✨", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        } else {
          // Student Smart ID Upload Verification
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = "Upload a scan or clear photo of your MGUG Student Smart ID Card:",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OutlinedTextField(
              value = idCardNumber,
              onValueChange = { idCardNumber = it },
              label = { Text("Roll / Enrollment Number") },
              modifier = Modifier.fillMaxWidth(),
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )

            // Document attachment box
            Card(
              modifier = Modifier
                .fillMaxWidth()
                .border(
                  width = 1.dp,
                  color = if (selectedDocName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                  shape = RoundedCornerShape(12.dp)
                )
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                  selectedDocName = "mgug_id_card_${currentUser.rollNo.replace('/', '_')}.jpg"
                },
              colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            ) {
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Icon(
                  Icons.Default.FileOpen,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                  text = selectedDocName ?: "Tap to select Student ID Card image",
                  style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (selectedDocName != null) FontWeight.Bold else FontWeight.Normal
                  ),
                  color = if (selectedDocName != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "PNG, JPG or PDF up to 10MB",
                  style = MaterialTheme.typography.labelSmall,
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }

            Button(
              onClick = {
                isSubmitting = true
                onVerify(VerificationMethod.STUDENT_ID_CARD_UPLOAD, idCardNumber)
              },
              enabled = idCardNumber.isNotBlank(),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("submit_id_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF651FFF))
            ) {
              Text("Submit ID for Verification 🛡️", fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Cancel")
        }
      }
    }
  )
}
