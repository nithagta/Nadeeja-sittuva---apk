package com.example.sittuva.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SittuvaDao {

    // Users
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE phone = :phone LIMIT 1")
    suspend fun getUserByPhone(phone: String): UserEntity?

    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    // Groups
    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    fun getAllGroupsFlow(): Flow<List<GroupEntity>>

    @Query("SELECT * FROM groups WHERE status = 'ACTIVE' ORDER BY createdAt DESC")
    fun getActiveGroupsFlow(): Flow<List<GroupEntity>>

    @Query("SELECT COUNT(*) FROM groups WHERE adminId = :adminId AND status = 'ACTIVE'")
    suspend fun countActiveGroupsForAdmin(adminId: String): Int

    @Query("SELECT * FROM groups WHERE id = :groupId LIMIT 1")
    suspend fun getGroupById(groupId: String): GroupEntity?

    @Query("SELECT * FROM groups WHERE id = :groupId LIMIT 1")
    fun getGroupFlowById(groupId: String): Flow<GroupEntity?>

    @Query("SELECT * FROM groups WHERE inviteCode = :inviteCode LIMIT 1")
    suspend fun getGroupByInviteCode(inviteCode: String): GroupEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: GroupEntity)

    @Update
    suspend fun updateGroup(group: GroupEntity)

    // Group Members
    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND status = 'ACTIVE' ORDER BY payoutPosition ASC")
    fun getMembersForGroupFlow(groupId: String): Flow<List<GroupMemberEntity>>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND status = 'ACTIVE' ORDER BY payoutPosition ASC")
    suspend fun getMembersForGroup(groupId: String): List<GroupMemberEntity>

    @Query("SELECT * FROM group_members WHERE groupId = :groupId AND userId = :userId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getMemberByGroupAndUser(groupId: String, userId: String): GroupMemberEntity?

    @Query("SELECT COUNT(*) FROM group_members WHERE groupId = :groupId AND status = 'ACTIVE'")
    suspend fun countMembersInGroup(groupId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMember(member: GroupMemberEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupMembers(members: List<GroupMemberEntity>)

    @Update
    suspend fun updateGroupMember(member: GroupMemberEntity)

    @Query("DELETE FROM group_members WHERE groupId = :groupId AND userId = :userId")
    suspend fun deleteMember(groupId: String, userId: String)

    // Payments
    @Query("SELECT * FROM payments WHERE groupId = :groupId ORDER BY periodNumber ASC, createdAt DESC")
    fun getPaymentsForGroupFlow(groupId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsForUserFlow(userId: String): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE groupId = :groupId AND periodNumber = :periodNumber")
    suspend fun getPaymentsForPeriod(groupId: String, periodNumber: Int): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE id = :paymentId LIMIT 1")
    suspend fun getPaymentById(paymentId: String): PaymentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    // Payouts
    @Query("SELECT * FROM payouts WHERE groupId = :groupId ORDER BY periodNumber ASC")
    fun getPayoutsForGroupFlow(groupId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE recipientUserId = :userId ORDER BY createdAt DESC")
    fun getPayoutsForUserFlow(userId: String): Flow<List<PayoutEntity>>

    @Query("SELECT * FROM payouts WHERE groupId = :groupId AND periodNumber = :periodNumber LIMIT 1")
    suspend fun getPayoutForPeriod(groupId: String, periodNumber: Int): PayoutEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(payout: PayoutEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayouts(payouts: List<PayoutEntity>)

    @Update
    suspend fun updatePayout(payout: PayoutEntity)

    // Bank Accounts
    @Query("SELECT * FROM bank_accounts WHERE userId = :userId LIMIT 1")
    fun getBankAccountForUserFlow(userId: String): Flow<BankAccountEntity?>

    @Query("SELECT * FROM bank_accounts WHERE userId = :userId LIMIT 1")
    suspend fun getBankAccountForUser(userId: String): BankAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(bankAccount: BankAccountEntity)

    // Payment Subscriptions (Tokenized Cards)
    @Query("SELECT * FROM payment_subscriptions WHERE userId = :userId AND groupId = :groupId LIMIT 1")
    suspend fun getSubscriptionForUserAndGroup(userId: String, groupId: String): PaymentSubscriptionEntity?

    @Query("SELECT * FROM payment_subscriptions WHERE userId = :userId LIMIT 1")
    fun getSubscriptionForUserFlow(userId: String): Flow<PaymentSubscriptionEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubscription(subscription: PaymentSubscriptionEntity)

    // Feedback
    @Query("SELECT * FROM feedback ORDER BY createdAt DESC")
    fun getAllFeedbackFlow(): Flow<List<FeedbackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: FeedbackEntity)

    @Query("UPDATE feedback SET status = :status WHERE id = :id")
    suspend fun updateFeedbackStatus(id: String, status: String)

    // Error Reports
    @Query("SELECT * FROM error_reports ORDER BY createdAt DESC")
    fun getAllErrorReportsFlow(): Flow<List<ErrorReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertErrorReport(report: ErrorReportEntity)

    @Query("UPDATE error_reports SET status = :status WHERE id = :id")
    suspend fun updateErrorReportStatus(id: String, status: String)

    // Notifications
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getNotificationsForUserFlow(userId: String): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotificationsFlow(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: String)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC")
    fun getAllAuditLogsFlow(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE entityId = :entityId ORDER BY createdAt DESC")
    fun getAuditLogsForEntityFlow(entityId: String): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLogEntity)
}
