package com.example.sittuva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sittuva.data.local.ErrorReportEntity
import com.example.sittuva.data.local.FeedbackEntity
import com.example.sittuva.model.Language
import com.example.sittuva.model.SittuvaCalculator
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.components.StatusBadge
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun FeedbackAndReportScreen(
    feedbacks: List<FeedbackEntity>,
    errorReports: List<ErrorReportEntity>,
    language: Language,
    isAdmin: Boolean,
    onSubmitFeedback: (String, String) -> Unit,
    onSubmitErrorReport: (String, String) -> Unit,
    onUpdateFeedbackStatus: (String, String) -> Unit,
    onUpdateReportStatus: (String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Ideas/Feedback, 1: Report Problem
    var titleInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }
    var showSubmittedMessage by remember { mutableStateOf(false) }

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
                        text = if (selectedTab == 0) SittuvaStrings.feedback(language) else SittuvaStrings.reportProblem(language),
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
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = SittuvaGold
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; showSubmittedMessage = false },
                    text = { Text(SittuvaStrings.feedback(language)) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; showSubmittedMessage = false },
                    text = { Text(SittuvaStrings.reportProblem(language)) }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Submission Form
                item {
                    SittuvaGlowingCard(isGlowing = true) {
                        Text(
                            text = if (selectedTab == 0)
                                (if (language == Language.SINHALA) "නව අදහසක් හෝ යෝජනාවක් යොමු කරන්න" else "Submit an Idea or Suggestion")
                            else
                                (if (language == Language.SINHALA) "ගැටලුවක් වාර්තා කරන්න" else "Report a Technical Problem"),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text(if (language == Language.SINHALA) "මාතෘකාව" else "Title") },
                            modifier = Modifier.fillMaxWidth().testTag("feedback_title_input"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = descInput,
                            onValueChange = { descInput = it },
                            label = { Text(if (language == Language.SINHALA) "විස්තරය" else "Description") },
                            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("feedback_desc_input"),
                            maxLines = 4
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (titleInput.isNotBlank() && descInput.isNotBlank()) {
                                    if (selectedTab == 0) {
                                        onSubmitFeedback(titleInput, descInput)
                                    } else {
                                        onSubmitErrorReport(titleInput, descInput)
                                    }
                                    titleInput = ""
                                    descInput = ""
                                    showSubmittedMessage = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("submit_feedback_button")
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (language == Language.SINHALA) "යොමු කරන්න" else "Submit", fontWeight = FontWeight.Bold)
                        }

                        if (showSubmittedMessage) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (language == Language.SINHALA) "සාර්ථකව යොමු කරන ලදී! ✓" else "Successfully submitted! ✓",
                                color = StatusSuccess,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Listing Submitted Items
                if (selectedTab == 0) {
                    item {
                        Text(
                            text = if (language == Language.SINHALA) "යොමු කළ අදහස් (${feedbacks.size})" else "Submitted Ideas (${feedbacks.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                    }

                    items(feedbacks) { fb ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SittuvaDarkSurface,
                            border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = fb.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    StatusBadge(status = fb.status, language = language)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = fb.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "By ${fb.userName} • ${SittuvaCalculator.formatDate(fb.createdAt, language)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )

                                // Admin Status Controls
                                if (isAdmin) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("NEW", "REVIEWING", "RESOLVED").forEach { st ->
                                            FilterChip(
                                                selected = fb.status == st,
                                                onClick = { onUpdateFeedbackStatus(fb.id, st) },
                                                label = { Text(st, style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = if (language == Language.SINHALA) "වාර්තා වූ ගැටලු (${errorReports.size})" else "Reported Problems (${errorReports.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                    }

                    items(errorReports) { err ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SittuvaDarkSurface,
                            border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = err.title,
                                        fontWeight = FontWeight.Bold,
                                        color = SittuvaRedAccent
                                    )
                                    StatusBadge(status = err.status, language = language)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = err.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.LightGray
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "From ${err.userName} (${err.userPhone}) • ${SittuvaCalculator.formatDate(err.createdAt, language)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )

                                if (isAdmin) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf("NEW", "REVIEWING", "RESOLVED").forEach { st ->
                                            FilterChip(
                                                selected = err.status == st,
                                                onClick = { onUpdateReportStatus(err.id, st) },
                                                label = { Text(st, style = MaterialTheme.typography.labelSmall) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
