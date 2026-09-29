package com.example.sittuva.model

enum class Language {
    SINHALA,
    ENGLISH
}

enum class UserRole {
    ADMIN,
    MEMBER
}

enum class Frequency {
    WEEKLY,   // සතියකට වරක්
    MONTHLY   // මාසයකට වරක්
}

enum class GroupStatus {
    SETUP,      // සැලසුම් වෙමින් පවතී (Edit rules & order allowed)
    ACTIVE,     // ක්‍රියාත්මකයි (Core financial rules locked)
    COMPLETED,  // අවසන් (All payouts completed)
    CANCELLED   // අවලංගු කරන ලදී
}

enum class MemberStatus {
    ACTIVE,
    REMOVED
}

enum class PaymentStatus {
    PENDING,  // ගෙවීමට ඇත
    PAID,     // සාර්ථකව ගෙවා ඇත
    FAILED,   // අසාර්ථකයි
    OVERDUE   // කල් ඉකුත් වී ඇත (Past grace period)
}

enum class PayoutStatus {
    SCHEDULED,   // සැලසුම් කර ඇත
    PROCESSING,  // සැකසෙමින් පවතී
    COMPLETED,   // සාර්ථකව බැර විය
    FAILED       // අසාර්ථකයි
}

enum class GracePeriodOption(val days: Int, val labelEn: String, val labelSi: String) {
    ONE_DAY(1, "1 Day", "දින 1"),
    THREE_DAYS(3, "3 Days", "දින 3"),
    FOUR_DAYS(4, "4 Days", "දින 4"),
    ONE_WEEK(7, "1 Week", "සති 1"),
    ONE_MONTH(30, "1 Month", "මාස 1")
}

sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val messageEn: String, val messageSi: String) : ValidationResult()
}
