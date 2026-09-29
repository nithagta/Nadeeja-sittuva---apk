package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sittuva.model.Language
import com.example.sittuva.ui.components.SittuvaHeader
import com.example.sittuva.ui.screens.*
import com.example.sittuva.ui.strings.SittuvaStrings
import com.example.sittuva.viewmodel.SittuvaViewModel
import com.example.ui.theme.SittuvaTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SittuvaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle Deep Links (sittuva://invite/{code} or https://sittuva.app/invite/{code})
        handleIntent(intent)

        setContent {
            SittuvaTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val allGroups by viewModel.allGroups.collectAsStateWithLifecycle()
                val activeGroups by viewModel.activeGroups.collectAsStateWithLifecycle()
                val selectedGroup by viewModel.selectedGroup.collectAsStateWithLifecycle()
                val selectedMembers by viewModel.selectedGroupMembers.collectAsStateWithLifecycle()
                val selectedPayments by viewModel.selectedGroupPayments.collectAsStateWithLifecycle()
                val selectedPayouts by viewModel.selectedGroupPayouts.collectAsStateWithLifecycle()
                val currentBankAccount by viewModel.currentBankAccount.collectAsStateWithLifecycle()
                val allFeedback by viewModel.allFeedback.collectAsStateWithLifecycle()
                val allErrorReports by viewModel.allErrorReports.collectAsStateWithLifecycle()
                val allAuditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()
                val allNotifications by viewModel.allNotifications.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                LaunchedEffect(uiState.userMessage, uiState.errorMessage) {
                    uiState.userMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                    }
                    uiState.errorMessage?.let {
                        snackbarHostState.showSnackbar(it)
                        viewModel.clearMessages()
                    }
                }

                // System BackHandler for sub-screens
                BackHandler(enabled = uiState.currentRoute != "ADMIN_DASHBOARD" && uiState.currentRoute != "MEMBER_DASHBOARD") {
                    val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                    viewModel.navigateTo(fallback)
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        if (uiState.currentRoute == "ADMIN_DASHBOARD" || uiState.currentRoute == "MEMBER_DASHBOARD") {
                            SittuvaHeader(
                                currentLanguage = uiState.language,
                                onLanguageToggle = { viewModel.setLanguage(it) },
                                isAdminMode = uiState.isAdminMode,
                                onRoleToggle = { viewModel.toggleRoleMode(it) }
                            )
                        }
                    },
                    bottomBar = {
                        if (uiState.currentRoute == "ADMIN_DASHBOARD" || uiState.currentRoute == "MEMBER_DASHBOARD") {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                windowInsets = WindowInsets.navigationBars
                            ) {
                                NavigationBarItem(
                                    selected = uiState.currentRoute == "ADMIN_DASHBOARD",
                                    onClick = { viewModel.navigateTo("ADMIN_DASHBOARD") },
                                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                                    label = { Text(SittuvaStrings.adminDashboard(uiState.language)) },
                                    modifier = Modifier.testTag("nav_admin_dashboard")
                                )
                                NavigationBarItem(
                                    selected = uiState.currentRoute == "MEMBER_DASHBOARD",
                                    onClick = { viewModel.navigateTo("MEMBER_DASHBOARD") },
                                    icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = null) },
                                    label = { Text(SittuvaStrings.memberDashboard(uiState.language)) },
                                    modifier = Modifier.testTag("nav_member_dashboard")
                                )
                                NavigationBarItem(
                                    selected = false,
                                    onClick = { viewModel.navigateTo("JOIN_GROUP") },
                                    icon = { Icon(Icons.Default.GroupAdd, contentDescription = null) },
                                    label = { Text(SittuvaStrings.joinGroup(uiState.language)) },
                                    modifier = Modifier.testTag("nav_join_group")
                                )
                                NavigationBarItem(
                                    selected = false,
                                    onClick = { viewModel.navigateTo("NOTIFICATIONS") },
                                    icon = {
                                        BadgedBox(
                                            badge = {
                                                val unread = allNotifications.count { !it.isRead }
                                                if (unread > 0) {
                                                    Badge { Text("$unread") }
                                                }
                                            }
                                        ) {
                                            Icon(Icons.Default.Notifications, contentDescription = null)
                                        }
                                    },
                                    label = { Text(SittuvaStrings.notifications(uiState.language)) },
                                    modifier = Modifier.testTag("nav_notifications")
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (uiState.currentRoute) {
                            "ADMIN_DASHBOARD" -> {
                                AdminDashboardScreen(
                                    groups = allGroups,
                                    language = uiState.language,
                                    onSelectGroup = { groupId ->
                                        viewModel.navigateTo("GROUP_DETAILS", groupId)
                                    },
                                    onRequestCreateGroup = {
                                        viewModel.navigateTo("CREATE_GROUP_WIZARD")
                                    },
                                    onOpenAuditLogs = { viewModel.navigateTo("AUDIT_LOGS") },
                                    onOpenFeedback = { viewModel.navigateTo("FEEDBACK_REPORTS") }
                                )
                            }

                            "MEMBER_DASHBOARD" -> {
                                val memberGroup = selectedGroup ?: activeGroups.firstOrNull()
                                val myMemberRecord = selectedMembers.find { it.userId == uiState.activeMemberUser.id }
                                    ?: selectedMembers.find { it.userId == "user_kamal" }

                                MemberDashboardScreen(
                                    currentMember = uiState.activeMemberUser,
                                    group = memberGroup,
                                    memberRecord = myMemberRecord,
                                    payments = selectedPayments,
                                    payouts = selectedPayouts,
                                    allMembers = selectedMembers,
                                    language = uiState.language,
                                    onPayNowClick = { payment ->
                                        viewModel.openPaymentDialog(payment)
                                    },
                                    onOpenBankAccount = {
                                        viewModel.navigateTo("BANK_ACCOUNT")
                                    },
                                    onJoinAnotherGroup = {
                                        viewModel.navigateTo("JOIN_GROUP")
                                    }
                                )
                            }

                            "GROUP_DETAILS" -> {
                                selectedGroup?.let { grp ->
                                    GroupDetailsScreen(
                                        group = grp,
                                        members = selectedMembers,
                                        payments = selectedPayments,
                                        payouts = selectedPayouts,
                                        language = uiState.language,
                                        isAdmin = uiState.isAdminMode,
                                        onBack = {
                                            val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                                            viewModel.navigateTo(fallback)
                                        },
                                        onDisbursePayout = { periodNumber ->
                                            viewModel.disbursePayout(grp.id, periodNumber)
                                        },
                                        onSimulatePayment = { payment ->
                                            viewModel.processPayment(payment.id, "Visa", "4242")
                                        },
                                        onSimulateFailedPayment = {},
                                        onShareInvite = {
                                            val sendIntent: Intent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "Sittuva Group Invitation! Join using code: ${grp.inviteCode} or deep link sittuva://invite/${grp.inviteCode}")
                                                type = "text/plain"
                                            }
                                            startActivity(Intent.createChooser(sendIntent, "Share Sittuva Invite"))
                                        }
                                    )
                                } ?: run {
                                    Text("Group not found", modifier = Modifier.padding(16.dp))
                                }
                            }

                            "CREATE_GROUP_WIZARD" -> {
                                GroupCreationWizardScreen(
                                    adminUser = uiState.currentUser,
                                    language = uiState.language,
                                    onGroupCreatedAndStarted = { name ->
                                        viewModel.createAndStartGroup(name)
                                    },
                                    onCancel = {
                                        viewModel.navigateTo("ADMIN_DASHBOARD")
                                    }
                                )
                            }

                            "JOIN_GROUP" -> {
                                val targetGroup = selectedGroup ?: activeGroups.firstOrNull()
                                JoinGroupScreen(
                                    group = targetGroup,
                                    currentMembersCount = selectedMembers.size,
                                    language = uiState.language,
                                    onJoinSuccess = { name, phone ->
                                        targetGroup?.let { grp ->
                                            viewModel.joinGroupViaInvite(grp.inviteCode, name, phone)
                                        }
                                    },
                                    onBack = {
                                        val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                                        viewModel.navigateTo(fallback)
                                    }
                                )
                            }

                            "BANK_ACCOUNT" -> {
                                BankAccountScreen(
                                    currentAccount = currentBankAccount,
                                    language = uiState.language,
                                    onSaveBankAccount = { bank, branch, acc, holder ->
                                        viewModel.saveBankAccount(bank, branch, acc, holder)
                                    },
                                    onBack = {
                                        viewModel.navigateTo("MEMBER_DASHBOARD")
                                    }
                                )
                            }

                            "FEEDBACK_REPORTS" -> {
                                FeedbackAndReportScreen(
                                    feedbacks = allFeedback,
                                    errorReports = allErrorReports,
                                    language = uiState.language,
                                    isAdmin = uiState.isAdminMode,
                                    onSubmitFeedback = { title, desc ->
                                        viewModel.submitFeedback(title, desc)
                                    },
                                    onSubmitErrorReport = { title, desc ->
                                        viewModel.submitErrorReport(title, desc)
                                    },
                                    onUpdateFeedbackStatus = { id, st ->
                                        viewModel.updateFeedbackStatus(id, st)
                                    },
                                    onUpdateReportStatus = { id, st ->
                                        viewModel.updateReportStatus(id, st)
                                    },
                                    onBack = {
                                        val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                                        viewModel.navigateTo(fallback)
                                    }
                                )
                            }

                            "AUDIT_LOGS" -> {
                                AuditLogScreen(
                                    auditLogs = allAuditLogs,
                                    language = uiState.language,
                                    onBack = {
                                        val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                                        viewModel.navigateTo(fallback)
                                    }
                                )
                            }

                            "NOTIFICATIONS" -> {
                                NotificationsScreen(
                                    notifications = allNotifications,
                                    language = uiState.language,
                                    onMarkAsRead = { id ->
                                        viewModel.markNotificationAsRead(id)
                                    },
                                    onBack = {
                                        val fallback = if (uiState.isAdminMode) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
                                        viewModel.navigateTo(fallback)
                                    }
                                )
                            }
                        }

                        // Payment Dialog
                        uiState.activePaymentForDialog?.let { payment ->
                            PaymentDialog(
                                payment = payment,
                                language = uiState.language,
                                onDismiss = { viewModel.dismissPaymentDialog() },
                                onPaymentSuccess = { brand, last4 ->
                                    viewModel.processPayment(payment.id, brand, last4)
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val data = intent?.data ?: return
        // Check for deep link scheme sittuva://invite/{code} or https://sittuva.app/invite/{code}
        if (data.scheme == "sittuva" && data.host == "invite") {
            val code = data.lastPathSegment
            if (!code.isNullOrEmpty()) {
                viewModel.navigateTo("JOIN_GROUP")
            }
        } else if (data.host == "sittuva.app" && data.path?.startsWith("/invite") == true) {
            val code = data.lastPathSegment
            if (!code.isNullOrEmpty()) {
                viewModel.navigateTo("JOIN_GROUP")
            }
        }
    }
}
