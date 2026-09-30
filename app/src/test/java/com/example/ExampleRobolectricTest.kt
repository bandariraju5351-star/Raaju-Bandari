package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.NoiseMode
import com.example.model.NoiseProfileType
import com.example.model.NoiseSeverity
import com.example.viewmodel.ClearCallScreen
import com.example.viewmodel.ClearCallViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("CLEARCALL AI", appName)
    }

    @Test
    fun `test noise profiles are configured correctly`() {
        val dj = NoiseProfileType.DJ_LOUDSPEAKER
        assertEquals("DJ / Loudspeaker", dj.title)
        assertTrue(dj.defaultReductionStrength >= 0.9f)

        val crowd = NoiseProfileType.CROWD
        assertEquals("Crowd & Babble", crowd.title)

        val traffic = NoiseProfileType.TRAFFIC
        assertEquals("Traffic & Engine", traffic.title)
    }

    @Test
    fun `test noise modes attenuation`() {
        assertEquals("Auto", NoiseMode.AUTO.title)
        assertEquals("Maximum", NoiseMode.MAXIMUM.title)
        assertTrue(NoiseMode.MAXIMUM.filterLevel > NoiseMode.LIGHT.filterLevel)
    }

    @Test
    fun `test viewmodel navigation and state initialization`() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = ClearCallViewModel(app)

        assertEquals(ClearCallScreen.HOME, viewModel.currentScreen.value)
        assertTrue(viewModel.isClearCallActive.value)
        assertTrue(viewModel.isAiEnabled.value)

        viewModel.navigateTo(ClearCallScreen.CLEAR_CALL)
        assertEquals(ClearCallScreen.CLEAR_CALL, viewModel.currentScreen.value)

        viewModel.toggleAi()
        assertEquals(false, viewModel.isAiEnabled.value)

        viewModel.selectNoiseMode(NoiseMode.STRONG)
        assertEquals(NoiseMode.STRONG, viewModel.selectedNoiseMode.value)
    }
}
