package com.example.sittuva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sittuva.data.local.GroupEntity
import com.example.sittuva.model.Language
import com.example.sittuva.model.SittuvaCalculator
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun JoinGroupScreen(
    group: GroupEntity?,
    currentMembersCount: Int,
    language: Language,
    onJoinSuccess: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var phoneInput by remember { mutableStateOf("+947") }
    var nameInput by remember { mutableStateOf("") }
    var otpInput by remember { mutableStateOf("") }
    var generatedOtp by remember { mutableStateOf<String?>(null) }
    var isOtpSent by remember { mutableStateOf(false) }
    var isOtpVerified by remember { mutableStateOf(false) }
    var countdownSeconds by remember { mutableStateOf(60) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showJoinSuccessDialog by remember { mutableStateOf(false) }
    var assignedPosition by remember { mutableStateOf(currentMembersCount + 1) }

    val remainingSlots = if (group != null) group.participantLimit - currentMembersCount else 0
    val isFull = remainingSlots <= 0

    // Timer countdown when OTP is sent
    LaunchedEffect(isOtpSent) {
        if (isOtpSent) {
            countdownSeconds = 60
            while (countdownSeconds > 0) {
                delay(1000)
                countdownSeconds--
            }
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = if (language == Language.SINHALA) "සිට්ටුවට එක්වීම (Join Sittuva)" else "Join Sittuva Group",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGold
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (group == null) {
                Text(
                    text = if (language == Language.SINHALA) "සමූහය හමු නොවීය." else "Group not found.",
                    color = SittuvaRedAccent
                )
                return@Scaffold
            }

            // Group Info Card
            SittuvaGlowingCard {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Text(
                    text = "පරිපාලක (Admin): ${group.adminName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = SittuvaDarkCardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = SittuvaStrings.contributionPerPerson(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = SittuvaCalculator.formatCurrency(group.contributionAmount, language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (language == Language.SINHALA) "ඉතිරි ඉඩ" else "Available Slots",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = if (isFull) SittuvaStrings.groupIsFull(language) else "$remainingSlots / ${group.participantLimit}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isFull) SittuvaRedAccent else StatusSuccess
                        )
                    }
                }
            }

            // Error Notice
            if (errorMessage != null) {
                Surface(
                    color = StatusFailedBg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, StatusFailed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage ?: "",
                        color = StatusFailed,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            if (isFull) {
                // Group FULL Banner
                Surface(
                    color = SittuvaCrimson.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, SittuvaRedGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.GroupOff, contentDescription = null, tint = SittuvaRedAccent, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = SittuvaStrings.groupIsFull(language),
                            fontWeight = FontWeight.Bold,
                            color = SittuvaRedAccent,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = SittuvaStrings.askAdminForAnother(language),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                // Join Form: Phone OTP Verification
                SittuvaGlowingCard(isGlowing = true) {
                    Text(
                        text = if (language == Language.SINHALA) "දුරකථන අංකය මගින් සත්‍යාපනය (OTP)" else "Phone OTP Verification",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 1: Phone number
                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text(if (language == Language.SINHALA) "දුරකථන අංකය (+94)" else "Phone Number") },
                        enabled = !isOtpVerified,
                        modifier = Modifier.fillMaxWidth().testTag("join_phone_input"),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isOtpSent) {
                        Button(
                            onClick = {
                                if (phoneInput.length < 10) {
                                    errorMessage = if (language == Language.SINHALA) "කරුණාකර වලංගු දුරකථන අංකයක් ඇතුළත් කරන්න." else "Please enter a valid phone number."
                                    return@Button
                                }
                                errorMessage = null
                                val randomOtp = (100000..999999).random().toString()
                                generatedOtp = randomOtp
                                isOtpSent = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SittuvaCrimson),
                            modifier = Modifier.fillMaxWidth().testTag("send_otp_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (language == Language.SINHALA) "OTP කේතය ලබාගන්න" else "Send OTP")
                        }
                    } else if (!isOtpVerified) {
                        // Display simulated secure OTP notice
                        Surface(
                            color = SittuvaDarkSurface,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🔐 Sittuva SMS OTP: $generatedOtp (${countdownSeconds}s)",
                                color = SittuvaGold,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = otpInput,
                            onValueChange = { otpInput = it.take(6) },
                            label = { Text("6-Digit OTP") },
                            modifier = Modifier.fillMaxWidth().testTag("otp_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                if (otpInput == generatedOtp) {
                                    isOtpVerified = true
                                    errorMessage = null
                                } else {
                                    errorMessage = if (language == Language.SINHALA) "OTP කේතය වැරදියි. කරුණාකර නැවත බලන්න." else "Invalid OTP. Please check the code."
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                            modifier = Modifier.fillMaxWidth().testTag("verify_otp_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (language == Language.SINHALA) "OTP සත්‍යාපනය කරන්න" else "Verify OTP")
                        }
                    }

                    // Step 2: Name input once OTP verified
                    if (isOtpVerified) {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            label = { Text(if (language == Language.SINHALA) "ඔබගේ සම්පූර්ණ නම" else "Your Full Name") },
                            modifier = Modifier.fillMaxWidth().testTag("join_name_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (nameInput.isBlank()) {
                                    errorMessage = if (language == Language.SINHALA) "කරුණාකර ඔබගේ නම ඇතුළත් කරන්න." else "Please enter your name."
                                    return@Button
                                }
                                assignedPosition = currentMembersCount + 1
                                onJoinSuccess(nameInput, phoneInput)
                                showJoinSuccessDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_and_join_button")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.SINHALA) "තහවුරු කර සමූහයට එක්වන්න" else "Confirm & Join Group",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }

    // Join Success Dialog
    if (showJoinSuccessDialog) {
        AlertDialog(
            onDismissRequest = onBack,
            title = {
                Text(
                    text = if (language == Language.SINHALA) "සාර්ථකව එක්විය! 🎉" else "Successfully Joined! 🎉",
                    fontWeight = FontWeight.Bold,
                    color = SittuvaGold
                )
            },
            text = {
                Text(
                    text = if (language == Language.SINHALA)
                        "ඔබ '${group?.name}' සමූහයට සාර්ථකව එක්විය. ඔබගේ ගෙවීම් ස්ථානය #${assignedPosition} වේ."
                    else "You have joined '${group?.name}'. Your assigned payout position is #${assignedPosition}."
                )
            },
            confirmButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = SittuvaCrimson)
                ) {
                    Text(if (language == Language.SINHALA) "පුවරුවට පිවිසෙන්න" else "Go to Dashboard")
                }
            },
            containerColor = SittuvaDarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
