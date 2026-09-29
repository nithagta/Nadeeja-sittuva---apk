package com.example.sittuva.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sittuva.model.Language
import com.example.sittuva.model.PaymentStatus
import com.example.sittuva.model.PayoutStatus
import com.example.sittuva.model.SittuvaCalculator
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun SittuvaGlowingCard(
    modifier: Modifier = Modifier,
    isGlowing: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val borderColor = if (isGlowing) SittuvaRedGlow else SittuvaDarkCardBorder
    val glowBrush = Brush.linearGradient(
        colors = if (isGlowing) listOf(SittuvaDarkCardBorder, SittuvaCrimson, SittuvaDarkCardBorder)
        else listOf(SittuvaDarkCardBorder, Color(0xFF1E0E12))
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                BorderStroke(if (isGlowing) 1.5.dp else 1.dp, if (isGlowing) borderColor else SittuvaDarkCardBorder),
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isGlowing) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .background(glowBrush)
                .padding(18.dp),
            content = content
        )
    }
}

@Composable
fun SittuvaHeader(
    currentLanguage: Language,
    onLanguageToggle: (Language) -> Unit,
    isAdminMode: Boolean,
    onRoleToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(SittuvaRedGlow, SittuvaCrimson)
                            )
                        )
                        .border(1.5.dp, SittuvaGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "සී",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = SittuvaStrings.appTitle(currentLanguage),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SittuvaGold
                        )
                        Text(
                            text = "🇱🇰",
                            fontSize = 16.sp
                        )
                    }
                    Text(
                        text = SittuvaStrings.appTagline(currentLanguage),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Controls: Language Toggle & Mode Switch
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Language Switcher
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            val next = if (currentLanguage == Language.SINHALA) Language.ENGLISH else Language.SINHALA
                            onLanguageToggle(next)
                        }
                        .testTag("language_toggle_button")
                ) {
                    Text(
                        text = if (currentLanguage == Language.SINHALA) "සිංහල" else "ENG",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                // Admin / Member Mode Toggle
                FilterChip(
                    selected = isAdminMode,
                    onClick = { onRoleToggle(!isAdminMode) },
                    label = {
                        Text(
                            text = if (isAdminMode) "Admin" else "Member",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isAdminMode) Icons.Default.AdminPanelSettings else Icons.Default.Person,
                            contentDescription = "Role Mode",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SittuvaCrimson,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = SittuvaGold
                    ),
                    modifier = Modifier.testTag("role_mode_toggle")
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: String,
    language: Language,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status) {
        PaymentStatus.PAID.name, PayoutStatus.COMPLETED.name -> Triple(
            StatusSuccessBg,
            StatusSuccess,
            SittuvaStrings.statusPaid(language)
        )
        PaymentStatus.PENDING.name, PayoutStatus.SCHEDULED.name -> Triple(
            StatusPendingBg,
            StatusPending,
            SittuvaStrings.statusPending(language)
        )
        PaymentStatus.FAILED.name, PayoutStatus.FAILED.name -> Triple(
            StatusFailedBg,
            StatusFailed,
            SittuvaStrings.statusFailed(language)
        )
        PaymentStatus.OVERDUE.name -> Triple(
            Color(0xFFFFEBEE),
            StatusOverdue,
            SittuvaStrings.statusOverdue(language)
        )
        "ACTIVE" -> Triple(
            Color(0xFFE8F5E9),
            Color(0xFF2E7D32),
            SittuvaStrings.statusActive(language)
        )
        "SETUP" -> Triple(
            Color(0xFFFFF3E0),
            Color(0xFFE65100),
            SittuvaStrings.statusSetup(language)
        )
        "COMPLETED" -> Triple(
            Color(0xFFEDE7F6),
            Color(0xFF512DA8),
            SittuvaStrings.statusCompleted(language)
        )
        else -> Triple(Color.DarkGray, Color.White, status)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SittuvaInvitationCard(
    groupName: String,
    adminName: String,
    contributionAmount: Long,
    frequency: String,
    capacity: Int,
    currentCount: Int,
    inviteCode: String,
    language: Language,
    onJoinClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val remaining = capacity - currentCount
    val isFull = remaining <= 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(1.5.dp, SittuvaRedGlow), RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SittuvaDarkCard)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(SittuvaDarkCard, SittuvaCrimson.copy(alpha = 0.35f))
                    )
                )
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Emblem
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(SittuvaCrimson)
                    .border(2.dp, SittuvaGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = "Sittuva Logo",
                    tint = SittuvaGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = SittuvaStrings.inviteTitle(language),
                style = MaterialTheme.typography.labelMedium,
                color = SittuvaGoldLight,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = groupName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Text(
                text = "පරිපාලක (Admin): $adminName",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = SittuvaDarkCardBorder)
            Spacer(modifier = Modifier.height(16.dp))

            // Key Group Attributes Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = SittuvaStrings.contributionPerPerson(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    Text(
                        text = SittuvaCalculator.formatCurrency(contributionAmount, language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = SittuvaGold
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = SittuvaStrings.frequency(language),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    Text(
                        text = if (frequency == "WEEKLY") SittuvaStrings.weekly(language) else SittuvaStrings.monthly(language),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Member Progress & Remaining Slots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${SittuvaStrings.participants(language)}: $currentCount / $capacity",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
                Text(
                    text = if (isFull) SittuvaStrings.groupIsFull(language) else SittuvaStrings.slotsRemaining(remaining, language),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isFull) SittuvaRedAccent else SittuvaGoldLight
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentCount.toFloat() / capacity.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (isFull) SittuvaRedAccent else SittuvaGold,
                trackColor = SittuvaDarkCardBorder
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            if (isFull) {
                Surface(
                    color = SittuvaCrimson.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SittuvaRedGlow),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = SittuvaStrings.groupIsFull(language),
                            fontWeight = FontWeight.Bold,
                            color = SittuvaRedAccent,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = SittuvaStrings.askAdminForAnother(language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                Button(
                    onClick = onJoinClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("join_sittuva_group_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.GroupAdd, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = SittuvaStrings.joinGroup(language),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onShareClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("share_invitation_button"),
                border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = SittuvaGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${SittuvaStrings.shareInvite(language)} ($inviteCode)",
                    color = SittuvaGold,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}
