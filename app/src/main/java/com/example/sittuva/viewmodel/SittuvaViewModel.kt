package com.example.sittuva.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sittuva.data.local.*
import com.example.sittuva.data.repository.SittuvaRepository
import com.example.sittuva.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SittuvaUiState(
    val language: Language = Language.SINHALA,
    val isAdminMode: Boolean = true,
    val currentRoute: String = "ADMIN_DASHBOARD",
    val selectedGroupId: String? = null,
    val currentUser: UserEntity = UserEntity(
        id = "user_admin_nitish",
        name = "Nitish Nawagamuwa",
        phone = "+94771234567",
        role = "ADMIN"
    ),
    val activeMemberUser: UserEntity = UserEntity(
        id = "user_kamal",
        name = "Kamal Perera",
        phone = "+94712345678",
        role = "MEMBER"
    ),
    val activePaymentForDialog: PaymentEntity? = null,
    val userMessage: String? = null,
    val errorMessage: String? = null
)

class SittuvaViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SittuvaDatabase.getInstance(application)
    private val repository = SittuvaRepository(database.sittuvaDao())

    private val _uiState = MutableStateFlow(SittuvaUiState())
    val uiState: StateFlow<SittuvaUiState> = _uiState.asStateFlow()

    val allGroups: StateFlow<List<GroupEntity>> = repository.allGroupsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeGroups: StateFlow<List<GroupEntity>> = repository.activeGroupsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allFeedback: StateFlow<List<FeedbackEntity>> = repository.allFeedbackFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allErrorReports: StateFlow<List<ErrorReportEntity>> = repository.allErrorReportsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allNotifications: StateFlow<List<NotificationEntity>> = repository.allNotificationsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic Selected Group Flow
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedGroup: StateFlow<GroupEntity?> = _uiState
        .map { it.selectedGroupId }
        .flatMapLatest { groupId ->
            if (groupId != null) repository.getGroupFlowById(groupId) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedGroupMembers: StateFlow<List<GroupMemberEntity>> = _uiState
        .map { it.selectedGroupId }
        .flatMapLatest { groupId ->
            if (groupId != null) repository.getMembersForGroupFlow(groupId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedGroupPayments: StateFlow<List<PaymentEntity>> = _uiState
        .map { it.selectedGroupId }
        .flatMapLatest { groupId ->
            if (groupId != null) repository.getPaymentsForGroupFlow(groupId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val selectedGroupPayouts: StateFlow<List<PayoutEntity>> = _uiState
        .map { it.selectedGroupId }
        .flatMapLatest { groupId ->
            if (groupId != null) repository.getPayoutsForGroupFlow(groupId) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentBankAccount: StateFlow<BankAccountEntity?> = _uiState
        .flatMapLatest { state ->
            val user = if (state.isAdminMode) state.currentUser else state.activeMemberUser
            repository.getBankAccountForUserFlow(user.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
            // Set initial selected group if active
            allGroups.collect { groups ->
                if (_uiState.value.selectedGroupId == null && groups.isNotEmpty()) {
                    _uiState.update { it.copy(selectedGroupId = groups.first().id) }
                }
            }
        }
    }

    fun setLanguage(lang: Language) {
        _uiState.update { it.copy(language = lang) }
    }

    fun toggleRoleMode(isAdmin: Boolean) {
        _uiState.update {
            it.copy(
                isAdminMode = isAdmin,
                currentRoute = if (isAdmin) "ADMIN_DASHBOARD" else "MEMBER_DASHBOARD"
            )
        }
    }

    fun navigateTo(route: String, groupId: String? = null) {
        _uiState.update {
            it.copy(
                currentRoute = route,
                selectedGroupId = groupId ?: it.selectedGroupId,
                errorMessage = null,
                userMessage = null
            )
        }
    }

    fun createAndStartGroup(
        name: String,
        participantLimit: Int = 5,
        contributionAmount: Long = 2000L,
        frequency: Frequency = Frequency.WEEKLY,
        gracePeriod: GracePeriodOption = GracePeriodOption.THREE_DAYS
    ) {
        viewModelScope.launch {
            val admin = _uiState.value.currentUser
            val result = repository.createGroup(
                adminUser = admin,
                name = name,
                participantLimit = participantLimit,
                contributionAmount = contributionAmount,
                frequency = frequency,
                startDate = System.currentTimeMillis() + 86400000L,
                gracePeriod = gracePeriod
            )

            result.fold(
                onSuccess = { group ->
                    // Add remaining sample participants to fill group and immediately start
                    val defaultNames = listOf("කමල් පෙරේරා", "අමල් ප්‍රනාන්දු", "සුනිල් ජයවර්ධන", "නිමල් සිරිසේන", "රුවන් විජේසිංහ")
                    for (i in 2..participantLimit) {
                        val memberName = defaultNames.getOrElse(i - 2) { "සාමාජිකයා $i" }
                        val memberPhone = "+9477000000$i"
                        repository.joinGroupViaInvite(group.inviteCode, memberName, memberPhone)
                    }

                    // Start group officially
                    repository.startGroup(group.id, admin.id)

                    _uiState.update {
                        it.copy(
                            selectedGroupId = group.id,
                            currentRoute = "GROUP_DETAILS",
                            userMessage = "Group '${group.name}' created and started successfully!"
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            )
        }
    }

    fun joinGroupViaInvite(inviteCode: String, name: String, phone: String) {
        viewModelScope.launch {
            val result = repository.joinGroupViaInvite(inviteCode, name, phone)
            result.fold(
                onSuccess = { member ->
                    _uiState.update {
                        it.copy(
                            selectedGroupId = member.groupId,
                            currentRoute = "MEMBER_DASHBOARD",
                            activeMemberUser = UserEntity(id = member.userId, name = name, phone = phone, role = "MEMBER"),
                            isAdminMode = false,
                            userMessage = "Successfully joined at payout position #${member.payoutPosition}!"
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            )
        }
    }

    fun openPaymentDialog(payment: PaymentEntity) {
        _uiState.update { it.copy(activePaymentForDialog = payment) }
    }

    fun dismissPaymentDialog() {
        _uiState.update { it.copy(activePaymentForDialog = null) }
    }

    fun processPayment(paymentId: String, cardBrand: String, cardLast4: String) {
        viewModelScope.launch {
            val user = if (_uiState.value.isAdminMode) _uiState.value.currentUser else _uiState.value.activeMemberUser
            val result = repository.processPayment(paymentId, cardBrand, cardLast4, user.id)
            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(activePaymentForDialog = null, userMessage = "Payment successful!") }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            )
        }
    }

    fun disbursePayout(groupId: String, periodNumber: Int) {
        viewModelScope.launch {
            val admin = _uiState.value.currentUser
            val result = repository.disbursePayout(groupId, periodNumber, admin.id)
            result.fold(
                onSuccess = { payout ->
                    _uiState.update { it.copy(userMessage = "Payout of Rs. ${payout.amount} disbursed to ${payout.recipientName}!") }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(errorMessage = err.message) }
                }
            )
        }
    }

    fun saveBankAccount(bankName: String, branch: String, accountNumber: String, accountHolderName: String) {
        viewModelScope.launch {
            val user = if (_uiState.value.isAdminMode) _uiState.value.currentUser else _uiState.value.activeMemberUser
            repository.saveBankAccount(user.id, bankName, branch, accountNumber, accountHolderName)
            _uiState.update { it.copy(userMessage = "Bank account updated!") }
        }
    }

    fun submitFeedback(title: String, desc: String) {
        viewModelScope.launch {
            val user = if (_uiState.value.isAdminMode) _uiState.value.currentUser else _uiState.value.activeMemberUser
            val group = selectedGroup.value
            repository.submitFeedback(user.id, user.name, group?.id, group?.name, title, desc)
        }
    }

    fun submitErrorReport(title: String, desc: String) {
        viewModelScope.launch {
            val user = if (_uiState.value.isAdminMode) _uiState.value.currentUser else _uiState.value.activeMemberUser
            val group = selectedGroup.value
            repository.submitErrorReport(user.id, user.name, user.phone, group?.id, group?.name, title, desc)
        }
    }

    fun updateFeedbackStatus(id: String, status: String) {
        viewModelScope.launch { repository.updateFeedbackStatus(id, status) }
    }

    fun updateReportStatus(id: String, status: String) {
        viewModelScope.launch { repository.updateErrorReportStatus(id, status) }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch { repository.markNotificationAsRead(id) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, userMessage = null) }
    }
}
