package com.example.sittuva.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
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
import com.example.sittuva.data.local.GroupMemberEntity
import com.example.sittuva.data.local.UserEntity
import com.example.sittuva.model.*
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun GroupCreationWizardScreen(
    adminUser: UserEntity,
    language: Language,
    onGroupCreatedAndStarted: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    var step by remember { mutableStateOf(1) } // 1 to 10
    var groupName by remember { mutableStateOf("සමෘද්ධි මිතුරු සිට්ටුව") }
    var participantCountStr by remember { mutableStateOf("5") }
    var contributionAmountStr by remember { mutableStateOf("2000") }
    var frequency by remember { mutableStateOf(Frequency.WEEKLY) }
    var selectedGracePeriod by remember { mutableStateOf(GracePeriodOption.THREE_DAYS) }
    var startDateMillis by remember { mutableStateOf(System.currentTimeMillis() + 86400000L) } // Starts tomorrow

    // Initial payout order state (Admin is #1 by default, then sample placeholder members)
    var membersList by remember {
        mutableStateOf(
            listOf(
                GroupMemberEntity(groupId = "", userId = adminUser.id, userName = "${adminUser.name} (Admin)", userPhone = adminUser.phone, payoutPosition = 1, isDefaultAdmin = true),
                GroupMemberEntity(groupId = "", userId = "u2", userName = "කමල් පෙරේරා", userPhone = "+94712345678", payoutPosition = 2),
                GroupMemberEntity(groupId = "", userId = "u3", userName = "අමල් ප්‍රනාන්දු", userPhone = "+94723456789", payoutPosition = 3),
                GroupMemberEntity(groupId = "", userId = "u4", userName = "සුනිල් ජයවර්ධන", userPhone = "+94763456780", payoutPosition = 4),
                GroupMemberEntity(groupId = "", userId = "u5", userName = "නිමල් සිරිසේන", userPhone = "+94783456781", payoutPosition = 5)
            )
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val participantsInt = participantCountStr.toIntOrNull() ?: 0
    val contributionLong = contributionAmountStr.toLongOrNull() ?: 0L
    val calculatedTotalPayout = SittuvaCalculator.calculateTotalPayout(contributionLong, participantsInt)
    val durationText = SittuvaCalculator.calculateDurationText(participantsInt, frequency, language)

    // Sync member list size when participant count changes
    LaunchedEffect(participantsInt) {
        if (participantsInt in 2..49 && membersList.size != participantsInt) {
            val updated = mutableListOf<GroupMemberEntity>()
            // Ensure Admin is always included
            updated.add(
                GroupMemberEntity(groupId = "", userId = adminUser.id, userName = "${adminUser.name} (Admin)", userPhone = adminUser.phone, payoutPosition = 1, isDefaultAdmin = true)
            )
            for (i in 2..participantsInt) {
                if (i - 1 < membersList.size && !membersList[i - 1].isDefaultAdmin) {
                    updated.add(membersList[i - 1].copy(payoutPosition = i))
                } else {
                    val defaultNames = listOf("කමල් පෙරේරා", "අමල් ප්‍රනාන්දු", "සුනිල් ජයවර්ධන", "නිමල් සිරිසේන", "රුවන් විජේසිංහ", "චමින්ද බණ්ඩාර", "දිනේෂ් කුමාර")
                    val name = defaultNames.getOrElse(i - 2) { "සාමාජිකයා $i" }
                    updated.add(
                        GroupMemberEntity(groupId = "", userId = "u$i", userName = name, userPhone = "+9477000000$i", payoutPosition = i)
                    )
                }
            }
            membersList = updated
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = { if (step > 1) step-- else onCancel() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                        Text(
                            text = if (language == Language.SINHALA) "සිට්ටු නිර්මාණය (පියවර $step/10)" else "Create Sittuva (Step $step/10)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        IconButton(onClick = onCancel) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel")
                        }
                    }
                    LinearProgressIndicator(
                        progress = { (step.toFloat() / 10f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = SittuvaRedGlow,
                        trackColor = SittuvaDarkCardBorder
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(if (language == Language.SINHALA) "ආපසු" else "Previous")
                        }
                    }

                    Button(
                        onClick = {
                            errorMessage = null
                            // Validation checks per step
                            when (step) {
                                1 -> {
                                    if (groupName.isBlank()) {
                                        errorMessage = if (language == Language.SINHALA) "කරුණාකර සමූහ නමක් ඇතුළත් කරන්න." else "Please enter a group name."
                                        return@Button
                                    }
                                    step++
                                }
                                2 -> {
                                    when (val v = SittuvaCalculator.validateParticipantCount(participantsInt)) {
                                        is ValidationResult.Invalid -> {
                                            errorMessage = if (language == Language.SINHALA) v.messageSi else v.messageEn
                                            return@Button
                                        }
                                        ValidationResult.Valid -> step++
                                    }
                                }
                                3 -> {
                                    when (val v = SittuvaCalculator.validateContributionAmount(contributionLong)) {
                                        is ValidationResult.Invalid -> {
                                            errorMessage = if (language == Language.SINHALA) v.messageSi else v.messageEn
                                            return@Button
                                        }
                                        ValidationResult.Valid -> step++
                                    }
                                }
                                in 4..8 -> step++
                                9 -> step = 10 // Proceed to Step 10: Start Group
                                10 -> {
                                    // Complete and Start Group
                                    onGroupCreatedAndStarted(groupName)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (step == 10) SittuvaRedGlow else SittuvaCrimson),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag(if (step == 10) "start_sittuva_group_button" else "wizard_next_button")
                    ) {
                        Text(
                            text = if (step == 10) SittuvaStrings.startSittuvaGroup(language)
                            else if (language == Language.SINHALA) "ඉදිරියට" else "Next",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
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
            // Error Message Banner
            if (errorMessage != null) {
                item {
                    Surface(
                        color = StatusFailedBg,
                        border = BorderStroke(1.dp, StatusFailed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.ErrorOutline, contentDescription = null, tint = StatusFailed)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = errorMessage ?: "",
                                color = StatusFailed,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Step Content
            when (step) {
                1 -> item {
                    // Step 1: Group Name
                    SittuvaGlowingCard(isGlowing = true) {
                        Text(
                            text = SittuvaStrings.wizardStep1Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "ඔබගේ සිට්ටු සමූහයට ගැලපෙන ගෞරවනීය නමක් යොදන්න." else "Enter a distinct title for this savings circle.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            label = { Text(if (language == Language.SINHALA) "සමූහයේ නම" else "Group Name") },
                            modifier = Modifier.fillMaxWidth().testTag("group_name_input"),
                            singleLine = true
                        )
                    }
                }

                2 -> item {
                    // Step 2: Participant Count (2 to 49)
                    SittuvaGlowingCard(isGlowing = true) {
                        Text(
                            text = SittuvaStrings.wizardStep2Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "පරිපාලක (Admin) ද එක් සාමාජිකයෙකු ලෙස ගණනය කෙරේ. අවම: 2, උපරිම: 49."
                            else "Admin is included as 1 participant. Total participants = Admin + Added Members. Allowed: 2 to 49.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = participantCountStr,
                            onValueChange = { participantCountStr = it.filter { char -> char.isDigit() } },
                            label = { Text(if (language == Language.SINHALA) "මුළු සාමාජිකයින් (Admin ඇතුළුව)" else "Total Participants (inc. Admin)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("participant_count_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Admin (1) + ${maxOf(0, participantsInt - 1)} members = $participantsInt total",
                            style = MaterialTheme.typography.labelSmall,
                            color = SittuvaGoldLight
                        )
                    }
                }

                3 -> item {
                    // Step 3: Contribution Amount
                    SittuvaGlowingCard(isGlowing = true) {
                        Text(
                            text = SittuvaStrings.wizardStep3Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "සෑම සාමාජිකයෙකුම එක් වටයකට ගෙවිය යුතු ස්ථාවර දායකත්ව මුදල ටයිප් කරන්න."
                            else "Enter custom fixed contribution amount per participant per period.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = contributionAmountStr,
                            onValueChange = { contributionAmountStr = it.filter { char -> char.isDigit() } },
                            label = { Text(if (language == Language.SINHALA) "දායකත්ව මුදල (රුපියල්)" else "Contribution Amount (LKR)") },
                            prefix = { Text("Rs. ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("contribution_amount_input"),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        // Display auto calculation clearly
                        Surface(
                            color = SittuvaCrimson.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = if (language == Language.SINHALA) "ගණනය කරන ලද මුළු ගෙවීම (Total Payout):" else "Calculated Period Payout:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.LightGray
                                )
                                Text(
                                    text = "${SittuvaCalculator.formatCurrency(contributionLong, language)} × $participantsInt = ${SittuvaCalculator.formatCurrency(calculatedTotalPayout, language)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SittuvaGold
                                )
                            }
                        }
                    }
                }

                4 -> item {
                    // Step 4: Frequency (Weekly / Monthly)
                    SittuvaGlowingCard {
                        Text(
                            text = SittuvaStrings.wizardStep4Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Weekly
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (frequency == Frequency.WEEKLY) SittuvaCrimson else SittuvaDarkSurface,
                                border = BorderStroke(1.dp, if (frequency == Frequency.WEEKLY) SittuvaRedGlow else SittuvaDarkCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { frequency = Frequency.WEEKLY }
                                    .testTag("frequency_weekly_option")
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null, tint = if (frequency == Frequency.WEEKLY) SittuvaGold else Color.Gray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = SittuvaStrings.weekly(language),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Monthly
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (frequency == Frequency.MONTHLY) SittuvaCrimson else SittuvaDarkSurface,
                                border = BorderStroke(1.dp, if (frequency == Frequency.MONTHLY) SittuvaRedGlow else SittuvaDarkCardBorder),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { frequency = Frequency.MONTHLY }
                                    .testTag("frequency_monthly_option")
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(imageVector = Icons.Default.CalendarMonth, contentDescription = null, tint = if (frequency == Frequency.MONTHLY) SittuvaGold else Color.Gray)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = SittuvaStrings.monthly(language),
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${SittuvaStrings.totalDuration(language)}: $durationText ($participantsInt payment periods)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGoldLight
                        )
                    }
                }

                5 -> item {
                    // Step 5: Payment Date
                    SittuvaGlowingCard {
                        Text(
                            text = SittuvaStrings.wizardStep5Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "පළමු වටයේ ගෙවීම් ආරම්භක දිනය තෝරන්න." else "Select group kick-off starting date.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SittuvaDarkSurface,
                            border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = if (language == Language.SINHALA) "ආරම්භක දිනය" else "Start Date",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = SittuvaCalculator.formatDate(startDateMillis, language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = SittuvaGold)
                            }
                        }
                    }
                }

                6 -> item {
                    // Step 6: Grace Period
                    SittuvaGlowingCard {
                        Text(
                            text = SittuvaStrings.wizardStep6Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "ගෙවීමක් ප්‍රමාද වුවහොත් ලබා දෙන සහන කාලය තෝරන්න." else "Allow participants time to resolve failed/pending contributions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            GracePeriodOption.values().forEach { option ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (selectedGracePeriod == option) SittuvaCrimson.copy(alpha = 0.35f) else Color.Transparent)
                                        .clickable { selectedGracePeriod = option }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedGracePeriod == option,
                                        onClick = { selectedGracePeriod = option }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (language == Language.SINHALA) option.labelSi else option.labelEn,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                7 -> item {
                    // Step 7: Invite Members
                    SittuvaGlowingCard {
                        Text(
                            text = SittuvaStrings.wizardStep7Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (language == Language.SINHALA) "සමූහයේ ඉඩ ප්‍රමාණය: $participantsInt (Admin ඇතුළුව)" else "Group capacity: $participantsInt (including Admin)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            color = SittuvaDarkSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (language == Language.SINHALA) "ආරාධනා ලින්ක් එක (Invitation Link):" else "Invitation Deep Link:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "sittuva://invite/SITTUVA${(1000..9999).random()}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SittuvaGoldLight
                                )
                            }
                        }
                    }
                }

                8 -> item {
                    // Step 8: Arrange Payout Order (Clean Reordering UI)
                    SittuvaGlowingCard {
                        Text(
                            text = SittuvaStrings.wizardStep8Title(language),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == Language.SINHALA) "පළමු වටයේ ගෙවීම පෙරනිමියෙන් Admin හට ලැබේ. ආරම්භ කිරීමට පෙර අනුපිළිවෙල මාරු කළ හැක."
                            else "Admin is scheduled as #1 by default. You can reorder payout priority before starting.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        membersList.forEachIndexed { index, member ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = SittuvaDarkSurface,
                                border = BorderStroke(1.dp, if (member.isDefaultAdmin) SittuvaGold else SittuvaDarkCardBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(if (index == 0) SittuvaRedGlow else SittuvaCrimson),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = member.userName,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                            Text(
                                                text = member.userPhone,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = Color.Gray
                                            )
                                        }
                                    }

                                    // Up/Down reordering arrows
                                    Row {
                                        IconButton(
                                            onClick = {
                                                if (index > 0) {
                                                    val reordered = membersList.toMutableList()
                                                    val temp = reordered[index]
                                                    reordered[index] = reordered[index - 1]
                                                    reordered[index - 1] = temp
                                                    membersList = reordered.mapIndexed { idx, m -> m.copy(payoutPosition = idx + 1) }
                                                }
                                            },
                                            enabled = index > 0
                                        ) {
                                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = if (index > 0) Color.White else Color.DarkGray)
                                        }
                                        IconButton(
                                            onClick = {
                                                if (index < membersList.size - 1) {
                                                    val reordered = membersList.toMutableList()
                                                    val temp = reordered[index]
                                                    reordered[index] = reordered[index + 1]
                                                    reordered[index + 1] = temp
                                                    membersList = reordered.mapIndexed { idx, m -> m.copy(payoutPosition = idx + 1) }
                                                }
                                            },
                                            enabled = index < membersList.size - 1
                                        ) {
                                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = if (index < membersList.size - 1) Color.White else Color.DarkGray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                9, 10 -> item {
                    // Step 9 & 10: Complete Summary Review & Start Button
                    SittuvaGlowingCard(isGlowing = true) {
                        Text(
                            text = SittuvaStrings.wizardStep9Title(language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SittuvaGold
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // Summary rows
                        SummaryRow("සමූහයේ නම / Group Name", groupName)
                        SummaryRow("මුළු සාමාජිකයින් / Participants", "$participantsInt (${SittuvaStrings.totalDuration(language)}: $durationText)")
                        SummaryRow("එක් අයෙකුගේ දායකත්වය / Contribution", SittuvaCalculator.formatCurrency(contributionLong, language))
                        SummaryRow("කාල පරතරය / Frequency", if (frequency == Frequency.WEEKLY) SittuvaStrings.weekly(language) else SittuvaStrings.monthly(language))
                        SummaryRow("ආරම්භක දිනය / Start Date", SittuvaCalculator.formatDate(startDateMillis, language))
                        SummaryRow("සහන කාලය / Grace Period", if (language == Language.SINHALA) selectedGracePeriod.labelSi else selectedGracePeriod.labelEn)
                        SummaryRow("පළමු ලාභියා / First Recipient", membersList.firstOrNull()?.userName ?: "Admin")

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = SittuvaDarkCardBorder)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Full Period Payout Highlight
                        Surface(
                            color = SittuvaCrimson,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = if (language == Language.SINHALA) "සෑම වටයකදීම ලාභියාට ලැබෙන මුදල:" else "Total Period Payout to Scheduled Recipient:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.White
                                )
                                Text(
                                    text = SittuvaCalculator.formatCurrency(calculatedTotalPayout, language),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = SittuvaGold
                                )
                                Text(
                                    text = "(${SittuvaCalculator.formatCurrency(contributionLong, language)} × $participantsInt = ${SittuvaCalculator.formatCurrency(calculatedTotalPayout, language)})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = SittuvaGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == Language.SINHALA) "සිට්ටුව ආරම්භ කළ පසු මූල්‍ය නීති සහ ගෙවීම් අනුපිළිවෙල ස්ථිරව අගුලු වැටේ."
                                else "Core financial rules and rotation order will be permanently locked upon starting.",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.LightGray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Text(text = value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White)
    }
}
