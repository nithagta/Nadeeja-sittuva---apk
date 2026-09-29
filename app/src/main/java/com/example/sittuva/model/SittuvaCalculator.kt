package com.example.sittuva.model

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object SittuvaCalculator {

    const val MIN_PARTICIPANTS = 2
    const val MAX_PARTICIPANTS = 49
    const val MAX_ACTIVE_GROUPS_PER_ADMIN = 3

    /**
     * Core Financial Rule:
     * TOTAL PAYOUT = TOTAL PARTICIPANTS * CONTRIBUTION AMOUNT
     * No commission, no hidden deductions.
     */
    fun calculateTotalPayout(contributionAmount: Long, totalParticipants: Int): Long {
        require(totalParticipants >= 0) { "Participants cannot be negative" }
        require(contributionAmount >= 0) { "Contribution cannot be negative" }
        return contributionAmount * totalParticipants.toLong()
    }

    /**
     * Validates participant count.
     * Allowed range: 2 to 49.
     */
    fun validateParticipantCount(count: Int): ValidationResult {
        return if (count in MIN_PARTICIPANTS..MAX_PARTICIPANTS) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                messageEn = "Please enter between $MIN_PARTICIPANTS and $MAX_PARTICIPANTS participants.",
                messageSi = "කරුණාකර සාමාජිකයින් $MIN_PARTICIPANTS ත් $MAX_PARTICIPANTS ත් අතර සංඛ්‍යාවක් ඇතුළත් කරන්න."
            )
        }
    }

    /**
     * Validates custom contribution amount.
     */
    fun validateContributionAmount(amount: Long): ValidationResult {
        return if (amount > 0) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(
                messageEn = "Please enter a valid contribution amount.",
                messageSi = "කරුණාකර නිවැරදි දායකත්ව මුදලක් ඇතුළත් කරන්න."
            )
        }
    }

    /**
     * Calculates automatic duration text.
     * e.g. 7 participants + Weekly = 7 weeks
     * 7 participants + Monthly = 7 months
     */
    fun calculateDurationText(participants: Int, frequency: Frequency, lang: Language): String {
        return when (frequency) {
            Frequency.WEEKLY -> {
                if (lang == Language.SINHALA) "සති $participants" else "$participants Weeks"
            }
            Frequency.MONTHLY -> {
                if (lang == Language.SINHALA) "මාස $participants" else "$participants Months"
            }
        }
    }

    /**
     * Formats Sri Lankan currency amount.
     * e.g. Rs. 10,000 / රු. 10,000
     */
    fun formatCurrency(amount: Long, lang: Language = Language.SINHALA): String {
        val formatter = NumberFormat.getNumberInstance(Locale.US)
        val formatted = formatter.format(amount)
        return if (lang == Language.SINHALA) "රු. $formatted" else "Rs. $formatted"
    }

    /**
     * Generates recurring schedule dates starting from startDateMillis.
     */
    fun calculatePaymentDates(startDateMillis: Long, totalParticipants: Int, frequency: Frequency): List<Long> {
        val dates = mutableListOf<Long>()
        val calendar = Calendar.getInstance().apply { timeInMillis = startDateMillis }

        for (i in 0 until totalParticipants) {
            dates.add(calendar.timeInMillis)
            when (frequency) {
                Frequency.WEEKLY -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                Frequency.MONTHLY -> calendar.add(Calendar.MONTH, 1)
            }
        }
        return dates
    }

    /**
     * Formats date to user-friendly string.
     */
    fun formatDate(timeMillis: Long, lang: Language = Language.SINHALA): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.US)
        return sdf.format(Date(timeMillis))
    }
}
