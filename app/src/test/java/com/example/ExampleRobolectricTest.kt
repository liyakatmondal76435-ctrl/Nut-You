package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.model.StreakTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.concurrent.TimeUnit

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("CleanStreak", appName)
  }

  @Test
  fun `verify streak time calculation`() {
    val millis = TimeUnit.DAYS.toMillis(5) + TimeUnit.HOURS.toMillis(3) + TimeUnit.MINUTES.toMillis(20)
    val streakTime = StreakTime.fromDurationMillis(millis)
    assertEquals(5L, streakTime.days)
    assertEquals(3L, streakTime.hours)
    assertEquals(20L, streakTime.minutes)
  }
}
