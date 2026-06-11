package com.github.alyssaburlton.swingtest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.concurrent.CompletableFuture.runAsync
import javax.swing.JOptionPane

@ExtendWith(SwingTestCleanupExtension::class)
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
        val error = shouldThrow<AssertionError> {
            expectQuestionDialog("Anything", "No")
        }

        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `waitForQuestionDialog should time out if no dialog present`() {
        val error = shouldThrow<AssertionError> {
            waitForQuestionDialog("Anything", "No", timeout = 2000)
        }

        error.message.shouldContain("Timed out waiting for assertion")
        error.cause!!.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectQuestionDialog answers the question and finds nothing on second call`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        expectQuestionDialog("Would you like a cup of tea?", "Yes")
        panel.result shouldBe JOptionPane.YES_OPTION

        val error = shouldThrow<AssertionError> { expectQuestionDialog("Would you like a cup of tea?", "Yes") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectQuestionDialog fails for invalid answer`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        val error = shouldThrow<Exception> {
            expectQuestionDialog("Would you like a cup of tea?", "Not sure")
        }
        error.message.shouldContain("Found 0 JButtons")
    }

    @Test
    fun `waitForQuestionDialog works`() {
        val panel = OptionPaneLauncher()
        runAsync {
            Thread.sleep(1000)
            panel.clickButton(text = "Question", async = false)
        }

        shouldThrow<AssertionError> {
            expectQuestionDialog("Would you like a cup of tea?", "No")
        }

        waitForQuestionDialog("Would you like a cup of tea?", "No")
        panel.result shouldBe JOptionPane.NO_OPTION
    }

    @Test
    fun `findOptionPaneDialog returns null if none found`() {
        findOptionPaneDialog("Question") shouldBe null
    }
}