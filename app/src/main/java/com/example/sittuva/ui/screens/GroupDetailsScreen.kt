package com.example.sittuva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sittuva.data.local.*
import com.example.sittuva.model.*
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.components.SittuvaInvitationCard
import com.example.sittuva.ui.components.StatusBadge
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun GroupDetailsScreen(
    group: GroupEntity,
    members: List<GroupMemberEntity>,
    payments: List<PaymentEntity>,
    payouts: List<PayoutEntity>,
    language: Language,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onDisbursePayout: (Int) -> Unit,
    onSimulatePayment: (PaymentEntity) -> Unit,
    onSimulateFailedPayment: (PaymentEntity) -> Unit,
    onShareInvite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Rotation & Payouts, 1: Current Period Payments, 2: Invite & Members
    val totalPeriodPayout = SittuvaCalculator.calculateTotalPayout(group.contributionAmount, group.participantLimit)
    val durationText = SittuvaCalculator.calculateDurationText(
        group.participantLimit,
        if (group.frequency == "WEEKLY") Frequency.WEEKLY else Frequency.MONTHLY,
        language
    )

    val currentPeriodPayments = payments.filter { it.periodNumber == group.currentPeriod }
    val allCurrentPaid = currentPeriodPayments.isNotEmpty() && currentPeriodPayments.all { it.status == PaymentStatus.PAID.name }
    val currentPeriodPayout = payouts.find { it.periodNumber == group.currentPeriod }

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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "Code: ${group.inviteCode} • $durationText",
                            style = MaterialTheme.typography.labelSmall,
                            color = SittuvaGoldLight
                        )
                    }
                    StatusBadge(status = group.status, language = language)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Lock Rule Banner
            item {
                Surface(
                    color = SittuvaDarkCard,
                    border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SittuvaGold, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = SittuvaStrings.lockedNotice(language),
                                fontWeight = FontWeight.Bold,
                                color = SittuvaGold,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                text = if (language == Language.SINHALA)
                                    "දායකත්ව මුදල (රු. ${group.contributionAmount}) සහ සාමාජික සංඛ්‍යාව (${group.participantLimit}) ආරක්ෂිතව අගුලු ලා ඇත."
                                else "Contribution (Rs. ${group.contributionAmount}) and participants (${group.participantLimit}) are locked.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }

            // Summary Header Card
            item {
                SittuvaGlowingCard(isGlowing = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = SittuvaStrings.totalPool(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = SittuvaCalculator.formatCurrency(totalPeriodPayout, language),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Black,
                                color = SittuvaGold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = SittuvaStrings.currentPeriod(language),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                            Text(
                                text = "${group.currentPeriod} / ${group.participantLimit}",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Payout Action when all payments received
                    if (isAdmin && group.status == "ACTIVE") {
                        if (allCurrentPaid && currentPeriodPayout?.status != PayoutStatus.COMPLETED.name) {
                            Button(
                                onClick = { onDisbursePayout(group.currentPeriod) },
                                colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("disburse_payout_button")
                            ) {
                                Icon(imageVector = Icons.Default.Send, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${SittuvaStrings.disbursePayout(language)} (${SittuvaCalculator.formatCurrency(totalPeriodPayout, language)})",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (currentPeriodPayout?.status == PayoutStatus.COMPLETED.name) {
                            Surface(
                                color = StatusSuccessBg,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == Language.SINHALA) "වටය #${group.currentPeriod} මුදල් සාර්ථකව බැර කර ඇත." else "Period #${group.currentPeriod} Payout Completed.",
                                        color = StatusSuccess,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Tab Navigation
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = SittuvaGold
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text(if (language == Language.SINHALA) "ගෙවීම් අනුපිළිවෙල" else "Payout Schedule") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (language == Language.SINHALA) "වත්මන් දායකත්ව" else "Current Contributions") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text(if (language == Language.SINHALA) "ආරාධනා සහ සාමාජිකයින්" else "Invite & Members") }
                    )
                }
            }

            // Tab 0: Payout Schedule / Rotation Timeline
            if (selectedTab == 0) {
                items(payouts) { p ->
                    val isCurrent = p.periodNumber == group.currentPeriod
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isCurrent) SittuvaCrimson.copy(alpha = 0.35f) else SittuvaDarkSurface,
                        border = BorderStroke(1.dp, if (isCurrent) SittuvaRedGlow else SittuvaDarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isCurrent) SittuvaRedGlow else SittuvaCrimson),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${p.periodNumber}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = p.recipientName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = p.bankAccountMask ?: "Bank Payout Transfer",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = SittuvaCalculator.formatCurrency(p.amount, language),
                                    fontWeight = FontWeight.Bold,
                                    color = SittuvaGold
                                )
                                StatusBadge(status = p.status, language = language)
                            }
                        }
                    }
                }
            }

            // Tab 1: Current Period Contributions
            if (selectedTab == 1) {
                item {
                    Text(
                        text = if (language == Language.SINHALA) "වටය #${group.currentPeriod} සඳහා සාමාජික දායකත්ව" else "Member Contributions for Period #${group.currentPeriod}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGoldLight
                    )
                }

                items(currentPeriodPayments) { payment ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SittuvaDarkSurface,
                        border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = payment.userName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = payment.providerReference ?: SittuvaCalculator.formatCurrency(payment.amount, language),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusBadge(status = payment.status, language = language)

                                // Quick simulation actions for Admin
                                if (isAdmin && payment.status != PaymentStatus.PAID.name) {
                                    IconButton(
                                        onClick = { onSimulatePayment(payment) },
                                        modifier = Modifier.size(32.dp).testTag("simulate_payment_${payment.id}")
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = "Mark Paid", tint = StatusSuccess)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Tab 2: Invitation & Members
            if (selectedTab == 2) {
                item {
                    SittuvaInvitationCard(
                        groupName = group.name,
                        adminName = group.adminName,
                        contributionAmount = group.contributionAmount,
                        frequency = group.frequency,
                        capacity = group.participantLimit,
                        currentCount = members.size,
                        inviteCode = group.inviteCode,
                        language = language,
                        onJoinClick = {},
                        onShareClick = { onShareInvite(group.inviteCode) }
                    )
                }

                item {
                    Text(
                        text = if (language == Language.SINHALA) "සමූහයේ සාමාජිකයින් (${members.size}/${group.participantLimit})" else "Registered Members (${members.size}/${group.participantLimit})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGold
                    )
                }

                items(members) { m ->
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (m.isDefaultAdmin) SittuvaGold else SittuvaCrimson),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${m.payoutPosition}",
                                        color = if (m.isDefaultAdmin) Color.Black else Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (m.isDefaultAdmin) "${m.userName} (Admin)" else m.userName,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = m.userPhone,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Text(
                                text = "ස්ථානය: #${m.payoutPosition}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SittuvaGoldLight
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}
