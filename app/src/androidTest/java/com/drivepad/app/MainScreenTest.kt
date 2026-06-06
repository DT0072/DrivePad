package com.drivepad.app

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class MainScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun homeDashboard_isDisplayed() {
        composeTestRule.onNodeWithText("Navigation").fetchSemanticsNode()
    }
}
