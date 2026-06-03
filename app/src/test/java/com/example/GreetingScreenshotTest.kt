package com.example

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent { MyApplicationTheme { Greeting("Robolectric") } }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun testPercentageEvaluation() {
    val resultPlus = com.example.util.MathEvaluator.evaluate("599+18%")
    println("599+18% = $resultPlus")
    org.junit.Assert.assertEquals(706.82, resultPlus, 1e-2)

    val resultMinus = com.example.util.MathEvaluator.evaluate("599-18%")
    println("599-18% = $resultMinus")
    org.junit.Assert.assertEquals(491.18, resultMinus, 1e-2)

    val resultMult = com.example.util.MathEvaluator.evaluate("599*18%")
    println("599*18% = $resultMult")
    org.junit.Assert.assertEquals(107.82, resultMult, 1e-2)

    val resultSingle = com.example.util.MathEvaluator.evaluate("18%")
    org.junit.Assert.assertEquals(0.18, resultSingle, 1e-6)
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}
