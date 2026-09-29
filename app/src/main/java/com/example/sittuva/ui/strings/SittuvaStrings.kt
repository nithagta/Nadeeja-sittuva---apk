package com.example.sittuva.ui.strings

import com.example.sittuva.model.Language

object SittuvaStrings {

    fun appTitle(lang: Language) = if (lang == Language.SINHALA) "සිට්ටුව" else "Sittuva"
    fun appTagline(lang: Language) = if (lang == Language.SINHALA) "ශ්‍රී ලාංකීය සුරක්ෂිත සිට්ටු කළමනාකරණය" else "Sri Lankan Rotating Savings & Trust"

    // Navigation & Tabs
    fun adminDashboard(lang: Language) = if (lang == Language.SINHALA) "පරිපාලක පුවරුව" else "Admin Dashboard"
    fun memberDashboard(lang: Language) = if (lang == Language.SINHALA) "සාමාජික පුවරුව" else "Member Dashboard"
    fun groupsTab(lang: Language) = if (lang == Language.SINHALA) "සිට්ටු සමූහ" else "Sittuva Groups"
    fun createGroup(lang: Language) = if (lang == Language.SINHALA) "නව සිට්ටුවක් අරඹන්න" else "Start New Sittuva"
    fun myPayments(lang: Language) = if (lang == Language.SINHALA) "මගේ ගෙවීම්" else "My Payments"
    fun feedback(lang: Language) = if (lang == Language.SINHALA) "අදහස් සහ යෝජනා" else "Ideas & Suggestions"
    fun reportProblem(lang: Language) = if (lang == Language.SINHALA) "ගැටලුවක් වාර්තා කරන්න" else "Report a Problem"
    fun notifications(lang: Language) = if (lang == Language.SINHALA) "දැනුම්දීම්" else "Notifications"
    fun auditLogs(lang: Language) = if (lang == Language.SINHALA) "විගණන සටහන්" else "Audit Logs"
    fun bankAccount(lang: Language) = if (lang == Language.SINHALA) "බැංකු ගිණුම" else "Payout Bank Account"

    // Group Information
    fun activeGroups(lang: Language) = if (lang == Language.SINHALA) "ක්‍රියාකාරී සිට්ටු" else "Active Groups"
    fun completedGroups(lang: Language) = if (lang == Language.SINHALA) "අවසන් වූ සිට්ටු" else "Completed Groups"
    fun totalPool(lang: Language) = if (lang == Language.SINHALA) "එක් වටයක එකතුව" else "Period Total Payout"
    fun contributionPerPerson(lang: Language) = if (lang == Language.SINHALA) "එක් අයෙකුගේ දායකත්වය" else "Contribution Per Member"
    fun participants(lang: Language) = if (lang == Language.SINHALA) "සාමාජිකයින් ගණන" else "Participants"
    fun frequency(lang: Language) = if (lang == Language.SINHALA) "ගෙවීමේ කාල පරතරය" else "Payment Frequency"
    fun weekly(lang: Language) = if (lang == Language.SINHALA) "සතියකට වරක්" else "Weekly"
    fun monthly(lang: Language) = if (lang == Language.SINHALA) "මාසයකට වරක්" else "Monthly"
    fun currentPeriod(lang: Language) = if (lang == Language.SINHALA) "වත්මන් වටය" else "Current Period"
    fun nextPaymentDate(lang: Language) = if (lang == Language.SINHALA) "ඊළඟ ගෙවීම් දිනය" else "Next Payment Date"
    fun currentRecipient(lang: Language) = if (lang == Language.SINHALA) "වත්මන් වටයේ ලාභියා" else "Current Payout Recipient"
    fun totalDuration(lang: Language) = if (lang == Language.SINHALA) "මුළු කාලය" else "Total Duration"

    // Wizard
    fun wizardStep1Title(lang: Language) = if (lang == Language.SINHALA) "1. සිට්ටු සමූහයේ නම" else "1. Group Name"
    fun wizardStep2Title(lang: Language) = if (lang == Language.SINHALA) "2. සාමාජිකයින් සංඛ්‍යාව (2-49)" else "2. Member Count (2-49)"
    fun wizardStep3Title(lang: Language) = if (lang == Language.SINHALA) "3. දායකත්ව මුදල" else "3. Contribution Amount"
    fun wizardStep4Title(lang: Language) = if (lang == Language.SINHALA) "4. ගෙවීමේ කාල පරතරය" else "4. Payment Frequency"
    fun wizardStep5Title(lang: Language) = if (lang == Language.SINHALA) "5. ආරම්භක දිනය" else "5. Starting Date"
    fun wizardStep6Title(lang: Language) = if (lang == Language.SINHALA) "6. සහන කාලය (Grace Period)" else "6. Grace Period"
    fun wizardStep7Title(lang: Language) = if (lang == Language.SINHALA) "7. සාමාජිකයින්ට ආරාධනා කරන්න" else "7. Invite Members"
    fun wizardStep8Title(lang: Language) = if (lang == Language.SINHALA) "8. ගෙවීම් අනුපිළිවෙල සකසන්න" else "8. Arrange Payout Order"
    fun wizardStep9Title(lang: Language) = if (lang == Language.SINHALA) "9. සාරාංශය පරීක්ෂා කරන්න" else "9. Review Summary"
    fun startSittuvaGroup(lang: Language) = if (lang == Language.SINHALA) "සිට්ටුව ආරම්භ කරන්න" else "START SITTUVA GROUP"

    // Statuses
    fun statusPending(lang: Language) = if (lang == Language.SINHALA) "ගෙවීමට ඇත" else "Pending"
    fun statusPaid(lang: Language) = if (lang == Language.SINHALA) "ගෙවා ඇත" else "Paid"
    fun statusFailed(lang: Language) = if (lang == Language.SINHALA) "අසාර්ථකයි" else "Failed"
    fun statusOverdue(lang: Language) = if (lang == Language.SINHALA) "කල් ඉකුත් වී ඇත" else "Overdue"
    fun statusActive(lang: Language) = if (lang == Language.SINHALA) "ක්‍රියාකාරී" else "Active"
    fun statusCompleted(lang: Language) = if (lang == Language.SINHALA) "අවසන් වූ" else "Completed"
    fun statusSetup(lang: Language) = if (lang == Language.SINHALA) "සැකසුමෙහි" else "In Setup"

    // Invitation
    fun inviteTitle(lang: Language) = if (lang == Language.SINHALA) "සිට්ටු සමූහ ආරාධනාව" else "Sittuva Group Invitation"
    fun joinGroup(lang: Language) = if (lang == Language.SINHALA) "සිට්ටුවට එක්වන්න" else "JOIN SITTUVA GROUP"
    fun shareInvite(lang: Language) = if (lang == Language.SINHALA) "ආරාධනාව බෙදාහරින්න" else "Share Invitation"
    fun slotsRemaining(remaining: Int, lang: Language) = if (lang == Language.SINHALA) "ඉතිරි ඉඩ: $remaining" else "Remaining Slots: $remaining"
    fun groupIsFull(lang: Language) = if (lang == Language.SINHALA) "මෙම සිට්ටු සමූහය සම්පූර්ණයි." else "This Sittuva group is full."
    fun askAdminForAnother(lang: Language) = if (lang == Language.SINHALA) "කරුණාකර වෙනත් ආරාධනාවක් සඳහා පරිපාලක අමතන්න." else "Please ask the group Admin for another invitation."

    // Verification & Payment
    fun payNow(lang: Language) = if (lang == Language.SINHALA) "දැන් ගෙවන්න" else "Pay Now"
    fun securePayment(lang: Language) = if (lang == Language.SINHALA) "සුරක්ෂිත ගෙවීම (PCI Tokenized)" else "Secure Payment (PCI Tokenized)"
    fun disbursePayout(lang: Language) = if (lang == Language.SINHALA) "මුදල් බැර කරන්න" else "Disburse Payout"
    fun lockedNotice(lang: Language) = if (lang == Language.SINHALA) "මූල්‍ය නීති ස්ථිර කර ඇත (Group Locked)" else "Core Financial Rules Locked"

    // Errors
    fun invalidParticipantCount(lang: Language) = if (lang == Language.SINHALA) "කරුණාකර සාමාජිකයින් 2ත් 49ත් අතර සංඛ්‍යාවක් ඇතුළත් කරන්න." else "Please enter between 2 and 49 participants."
    fun invalidAmount(lang: Language) = if (lang == Language.SINHALA) "කරුණාකර නිවැරදි දායකත්ව මුදලක් ඇතුළත් කරන්න." else "Please enter a valid contribution amount."
    fun maxGroupsReached(lang: Language) = if (lang == Language.SINHALA) "ඔබ සතුව දැනටමත් ක්‍රියාකාරී සිට්ටු 3ක් ඇත. අලුත් එකක් ඇරඹීමට පෙර පවතින එකක් අවසන් කරන්න." else "You already have 3 active groups. Complete or close an existing group before creating another."
}
