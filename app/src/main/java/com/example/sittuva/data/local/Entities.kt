package com.example.sittuva.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phone: String,
    val role: String, // ADMIN, MEMBER
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val name: String,
    val inviteCode: String = UUID.randomUUID().toString().take(8).uppercase(),
    val adminId: String,
    val adminName: String,
    val adminPhone: String,
    val participantLimit: Int, // 2 to 49
    val contributionAmount: Long, // e.g. 2000
    val frequency: String, // WEEKLY, MONTHLY
    val startDate: Long,
    val gracePeriod: String, // ONE_DAY, THREE_DAYS, FOUR_DAYS, ONE_WEEK, ONE_MONTH
    val status: String, // SETUP, ACTIVE, COMPLETED, CANCELLED
    val currentPeriod: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val startedAt: Long? = null,
    val completedAt: Long? = null
)

@Entity(tableName = "group_members")
data class GroupMemberEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val payoutPosition: Int, // 1-indexed
    val isDefaultAdmin: Boolean = false,
    val status: String = "ACTIVE",
    val joinedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "payment_subscriptions")
data class PaymentSubscriptionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val userId: String,
    val providerToken: String,
    val cardMask: String, // e.g. "•••• 4242"
    val cardBrand: String, // "Visa", "Mastercard"
    val status: String = "ACTIVE"
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val userId: String,
    val userName: String,
    val periodNumber: Int,
    val amount: Long,
    val currency: String = "LKR",
    val status: String, // PENDING, PAID, FAILED, OVERDUE
    val providerReference: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)

@Entity(tableName = "payouts")
data class PayoutEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val groupId: String,
    val recipientUserId: String,
    val recipientName: String,
    val periodNumber: Int,
    val amount: Long,
    val status: String, // SCHEDULED, PROCESSING, COMPLETED, FAILED
    val providerReference: String? = null,
    val bankAccountMask: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val bankName: String,
    val branch: String,
    val accountNumberMask: String, // e.g. "•••• •••• 5678"
    val accountHolderName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "feedback")
data class FeedbackEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val groupId: String? = null,
    val groupName: String? = null,
    val title: String,
    val description: String,
    val status: String = "NEW", // NEW, REVIEWING, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "error_reports")
data class ErrorReportEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val userName: String,
    val userPhone: String,
    val groupId: String? = null,
    val groupName: String? = null,
    val title: String,
    val description: String,
    val status: String = "NEW", // NEW, REVIEWING, RESOLVED
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val title: String,
    val body: String,
    val type: String, // INVITATION, JOINED, FULL, DUE, SUCCESS, FAILED, OVERDUE, PAYOUT, COMPLETED, FEEDBACK, ERROR_REPORT
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val actorId: String,
    val actorName: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val oldValue: String? = null,
    val newValue: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
