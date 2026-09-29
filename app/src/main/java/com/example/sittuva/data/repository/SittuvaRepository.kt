package com.example.sittuva.data.repository

import com.example.sittuva.data.local.*
import com.example.sittuva.model.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SittuvaRepository(private val dao: SittuvaDao) {

    // Streams
    val allGroupsFlow: Flow<List<GroupEntity>> = dao.getAllGroupsFlow()
    val activeGroupsFlow: Flow<List<GroupEntity>> = dao.getActiveGroupsFlow()
    val allFeedbackFlow: Flow<List<FeedbackEntity>> = dao.getAllFeedbackFlow()
    val allErrorReportsFlow: Flow<List<ErrorReportEntity>> = dao.getAllErrorReportsFlow()
    val allAuditLogsFlow: Flow<List<AuditLogEntity>> = dao.getAllAuditLogsFlow()
    val allNotificationsFlow: Flow<List<NotificationEntity>> = dao.getAllNotificationsFlow()

    fun getMembersForGroupFlow(groupId: String): Flow<List<GroupMemberEntity>> =
        dao.getMembersForGroupFlow(groupId)

    fun getPaymentsForGroupFlow(groupId: String): Flow<List<PaymentEntity>> =
        dao.getPaymentsForGroupFlow(groupId)

    fun getPayoutsForGroupFlow(groupId: String): Flow<List<PayoutEntity>> =
        dao.getPayoutsForGroupFlow(groupId)

    fun getGroupFlowById(groupId: String): Flow<GroupEntity?> =
        dao.getGroupFlowById(groupId)

    fun getBankAccountForUserFlow(userId: String): Flow<BankAccountEntity?> =
        dao.getBankAccountForUserFlow(userId)

    fun getSubscriptionForUserFlow(userId: String): Flow<PaymentSubscriptionEntity?> =
        dao.getSubscriptionForUserFlow(userId)

    // Seed Data Initialization
    suspend fun seedInitialDataIfEmpty() {
        val groups = dao.getActiveGroupsFlow()
        // Check if users already exist
        val existingAdmin = dao.getUserByPhone("+94771234567")
        if (existingAdmin != null) return

        // 1. Create Default Admin & Members
        val adminUser = UserEntity(
            id = "user_admin_nitish",
            name = "Nitish Nawagamuwa",
            phone = "+94771234567",
            role = "ADMIN"
        )
        val member1 = UserEntity(
            id = "user_kamal",
            name = "Kamal Perera",
            phone = "+94712345678",
            role = "MEMBER"
        )
        val member2 = UserEntity(
            id = "user_amal",
            name = "Amal Fernando",
            phone = "+94723456789",
            role = "MEMBER"
        )
        val member3 = UserEntity(
            id = "user_sunil",
            name = "Sunil Jayawardena",
            phone = "+94763456780",
            role = "MEMBER"
        )
        val member4 = UserEntity(
            id = "user_nimal",
            name = "Nimal Sirisena",
            phone = "+94783456781",
            role = "MEMBER"
        )

        listOf(adminUser, member1, member2, member3, member4).forEach { dao.insertUser(it) }

        // Bank Accounts
        dao.insertBankAccount(
            BankAccountEntity(
                userId = adminUser.id,
                bankName = "Bank of Ceylon (BOC)",
                branch = "Kollupitiya",
                accountNumberMask = "•••• •••• 4589",
                accountHolderName = "N. Nawagamuwa"
            )
        )
        dao.insertBankAccount(
            BankAccountEntity(
                userId = member1.id,
                bankName = "Commercial Bank of Ceylon",
                branch = "Nawala",
                accountNumberMask = "•••• •••• 1123",
                accountHolderName = "K. Perera"
            )
        )

        // Seed Active Group 1: 5 participants, Rs. 2,000 contribution, Weekly
        val groupId1 = "group_samruddhi_01"
        val contribution = 2000L
        val participantsCount = 5
        val now = System.currentTimeMillis()
        val oneWeekAgo = now - (7L * 24 * 60 * 60 * 1000)

        val activeGroup = GroupEntity(
            id = groupId1,
            name = "සමෘද්ධි මිතුරු සිට්ටුව (Samruddhi #1)",
            inviteCode = "SAMRUD01",
            adminId = adminUser.id,
            adminName = adminUser.name,
            adminPhone = adminUser.phone,
            participantLimit = participantsCount,
            contributionAmount = contribution,
            frequency = Frequency.WEEKLY.name,
            startDate = oneWeekAgo,
            gracePeriod = GracePeriodOption.THREE_DAYS.name,
            status = GroupStatus.ACTIVE.name,
            currentPeriod = 2,
            createdAt = oneWeekAgo - 86400000L,
            startedAt = oneWeekAgo
        )
        dao.insertGroup(activeGroup)

        // Group Members in Payout Order (1: Admin, 2: Kamal, 3: Amal, 4: Sunil, 5: Nimal)
        val memberEntities = listOf(
            GroupMemberEntity(groupId = groupId1, userId = adminUser.id, userName = adminUser.name, userPhone = adminUser.phone, payoutPosition = 1, isDefaultAdmin = true),
            GroupMemberEntity(groupId = groupId1, userId = member1.id, userName = member1.name, userPhone = member1.phone, payoutPosition = 2),
            GroupMemberEntity(groupId = groupId1, userId = member2.id, userName = member2.name, userPhone = member2.phone, payoutPosition = 3),
            GroupMemberEntity(groupId = groupId1, userId = member3.id, userName = member3.name, userPhone = member3.phone, payoutPosition = 4),
            GroupMemberEntity(groupId = groupId1, userId = member4.id, userName = member4.name, userPhone = member4.phone, payoutPosition = 5)
        )
        dao.insertGroupMembers(memberEntities)

        // Period 1 Payout: Admin received Rs. 10,000 (2,000 x 5)
        val poolAmount = SittuvaCalculator.calculateTotalPayout(contribution, participantsCount)
        dao.insertPayout(
            PayoutEntity(
                groupId = groupId1,
                recipientUserId = adminUser.id,
                recipientName = adminUser.name,
                periodNumber = 1,
                amount = poolAmount,
                status = PayoutStatus.COMPLETED.name,
                providerReference = "PAYOUT-LKR-BOC-99214",
                bankAccountMask = "BOC •••• 4589",
                createdAt = oneWeekAgo,
                completedAt = oneWeekAgo + 3600000L
            )
        )

        // Period 1 Payments (all paid)
        listOf(adminUser, member1, member2, member3, member4).forEachIndexed { idx, user ->
            dao.insertPayment(
                PaymentEntity(
                    groupId = groupId1,
                    userId = user.id,
                    userName = user.name,
                    periodNumber = 1,
                    amount = contribution,
                    status = PaymentStatus.PAID.name,
                    providerReference = "TXN-SEC-100$idx",
                    createdAt = oneWeekAgo,
                    paidAt = oneWeekAgo + (idx * 600000L)
                )
            )
        }

        // Period 2 (Current period: Kamal Perera scheduled for payout)
        dao.insertPayout(
            PayoutEntity(
                groupId = groupId1,
                recipientUserId = member1.id,
                recipientName = member1.name,
                periodNumber = 2,
                amount = poolAmount,
                status = PayoutStatus.SCHEDULED.name,
                bankAccountMask = "Commercial Bank •••• 1123",
                createdAt = now
            )
        )

        // Period 2 Payments (Admin and Kamal paid, Amal pending, Sunil pending, Nimal pending)
        dao.insertPayment(
            PaymentEntity(groupId = groupId1, userId = adminUser.id, userName = adminUser.name, periodNumber = 2, amount = contribution, status = PaymentStatus.PAID.name, providerReference = "TXN-SEC-2001", createdAt = now, paidAt = now)
        )
        dao.insertPayment(
            PaymentEntity(groupId = groupId1, userId = member1.id, userName = member1.name, periodNumber = 2, amount = contribution, status = PaymentStatus.PAID.name, providerReference = "TXN-SEC-2002", createdAt = now, paidAt = now)
        )
        dao.insertPayment(
            PaymentEntity(groupId = groupId1, userId = member2.id, userName = member2.name, periodNumber = 2, amount = contribution, status = PaymentStatus.PENDING.name, createdAt = now)
        )
        dao.insertPayment(
            PaymentEntity(groupId = groupId1, userId = member3.id, userName = member3.name, periodNumber = 2, amount = contribution, status = PaymentStatus.PENDING.name, createdAt = now)
        )
        dao.insertPayment(
            PaymentEntity(groupId = groupId1, userId = member4.id, userName = member4.name, periodNumber = 2, amount = contribution, status = PaymentStatus.PENDING.name, createdAt = now)
        )

        // Seed Audit Logs
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = adminUser.id,
                actorName = adminUser.name,
                action = "CREATE_GROUP",
                entityType = "GROUP",
                entityId = groupId1,
                newValue = "Group created with 5 participants, Rs. 2,000 weekly"
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = adminUser.id,
                actorName = adminUser.name,
                action = "START_GROUP",
                entityType = "GROUP",
                entityId = groupId1,
                newValue = "Group started. Financial rules locked."
            )
        )
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = "SYSTEM",
                actorName = "Sittuva Escrow Engine",
                action = "PAYOUT_COMPLETED",
                entityType = "PAYOUT",
                entityId = "PAYOUT-LKR-BOC-99214",
                newValue = "Disbursed Rs. 10,000 to Nitish Nawagamuwa"
            )
        )

        // Seed Notifications
        dao.insertNotification(
            NotificationEntity(
                userId = adminUser.id,
                title = "සිට්ටු වටය 1 සම්පූර්ණයි (Period 1 Completed)",
                body = "ඔබගේ බැංකු ගිණුමට රු. 10,000 මුදල සාර්ථකව බැර කරන ලදී.",
                type = "PAYOUT"
            )
        )
        dao.insertNotification(
            NotificationEntity(
                userId = adminUser.id,
                title = "සිට්ටු වටය 2 ආරම්භ විය (Period 2 Active)",
                body = "කමල් පෙරේරා හට රු. 10,000 ගෙවීම සඳහා දායකත්ව එකතු කිරීම ඇරඹිණි.",
                type = "DUE"
            )
        )
    }

    // Admin Group Creation with 3-Active Limit Rule
    suspend fun createGroup(
        adminUser: UserEntity,
        name: String,
        participantLimit: Int,
        contributionAmount: Long,
        frequency: Frequency,
        startDate: Long,
        gracePeriod: GracePeriodOption
    ): Result<GroupEntity> {
        // Enforce maximum 3 active groups
        val activeCount = dao.countActiveGroupsForAdmin(adminUser.id)
        if (activeCount >= SittuvaCalculator.MAX_ACTIVE_GROUPS_PER_ADMIN) {
            return Result.failure(
                IllegalStateException("You already have 3 active groups. Complete or close an existing group before creating another.")
            )
        }

        // Validate Participant Count (2 to 49)
        when (val validation = SittuvaCalculator.validateParticipantCount(participantLimit)) {
            is ValidationResult.Invalid -> return Result.failure(IllegalArgumentException(validation.messageEn))
            ValidationResult.Valid -> {}
        }

        // Validate Contribution Amount
        when (val validation = SittuvaCalculator.validateContributionAmount(contributionAmount)) {
            is ValidationResult.Invalid -> return Result.failure(IllegalArgumentException(validation.messageEn))
            ValidationResult.Valid -> {}
        }

        val groupId = UUID.randomUUID().toString()
        val group = GroupEntity(
            id = groupId,
            name = name,
            adminId = adminUser.id,
            adminName = adminUser.name,
            adminPhone = adminUser.phone,
            participantLimit = participantLimit,
            contributionAmount = contributionAmount,
            frequency = frequency.name,
            startDate = startDate,
            gracePeriod = gracePeriod.name,
            status = GroupStatus.SETUP.name,
            currentPeriod = 1
        )
        dao.insertGroup(group)

        // Automatically add Admin as Participant Number 1
        val adminMember = GroupMemberEntity(
            groupId = groupId,
            userId = adminUser.id,
            userName = adminUser.name,
            userPhone = adminUser.phone,
            payoutPosition = 1,
            isDefaultAdmin = true
        )
        dao.insertGroupMember(adminMember)

        // Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = adminUser.id,
                actorName = adminUser.name,
                action = "GROUP_CREATED",
                entityType = "GROUP",
                entityId = groupId,
                newValue = "Group '$name' created for $participantLimit participants at Rs. $contributionAmount"
            )
        )

        return Result.success(group)
    }

    // Join Group via Invitation with Capacity Check
    suspend fun joinGroupViaInvite(
        inviteCode: String,
        userName: String,
        phone: String
    ): Result<GroupMemberEntity> {
        val group = dao.getGroupByInviteCode(inviteCode)
            ?: return Result.failure(IllegalArgumentException("Invalid invitation link or code."))

        if (group.status != GroupStatus.SETUP.name && group.status != GroupStatus.ACTIVE.name) {
            return Result.failure(IllegalStateException("This Sittuva group is no longer accepting new members."))
        }

        val currentCount = dao.countMembersInGroup(group.id)
        if (currentCount >= group.participantLimit) {
            return Result.failure(
                IllegalStateException("This Sittuva group is full. Please ask the group Admin for another invitation.")
            )
        }

        // Find or create User
        var user = dao.getUserByPhone(phone)
        if (user == null) {
            user = UserEntity(name = userName, phone = phone, role = "MEMBER")
            dao.insertUser(user)
        }

        // Check if user already joined this group
        val existingMember = dao.getMemberByGroupAndUser(group.id, user.id)
        if (existingMember != null) {
            return Result.failure(IllegalStateException("You are already a member of this Sittuva group."))
        }

        // Assign next available payout position
        val nextPosition = currentCount + 1
        val newMember = GroupMemberEntity(
            groupId = group.id,
            userId = user.id,
            userName = userName,
            userPhone = phone,
            payoutPosition = nextPosition,
            isDefaultAdmin = false
        )
        dao.insertGroupMember(newMember)

        // Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = user.id,
                actorName = userName,
                action = "MEMBER_JOINED",
                entityType = "GROUP",
                entityId = group.id,
                newValue = "Joined at payout position #$nextPosition"
            )
        )

        // Notify Admin of new member
        dao.insertNotification(
            NotificationEntity(
                userId = group.adminId,
                title = "නව සාමාජිකයෙකු එක්විය (New Member Joined)",
                body = "$userName ($phone) successfully joined '${group.name}' at position #$nextPosition.",
                type = "JOINED"
            )
        )

        // If capacity reached, notify Admin: "Your Sittuva group is now full."
        if (nextPosition == group.participantLimit) {
            dao.insertNotification(
                NotificationEntity(
                    userId = group.adminId,
                    title = "ඔබගේ සිට්ටු සමූහය සම්පූර්ණයි (Group is Full)",
                    body = "Your Sittuva group '${group.name}' is now full with ${group.participantLimit} participants.",
                    type = "FULL"
                )
            )
        }

        return Result.success(newMember)
    }

    // Reorder Payout Order (Before Start Only)
    suspend fun reorderPayoutPositions(
        groupId: String,
        adminId: String,
        reorderedMembers: List<GroupMemberEntity>
    ): Result<Unit> {
        val group = dao.getGroupById(groupId) ?: return Result.failure(IllegalArgumentException("Group not found"))
        if (group.status != GroupStatus.SETUP.name) {
            return Result.failure(IllegalStateException("Cannot change payout order once the group has started. Financial rules are locked."))
        }
        if (group.adminId != adminId) {
            return Result.failure(SecurityException("Only group Admin can reorder payout positions."))
        }

        reorderedMembers.forEachIndexed { index, member ->
            val updated = member.copy(payoutPosition = index + 1)
            dao.updateGroupMember(updated)
        }

        dao.insertAuditLog(
            AuditLogEntity(
                actorId = adminId,
                actorName = group.adminName,
                action = "PAYOUT_ORDER_CHANGED",
                entityType = "GROUP",
                entityId = groupId,
                newValue = "Payout order reordered before start"
            )
        )
        return Result.success(Unit)
    }

    // Start Group Officially (LOCKS all financial rules)
    suspend fun startGroup(groupId: String, adminId: String): Result<Unit> {
        val group = dao.getGroupById(groupId) ?: return Result.failure(IllegalArgumentException("Group not found"))
        if (group.adminId != adminId) {
            return Result.failure(SecurityException("Only Admin can start the group."))
        }
        if (group.status != GroupStatus.SETUP.name) {
            return Result.failure(IllegalStateException("Group is already active or completed."))
        }

        val members = dao.getMembersForGroup(groupId)
        if (members.size < 2) {
            return Result.failure(IllegalStateException("At least 2 participants are required to start the group."))
        }

        // Lock group & set ACTIVE
        val now = System.currentTimeMillis()
        val updatedGroup = group.copy(
            status = GroupStatus.ACTIVE.name,
            participantLimit = members.size, // Final locked participant count
            startedAt = now,
            currentPeriod = 1
        )
        dao.updateGroup(updatedGroup)

        val totalPayout = SittuvaCalculator.calculateTotalPayout(group.contributionAmount, members.size)

        // Generate Payout Schedule for each period
        members.forEach { member ->
            val payout = PayoutEntity(
                groupId = groupId,
                recipientUserId = member.userId,
                recipientName = member.userName,
                periodNumber = member.payoutPosition,
                amount = totalPayout,
                status = if (member.payoutPosition == 1) PayoutStatus.SCHEDULED.name else PayoutStatus.SCHEDULED.name
            )
            dao.insertPayout(payout)
        }

        // Generate Period 1 Payments for all members
        members.forEach { member ->
            dao.insertPayment(
                PaymentEntity(
                    groupId = groupId,
                    userId = member.userId,
                    userName = member.userName,
                    periodNumber = 1,
                    amount = group.contributionAmount,
                    status = PaymentStatus.PENDING.name
                )
            )
        }

        // Immutable Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = adminId,
                actorName = group.adminName,
                action = "START_GROUP_LOCKED",
                entityType = "GROUP",
                entityId = groupId,
                newValue = "Group officially started with ${members.size} members. Core financial rules permanently locked."
            )
        )

        // Notify all members
        members.forEach { m ->
            dao.insertNotification(
                NotificationEntity(
                    userId = m.userId,
                    title = "සිට්ටු සමූහය ආරම්භ විය! (${group.name})",
                    body = "සිට්ටුව ආරම්භ කර ඇත. ඔබගේ ගෙවීම් අනුපිළිවෙල #${m.payoutPosition} වේ.",
                    type = "SUCCESS"
                )
            )
        }

        return Result.success(Unit)
    }

    // Process Verified Member Payment (Tokenized Card)
    suspend fun processPayment(
        paymentId: String,
        cardBrand: String,
        cardLast4: String,
        actorId: String
    ): Result<PaymentEntity> {
        val payment = dao.getPaymentById(paymentId)
            ?: return Result.failure(IllegalArgumentException("Payment not found"))

        if (payment.status == PaymentStatus.PAID.name) {
            return Result.failure(IllegalStateException("Payment is already completed."))
        }

        val providerRef = "TXN-PCI-${UUID.randomUUID().toString().take(12).uppercase()}"
        val now = System.currentTimeMillis()
        val updated = payment.copy(
            status = PaymentStatus.PAID.name,
            providerReference = providerRef,
            paidAt = now
        )
        dao.updatePayment(updated)

        // Store subscription / tokenized card reference
        dao.insertSubscription(
            PaymentSubscriptionEntity(
                groupId = payment.groupId,
                userId = payment.userId,
                providerToken = "TOK-PCI-${UUID.randomUUID()}",
                cardMask = "•••• $cardLast4",
                cardBrand = cardBrand
            )
        )

        // Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = actorId,
                actorName = payment.userName,
                action = "PAYMENT_VERIFIED",
                entityType = "PAYMENT",
                entityId = paymentId,
                newValue = "Paid Rs. ${payment.amount} for Period #${payment.periodNumber} ($cardBrand •••• $cardLast4, Ref: $providerRef)"
            )
        )

        dao.insertNotification(
            NotificationEntity(
                userId = payment.userId,
                title = "ගෙවීම සාර්ථකයි (Payment Successful)",
                body = "රු. ${payment.amount} මුදල සාර්ථකව ගෙවන ලදී. Reference: $providerRef",
                type = "SUCCESS"
            )
        )

        return Result.success(updated)
    }

    // Disburse Payout to Scheduled Recipient
    suspend fun disbursePayout(groupId: String, periodNumber: Int, actorId: String): Result<PayoutEntity> {
        val group = dao.getGroupById(groupId) ?: return Result.failure(IllegalArgumentException("Group not found"))
        val payout = dao.getPayoutForPeriod(groupId, periodNumber)
            ?: return Result.failure(IllegalArgumentException("Payout record not found for period $periodNumber"))

        if (payout.status == PayoutStatus.COMPLETED.name) {
            return Result.failure(IllegalStateException("Payout for period $periodNumber is already disbursed."))
        }

        // Verify that all member payments for this period are PAID
        val periodPayments = dao.getPaymentsForPeriod(groupId, periodNumber)
        val unpaidCount = periodPayments.count { it.status != PaymentStatus.PAID.name }
        if (unpaidCount > 0) {
            return Result.failure(IllegalStateException("Cannot disburse payout: $unpaidCount member payment(s) are still pending or failed."))
        }

        val bankAccount = dao.getBankAccountForUser(payout.recipientUserId)
        val bankMask = bankAccount?.let { "${it.bankName} (${it.accountNumberMask})" } ?: "Bank Transfer Account"

        val providerRef = "PAYOUT-LKR-${UUID.randomUUID().toString().take(10).uppercase()}"
        val now = System.currentTimeMillis()
        val completedPayout = payout.copy(
            status = PayoutStatus.COMPLETED.name,
            providerReference = providerRef,
            bankAccountMask = bankMask,
            completedAt = now
        )
        dao.updatePayout(completedPayout)

        // Audit Log
        dao.insertAuditLog(
            AuditLogEntity(
                actorId = actorId,
                actorName = group.adminName,
                action = "PAYOUT_DISBURSED",
                entityType = "PAYOUT",
                entityId = payout.id,
                newValue = "Full period pool Rs. ${payout.amount} disbursed to ${payout.recipientName} ($bankMask, Ref: $providerRef)"
            )
        )

        dao.insertNotification(
            NotificationEntity(
                userId = payout.recipientUserId,
                title = "සිට්ටු මුදල් ඔබගේ ගිණුමට බැරවිය! (Payout Received)",
                body = "වටය #$periodNumber හි සම්පූර්ණ එකතුව රු. ${payout.amount} ඔබගේ $bankMask ගිණුමට බැර කර ඇත.",
                type = "PAYOUT"
            )
        )

        // Advance to next period or complete group if this was the last period
        val members = dao.getMembersForGroup(groupId)
        if (periodNumber >= members.size) {
            // Group Completed!
            val completedGroup = group.copy(
                status = GroupStatus.COMPLETED.name,
                completedAt = now
            )
            dao.updateGroup(completedGroup)

            dao.insertAuditLog(
                AuditLogEntity(
                    actorId = "SYSTEM",
                    actorName = "Sittuva Escrow Engine",
                    action = "GROUP_COMPLETED",
                    entityType = "GROUP",
                    entityId = groupId,
                    newValue = "All ${members.size} periods completed successfully. Group marked COMPLETED."
                )
            )

            members.forEach { m ->
                dao.insertNotification(
                    NotificationEntity(
                        userId = m.userId,
                        title = "සිට්ටු සමූහය සාර්ථකව නිමාවිය! (Group Completed)",
                        body = "'${group.name}' හි සියලු දෙනාටම නියමිත දායකත්ව සහ ගෙවීම් සාර්ථකව අවසන් විය.",
                        type = "COMPLETED"
                    )
                )
            }
        } else {
            // Advance to next period
            val nextPeriod = periodNumber + 1
            val updatedGroup = group.copy(currentPeriod = nextPeriod)
            dao.updateGroup(updatedGroup)

            // Generate payments for next period
            members.forEach { member ->
                dao.insertPayment(
                    PaymentEntity(
                        groupId = groupId,
                        userId = member.userId,
                        userName = member.userName,
                        periodNumber = nextPeriod,
                        amount = group.contributionAmount,
                        status = PaymentStatus.PENDING.name
                    )
                )
            }
        }

        return Result.success(completedPayout)
    }

    // Bank Account Registration
    suspend fun saveBankAccount(
        userId: String,
        bankName: String,
        branch: String,
        accountNumber: String,
        accountHolderName: String
    ) {
        val last4 = if (accountNumber.length > 4) accountNumber.takeLast(4) else accountNumber
        val mask = "•••• •••• $last4"
        dao.insertBankAccount(
            BankAccountEntity(
                userId = userId,
                bankName = bankName,
                branch = branch,
                accountNumberMask = mask,
                accountHolderName = accountHolderName
            )
        )
    }

    // Feedback Submission
    suspend fun submitFeedback(userId: String, userName: String, groupId: String?, groupName: String?, title: String, description: String) {
        dao.insertFeedback(
            FeedbackEntity(
                userId = userId,
                userName = userName,
                groupId = groupId,
                groupName = groupName,
                title = title,
                description = description
            )
        )
    }

    suspend fun updateFeedbackStatus(id: String, status: String) {
        dao.updateFeedbackStatus(id, status)
    }

    // Error Report Submission
    suspend fun submitErrorReport(userId: String, userName: String, userPhone: String, groupId: String?, groupName: String?, title: String, description: String) {
        dao.insertErrorReport(
            ErrorReportEntity(
                userId = userId,
                userName = userName,
                userPhone = userPhone,
                groupId = groupId,
                groupName = groupName,
                title = title,
                description = description
            )
        )
    }

    suspend fun updateErrorReportStatus(id: String, status: String) {
        dao.updateErrorReportStatus(id, status)
    }

    suspend fun markNotificationAsRead(id: String) {
        dao.markNotificationAsRead(id)
    }
}
