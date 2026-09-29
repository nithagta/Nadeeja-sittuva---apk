package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.sittuva.model.Language
import com.example.sittuva.model.SittuvaCalculator
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sittuva", appName)
    }

    @Test
    fun `verify currency formatting in Sinhala and English`() {
        val amount = 10000L
        val formattedSi = SittuvaCalculator.formatCurrency(amount, Language.SINHALA)
        val formattedEn = SittuvaCalculator.formatCurrency(amount, Language.ENGLISH)
        assertEquals("රු. 10,000", formattedSi)
        assertEquals("Rs. 10,000", formattedEn)
    }
}
