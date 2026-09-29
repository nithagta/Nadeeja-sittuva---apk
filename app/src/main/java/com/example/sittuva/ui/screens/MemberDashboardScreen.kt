package com.example.sittuva.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sittuva.data.local.*
import com.example.sittuva.model.*
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.components.StatusBadge
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun MemberDashboardScreen(
    currentMember: UserEntity,
    group: GroupEntity?,
    memberRecord: GroupMemberEntity?,
    payments: List<PaymentEntity>,
    payouts: List<PayoutEntity>,
    allMembers: List<GroupMemberEntity>,
    language: Language,
    onPayNowClick: (PaymentEntity) -> Unit,
    onOpenBankAccount: () -> Unit,
    onJoinAnotherGroup: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (group == null || memberRecord == null) {
        // No group joined yet
        Box(
            modifier = modifier.fillMaxSize().padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            SittuvaGlowingCard {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = SittuvaGold, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (language == Language.SINHALA) "ඔබ තවමත් කිසිදු සිට්ටු සමූහයකට එක්වී නැත." else "You haven't joined a Sittuva group yet.",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onJoinAnotherGroup,
                        colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(SittuvaStrings.joinGroup(language))
                    }
                }
            }
        }
        return
    }

    val totalPool = SittuvaCalculator.calculateTotalPayout(group.contributionAmount, group.participantLimit)
    val myScheduledPayout = payouts.find { it.recipientUserId == currentMember.id }
    val myPendingPayment = payments.find { it.userId == currentMember.id && it.periodNumber == group.currentPeriod && it.status != PaymentStatus.PAID.name }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero Position & Payout Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(BorderStroke(1.5.dp, SittuvaRedGlow), RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SittuvaDarkCard)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(SittuvaCrimson, SittuvaDarkCard)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.SINHALA) "ඔබගේ ස්ථානය (Your Position): #${memberRecord.payoutPosition}" else "Your Position: #${memberRecord.payoutPosition}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = SittuvaGold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(SittuvaDarkSurface)
                                .border(1.5.dp, SittuvaGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${memberRecord.payoutPosition}",
                                color = SittuvaGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = SittuvaDarkCardBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (language == Language.SINHALA) "ඔබට ලැබෙන මුදල (Your Payout):" else "Your Payout Amount:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = SittuvaCalculator.formatCurrency(totalPool, language),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Black,
                                color = SittuvaGold
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (language == Language.SINHALA) "දායකත්වය (Contribution):" else "Your Contribution:",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = SittuvaCalculator.formatCurrency(group.contributionAmount, language),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payout Schedule Info
                    Surface(
                        color = SittuvaDarkSurface.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = SittuvaGoldLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.SINHALA) "ඔබේ වටය: වටය #${memberRecord.payoutPosition}" else "Your Scheduled Round: Period #${memberRecord.payoutPosition}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Current Period Payment Action Box
        if (myPendingPayment != null) {
            item {
                SittuvaGlowingCard(isGlowing = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.SINHALA) "වත්මන් වටයේ දායකත්වය ගෙවන්න" else "Due Contribution (Period #${group.currentPeriod})",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = SittuvaGold
                            )
                            Text(
                                text = "${SittuvaCalculator.formatCurrency(group.contributionAmount, language)} • ${SittuvaStrings.statusPending(language)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = SittuvaRedAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = { onPayNowClick(myPendingPayment) },
                            colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("member_pay_now_button")
                        ) {
                            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(SittuvaStrings.payNow(language), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Payout Bank Account Status
        item {
            OutlinedCard(
                onClick = onOpenBankAccount,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SittuvaDarkCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = SittuvaGold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = SittuvaStrings.bankAccount(language),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = if (language == Language.SINHALA) "මුදල් ලබා ගැනීමේ බැංකු තොරතුරු" else "Registered Bank Account for Payouts",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
                }
            }
        }

        // Group Rotation Progress & Members List
        item {
            Text(
                text = if (language == Language.SINHALA) "සිට්ටු වට සහ සාමාජිකයින් (Rotation Order)" else "Rotation Order & Group Members",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SittuvaGold
            )
        }

        items(allMembers) { member ->
            val isCurrent = member.payoutPosition == group.currentPeriod
            val isMe = member.userId == currentMember.id

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isCurrent) SittuvaCrimson.copy(alpha = 0.35f) else SittuvaDarkSurface,
                border = BorderStroke(1.dp, if (isCurrent) SittuvaRedGlow else if (isMe) SittuvaGold else SittuvaDarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (isCurrent) SittuvaRedGlow else SittuvaDarkCardBorder),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${member.payoutPosition}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isMe) "${member.userName} (You)" else member.userName,
                                fontWeight = FontWeight.Bold,
                                color = if (isMe) SittuvaGold else Color.White
                            )
                            if (isCurrent) {
                                Text(
                                    text = if (language == Language.SINHALA) "වත්මන් වටයේ ලාභියා" else "Current Period Recipient",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SittuvaGoldLight
                                )
                            }
                        }
                    }

                    if (member.payoutPosition < group.currentPeriod) {
                        Surface(
                            color = StatusSuccessBg,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.SINHALA) "ලබාගෙන ඇත" else "Received",
                                color = StatusSuccess,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Payment History
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (language == Language.SINHALA) "ගෙවීම් ඉතිහාසය (Payment History)" else "Payment History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = SittuvaGold
            )
        }

        items(payments.filter { it.userId == currentMember.id }) { p ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SittuvaDarkSurface,
                border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Period #${p.periodNumber}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = p.providerReference ?: SittuvaCalculator.formatDate(p.createdAt, language),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = SittuvaCalculator.formatCurrency(p.amount, language),
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        StatusBadge(status = p.status, language = language)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
