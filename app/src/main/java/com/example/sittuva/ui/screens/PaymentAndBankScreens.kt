package com.example.sittuva.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.sittuva.data.local.BankAccountEntity
import com.example.sittuva.data.local.PaymentEntity
import com.example.sittuva.model.Language
import com.example.sittuva.model.SittuvaCalculator
import com.example.sittuva.ui.components.SittuvaGlowingCard
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.ui.theme.*

@Composable
fun PaymentDialog(
    payment: PaymentEntity,
    language: Language,
    onDismiss: () -> Unit,
    onPaymentSuccess: (String, String) -> Unit // cardBrand, cardLast4
) {
    var selectedCardBrand by remember { mutableStateOf("Visa") }
    var cardNumber by remember { mutableStateOf("4242 •••• •••• 4242") }
    var expiryDate by remember { mutableStateOf("12/28") }
    var cvv by remember { mutableStateOf("•••") }
    var isProcessing by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SittuvaGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = SittuvaStrings.securePayment(language),
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = SittuvaCrimson.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SittuvaDarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Period #${payment.periodNumber} Contribution",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                        Text(
                            text = SittuvaCalculator.formatCurrency(payment.amount, language),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = SittuvaGold
                        )
                    }
                }

                // Card Brand Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Visa", "Mastercard").forEach { brand ->
                        FilterChip(
                            selected = selectedCardBrand == brand,
                            onClick = { selectedCardBrand = brand },
                            label = { Text(brand, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = SittuvaCrimson,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = cardNumber,
                    onValueChange = { cardNumber = it },
                    label = { Text("Card Number (PCI Tokenized)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    readOnly = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = expiryDate,
                        onValueChange = { expiryDate = it },
                        label = { Text("MM/YY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        readOnly = true
                    )
                    OutlinedTextField(
                        value = cvv,
                        onValueChange = { cvv = it },
                        label = { Text("CVV") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        readOnly = true
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == Language.SINHALA) "256-bit සංකේතනය කළ සත්‍යාපිත ගෙවීමක්" else "256-bit Encrypted Tokenized Checkout",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                }

                if (isProcessing) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = SittuvaRedGlow
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProcessing = true
                    onPaymentSuccess(selectedCardBrand, "4242")
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_payment_dialog_button")
            ) {
                Text(
                    text = if (isProcessing) "සැකසෙමින්..." else "${SittuvaStrings.payNow(language)} (${SittuvaCalculator.formatCurrency(payment.amount, language)})",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text(if (language == Language.SINHALA) "අවලංගු කරන්න" else "Cancel")
            }
        },
        containerColor = SittuvaDarkSurface,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun BankAccountScreen(
    currentAccount: BankAccountEntity?,
    language: Language,
    onSaveBankAccount: (String, String, String, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val banks = listOf(
        "Bank of Ceylon (BOC)",
        "Commercial Bank of Ceylon",
        "People's Bank",
        "Sampath Bank",
        "Hatton National Bank (HNB)",
        "Seylan Bank",
        "Nations Trust Bank (NTB)",
        "DFCC Bank"
    )

    var selectedBank by remember { mutableStateOf(currentAccount?.bankName ?: banks.first()) }
    var branch by remember { mutableStateOf(currentAccount?.branch ?: "Colombo Main") }
    var accountNumber by remember { mutableStateOf(currentAccount?.accountNumberMask?.replace("•", "8") ?: "8001234567") }
    var accountHolderName by remember { mutableStateOf(currentAccount?.accountHolderName ?: "N. Nawagamuwa") }
    var isSavedNotification by remember { mutableStateOf(false) }

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
                        text = SittuvaStrings.bankAccount(language),
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
            SittuvaGlowingCard(isGlowing = true) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AccountBalance, contentDescription = null, tint = SittuvaGold, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (language == Language.SINHALA) "සිට්ටු මුදල් බැරවන බැංකු ගිණුම" else "Payout Bank Account",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (language == Language.SINHALA) "ඔබේ වටය පැමිණි විට සම්පූර්ණ එකතුව මෙයට බැරවේ." else "Full period pool will be deposited to this account when scheduled.",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.LightGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bank Selection dropdown simulation
                Text(
                    text = if (language == Language.SINHALA) "බැංකුව තෝරන්න" else "Select Bank",
                    style = MaterialTheme.typography.labelMedium,
                    color = SittuvaGoldLight
                )
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    banks.take(4).forEach { b ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedBank == b) SittuvaCrimson else SittuvaDarkSurface,
                            border = BorderStroke(1.dp, if (selectedBank == b) SittuvaRedGlow else SittuvaDarkCardBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedBank = b }
                                .padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = b,
                                color = Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (selectedBank == b) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = branch,
                    onValueChange = { branch = it },
                    label = { Text(if (language == Language.SINHALA) "ශාඛාව" else "Branch") },
                    modifier = Modifier.fillMaxWidth().testTag("bank_branch_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text(if (language == Language.SINHALA) "ගිණුම් අංකය" else "Account Number") },
                    modifier = Modifier.fillMaxWidth().testTag("bank_account_input"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = accountHolderName,
                    onValueChange = { accountHolderName = it },
                    label = { Text(if (language == Language.SINHALA) "ගිණුම් හිමියාගේ නම" else "Account Holder Name") },
                    modifier = Modifier.fillMaxWidth().testTag("bank_holder_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onSaveBankAccount(selectedBank, branch, accountNumber, accountHolderName)
                        isSavedNotification = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SittuvaRedGlow),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_bank_account_button")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == Language.SINHALA) "බැංකු තොරතුරු සුරකින්න" else "Save Bank Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (isSavedNotification) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == Language.SINHALA) "බැංකු තොරතුරු සාර්ථකව යාවත්කාලීන විය! ✓" else "Bank account saved successfully! ✓",
                        color = StatusSuccess,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
