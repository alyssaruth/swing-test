package com.github.alyssaburlton.swingtest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import javax.swing.JOptionPane

class OptionPanesTest {
    @Test
    fun `expectQuestionDialog should fail if message isn't expected`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        val error = shouldThrow<AssertionError> {
            expectQuestionDialog("Would you like a cup of coffee?", "Yes")
        }

        error.message shouldBe """expected:<"Would you like a cup of coffee?"> but was:<"Would you like a cup of tea?">"""
    }

    @Test
    fun `getQuestionDialog should fail if no dialog present`() {
        val error = shouldThrow<Exception> {
            getQuestionDialog()
        }

        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectQuestionDialog answers the question and finds nothing on second call`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        expectQuestionDialog("Would you like a cup of tea?", "Yes")
        panel.result shouldBe JOptionPane.YES_OPTION

        val error = shouldThrow<Exception> { expectQuestionDialog("Would you like a cup of tea?", "Yes") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectQuestionDialog fails for invalid answer`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        val error = shouldThrow<Exception> {
            expectQuestionDialog("Would you like a cup of tea?", "Not sure")
        }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `finders return null if none found`() {
        findQuestionDialog() shouldBe null
        findErrorDialog() shouldBe null
        findInfoDialog() shouldBe null
    }
}