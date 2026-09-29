package com.example.sittuva.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.example.sittuva.data.local.GroupEntity
import com.example.sittuva.model.Language
import com.example.sittuva.model.SittuvaCalculator
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.components.StatusBadge
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    groups: List<GroupEntity>,
    language: Language,
    onSelectGroup: (String) -> Unit,
    onRequestCreateGroup: () -> Unit,
    onOpenAuditLogs: () -> Unit,
    onOpenFeedback: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeGroups = groups.filter { it.status == "ACTIVE" || it.status == "SETUP" }
    val completedGroups = groups.filter { it.status == "COMPLETED" }

    var showMaxGroupLimitDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (activeGroups.size >= SittuvaCalculator.MAX_ACTIVE_GROUPS_PER_ADMIN) {
                        showMaxGroupLimitDialog = true
                    } else {
                        onRequestCreateGroup()
                    }
                },
                icon = { Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = {
                    Text(
                        text = SittuvaStrings.createGroup(language),
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                containerColor = SittuvaRedGlow,
                modifier = Modifier.testTag("admin_create_group_fab")
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))
                // Quick Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AssistChip(
                        onClick = onOpenAuditLogs,
                        label = { Text(SittuvaStrings.auditLogs(language)) },
                        leadingIcon = { Icon(Icons.Default.HistoryEdu, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("open_audit_logs_button")
                    )
                    AssistChip(
                        onClick = onOpenFeedback,
                        label = { Text(SittuvaStrings.feedback(language)) },
                        leadingIcon = { Icon(Icons.Default.Feedback, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.testTag("open_feedback_button")
                    )
                }
            }

            // Active Groups Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${SittuvaStrings.activeGroups(language)} (${activeGroups.size}/${SittuvaCalculator.MAX_ACTIVE_GROUPS_PER_ADMIN})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = SittuvaGold
                    )
                    if (activeGroups.size >= SittuvaCalculator.MAX_ACTIVE_GROUPS_PER_ADMIN) {
                        Surface(
                            color = SittuvaCrimson.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.SINHALA) "උපරිම සීමාව" else "Max limit",
                                color = SittuvaRedAccent,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            if (activeGroups.isEmpty()) {
                item {
                    SittuvaGlowingCard {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(imageVector = Icons.Default.Groups, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (language == Language.SINHALA) "ක්‍රියාකාරී සිට්ටු කිසිවක් නැත." else "No active Sittuva groups yet.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onRequestCreateGroup,
                                colors = ButtonDefaults.buttonColors(containerColor = SittuvaCrimson)
                            ) {
                                Text(SittuvaStrings.createGroup(language))
                            }
                        }
                    }
                }
            } else {
                items(activeGroups) { group ->
                    AdminGroupCard(
                        group = group,
                        language = language,
                        onClick = { onSelectGroup(group.id) }
                    )
                }
            }

            // Completed Groups Section
            if (completedGroups.isNotEmpty()) {
                item {
                    Text(
                        text = "${SittuvaStrings.completedGroups(language)} (${completedGroups.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.LightGray
                    )
                }
                items(completedGroups) { group ->
                    AdminGroupCard(
                        group = group,
                        language = language,
                        onClick = { onSelectGroup(group.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(64.dp))
            }
        }
    }

    // Max 3 Groups Limit Alert Dialog
    if (showMaxGroupLimitDialog) {
        AlertDialog(
            onDismissRequest = { showMaxGroupLimitDialog = false },
            title = {
                Text(
                    text = if (language == Language.SINHALA) "ක්‍රියාකාරී සිට්ටු සීමාව උපරිමයි" else "Active Group Limit Reached",
                    fontWeight = FontWeight.Bold,
                    color = SittuvaRedAccent
                )
            },
            text = {
                Text(
                    text = SittuvaStrings.maxGroupsReached(language),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = { showMaxGroupLimitDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = SittuvaCrimson)
                ) {
                    Text(if (language == Language.SINHALA) "හරි" else "OK")
                }
            },
            containerColor = SittuvaDarkSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun AdminGroupCard(
    group: GroupEntity,
    language: Language,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalPeriodPayout = SittuvaCalculator.calculateTotalPayout(group.contributionAmount, group.participantLimit)
    val durationText = SittuvaCalculator.calculateDurationText(
        group.participantLimit,
        if (group.frequency == "WEEKLY") com.example.sittuva.model.Frequency.WEEKLY else com.example.sittuva.model.Frequency.MONTHLY,
        language
    )

    SittuvaGlowingCard(
        isGlowing = group.status == "ACTIVE",
        modifier = modifier.clickable { onClick() }.testTag("admin_group_card_${group.id}")
    ) {
        // Title & Status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
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

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = SittuvaDarkCardBorder)
        Spacer(modifier = Modifier.height(14.dp))

        // Grid Metrics
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = SittuvaGold
                )
            }

            Column {
                Text(
                    text = SittuvaStrings.contributionPerPerson(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
                Text(
                    text = SittuvaCalculator.formatCurrency(group.contributionAmount, language),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = SittuvaStrings.participants(language),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
                Text(
                    text = "${group.participantLimit}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Current Period & Recipient
        Surface(
            color = SittuvaDarkSurface,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.RotateRight, contentDescription = null, tint = SittuvaRedGlow, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${SittuvaStrings.currentPeriod(language)}: ${group.currentPeriod} / ${group.participantLimit}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Event, contentDescription = null, tint = SittuvaGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = SittuvaCalculator.formatDate(group.startDate, language),
                        style = MaterialTheme.typography.labelSmall,
                        color = SittuvaGoldLight
                    )
                }
            }
        }
    }
}
