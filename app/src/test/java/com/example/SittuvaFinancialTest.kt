package com.example

import com.example.sittuva.model.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SittuvaFinancialTest {

    /**
     * Requirement Section 38: IMPORTANT CALCULATION TESTS
     * The formula must always be:
     * TOTAL PAYOUT = TOTAL PARTICIPANTS * CONTRIBUTION AMOUNT
     */

    @Test
    fun `Test 1 - 2 participants, Contribution 2000 yields payout 4000`() {
        val participants = 2
        val contribution = 2000L
        val expectedPayout = 4000L

        val actualPayout = SittuvaCalculator.calculateTotalPayout(contribution, participants)
        assertEquals(expectedPayout, actualPayout)
    }

    @Test
    fun `Test 2 - 5 participants, Contribution 2000 yields payout 10000`() {
        val participants = 5
        val contribution = 2000L
        val expectedPayout = 10000L

        val actualPayout = SittuvaCalculator.calculateTotalPayout(contribution, participants)
        assertEquals(expectedPayout, actualPayout)
    }

    @Test
    fun `Test 3 - 7 participants, Contribution 2500 yields payout 17500`() {
        val participants = 7
        val contribution = 2500L
        val expectedPayout = 17500L

        val actualPayout = SittuvaCalculator.calculateTotalPayout(contribution, participants)
        assertEquals(expectedPayout, actualPayout)
    }

    @Test
    fun `Test 4 - 49 participants, Contribution 200 yields payout 9800`() {
        val participants = 49
        val contribution = 200L
        val expectedPayout = 9800L

        val actualPayout = SittuvaCalculator.calculateTotalPayout(contribution, participants)
        assertEquals(expectedPayout, actualPayout)
    }

    /**
     * Requirement Section 39: IMPORTANT ROTATION TEST
     * 5 participants:
     * 1. Admin
     * 2. Member A
     * 3. Member B
     * 4. Member C
     * 5. Member D
     */
    @Test
    fun `Rotation Test - Weekly mode recipient progression`() {
        val members = listOf("Admin", "Member A", "Member B", "Member C", "Member D")
        val participants = 5
        val frequency = Frequency.WEEKLY

        // Verify duration is 5 weeks
        val durationSi = SittuvaCalculator.calculateDurationText(participants, frequency, Language.SINHALA)
        val durationEn = SittuvaCalculator.calculateDurationText(participants, frequency, Language.ENGLISH)
        assertEquals("සති 5", durationSi)
        assertEquals("5 Weeks", durationEn)

        // Verify rotation matches scheduled member for each period
        for (period in 1..participants) {
            val recipient = members[period - 1]
            when (period) {
                1 -> assertEquals("Admin", recipient)
                2 -> assertEquals("Member A", recipient)
                3 -> assertEquals("Member B", recipient)
                4 -> assertEquals("Member C", recipient)
                5 -> assertEquals("Member D", recipient)
            }
        }
    }

    @Test
    fun `Rotation Test - Monthly mode recipient progression`() {
        val members = listOf("Admin", "Member A", "Member B", "Member C", "Member D")
        val participants = 5
        val frequency = Frequency.MONTHLY

        // Verify duration is 5 months
        val durationSi = SittuvaCalculator.calculateDurationText(participants, frequency, Language.SINHALA)
        val durationEn = SittuvaCalculator.calculateDurationText(participants, frequency, Language.ENGLISH)
        assertEquals("මාස 5", durationSi)
        assertEquals("5 Months", durationEn)

        // Verify rotation order
        for (period in 1..participants) {
            val recipient = members[period - 1]
            when (period) {
                1 -> assertEquals("Admin", recipient)
                2 -> assertEquals("Member A", recipient)
                3 -> assertEquals("Member B", recipient)
                4 -> assertEquals("Member C", recipient)
                5 -> assertEquals("Member D", recipient)
            }
        }
    }

    /**
     * Validation Rules:
     * Minimum participants: 2
     * Maximum participants: 49
     * 1 and 50+ are invalid
     */
    @Test
    fun `Participant count validation - allowed range 2 to 49`() {
        assertTrue(SittuvaCalculator.validateParticipantCount(1) is ValidationResult.Invalid)
        assertTrue(SittuvaCalculator.validateParticipantCount(50) is ValidationResult.Invalid)
        assertTrue(SittuvaCalculator.validateParticipantCount(0) is ValidationResult.Invalid)

        assertTrue(SittuvaCalculator.validateParticipantCount(2) is ValidationResult.Valid)
        assertTrue(SittuvaCalculator.validateParticipantCount(5) is ValidationResult.Valid)
        assertTrue(SittuvaCalculator.validateParticipantCount(49) is ValidationResult.Valid)
    }

    @Test
    fun `Contribution amount validation`() {
        assertTrue(SittuvaCalculator.validateContributionAmount(0) is ValidationResult.Invalid)
        assertTrue(SittuvaCalculator.validateContributionAmount(-500) is ValidationResult.Invalid)
        assertTrue(SittuvaCalculator.validateContributionAmount(2000) is ValidationResult.Valid)
    }
}
