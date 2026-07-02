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
    fun `expectQuestionDialog should fail if no dialog present`() {
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
    fun `expectErrorDialog dismisses the error and finds nothing on second call`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Error")

        expectErrorDialog("Something went wrong")
        panel.result shouldBe Unit

        val error = shouldThrow<AssertionError> { expectErrorDialog("Something went wrong") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectErrorDialog fails if dialog title is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        val error = shouldThrow<AssertionError> { expectErrorDialog("Would you like a cup of tea?") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectErrorDialog fails if dialog message is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Error")

        val error = shouldThrow<AssertionError> { expectErrorDialog("Beep boop") }
        error.message.shouldContain("expected:<\"Beep boop\"> but was:<\"Something went wrong\">")
    }

    @Test
    fun `waitForErrorDialog works`() {
        val panel = OptionPaneLauncher()
        runAsync {
            Thread.sleep(1000)
            panel.clickButton(text = "Error", async = false)
        }

        shouldThrow<AssertionError> {
            expectErrorDialog("Something went wrong")
        }

        waitForErrorDialog("Something went wrong")
        panel.result shouldBe Unit
    }

    @Test
    fun `expectInfoDialog dismisses the error and finds nothing on second call`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Info")

        expectInfoDialog("This is a message.")
        panel.result shouldBe Unit

        val error = shouldThrow<AssertionError> { expectInfoDialog("This is a message.") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectInfoDialog fails if dialog title is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Question")

        val error = shouldThrow<AssertionError> { expectInfoDialog("Would you like a cup of tea?") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `expectInfoDialog fails if dialog message is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Info")

        val error = shouldThrow<AssertionError> { expectInfoDialog("Beep boop") }
        error.message.shouldContain("expected:<\"Beep boop\"> but was:<\"This is a message.\">")
    }

    @Test
    fun `waitForInfoDialog works`() {
        val panel = OptionPaneLauncher()
        runAsync {
            Thread.sleep(1000)
            panel.clickButton(text = "Info", async = false)
        }

        shouldThrow<AssertionError> {
            expectInfoDialog("This is a message.")
        }

        waitForInfoDialog("This is a message.")
        panel.result shouldBe Unit
    }

    @Test
    fun `Should be able to type into an input dialog`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        typeIntoInputDialog("Here is some text")

        panel.result shouldBe "Here is some text"

        val error = shouldThrow<AssertionError> { typeIntoInputDialog("Input", "Here is some text") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `typeIntoInputDialog should error if title is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        val error = shouldThrow<AssertionError> { typeIntoInputDialog("Wrong title", "Here is some text") }
        error.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `typeIntoInputDialog should fail if input isn't a free-text entry`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ComboInput")

        shouldThrow<NoSuchComponentException> { typeIntoInputDialog("Here is some text", "Input") }
    }

    @Test
    fun `should be able to cancel an input dialog`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        cancelDialog("Input")

        panel.result shouldBe null
        findOptionPaneDialog("Input")!!.shouldNotBeVisible()
    }

    @Test
    fun `should be able to dismiss an input dialog`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        dismissDialog("Input")

        panel.result shouldBe null
        findOptionPaneDialog("Input")!!.shouldNotBeVisible()
    }

    @Test
    fun `findOptionPaneDialog returns null if none found`() {
        findOptionPaneDialog("Question") shouldBe null
    }
}