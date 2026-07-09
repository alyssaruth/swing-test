package io.github.alyssaruth.swingtest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import javax.swing.JDialog
import javax.swing.JOptionPane
import javax.swing.SwingUtilities

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
    fun `expectQuestionDialog works for custom options`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "Custom Question")

        expectQuestionDialog("What would you like to drink?", "Coffee")
        panel.result shouldBe TEST_OPTIONS.indexOf("Coffee")
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
        SwingUtilities.invokeLater {
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
    fun `expectErrorDialog fails if dialog messageType is wrong`() {
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
        SwingUtilities.invokeLater {
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
    fun `expectInfoDialog fails if dialog messageType is wrong`() {
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
        SwingUtilities.invokeLater {
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

        typeIntoInputDialog("Enter some text", "Here is some text")

        panel.result shouldBe "Here is some text"

        val error = shouldThrow<AssertionError> { typeIntoInputDialog("Enter some text", "Here is some text") }
        error.cause!!.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `typeIntoInputDialog should error if title is wrong`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        val error = shouldThrow<AssertionError> { typeIntoInputDialog("Enter some text", "Here is some text", title = "Wrong title") }
        error.cause!!.message.shouldContain("Window not found for predicate.")
    }

    @Test
    fun `typeIntoInputDialog should fail if input isn't a free-text entry`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ComboInput")

        shouldThrow<NoSuchComponentException> { typeIntoInputDialog("Favourite Square?", "Here is some text") }
    }

    @Test
    fun `should be able to cancel an input dialog`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        cancelDialog("Input")

        panel.result shouldBe null
        findWindow<JDialog> { it.title == "Input" }!!.shouldNotBeVisible()
    }

    @Test
    fun `should be able to dismiss an input dialog`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "TextInput")

        dismissDialog("Input")

        panel.result shouldBe null
        findWindow<JDialog> { it.title == "Input" }!!.shouldNotBeVisible()
    }

    @Test
    fun `selectOptionFromInputDialog should work for a combo box`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ComboInput")

        selectOptionFromInputDialog("Favourite Square?", 25)
        panel.result shouldBe 25

        shouldThrow<AssertionError> { selectOptionFromInputDialog("Favourite Square?", 25) }
    }

    @Test
    fun `selectOptionFromInputDialog should throw assertion error if option not found in combo`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ComboInput")

        val err = shouldThrow<AssertionError> { selectOptionFromInputDialog("Favourite Square?", 26) }
        err.message shouldBe "Input dialog did not contain desired combo option 26. Options: [1, 4, 9, 16, 25, 36, 49, 64]"
    }

    @Test
    fun `selectOptionFromInputDialog should work for a list`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ListInput")

        selectOptionFromInputDialog("Pick a number", 56)
        panel.result shouldBe 56

        shouldThrow<AssertionError> { selectOptionFromInputDialog("Pick a number", 25) }
    }

    @Test
    fun `selectOptionFromInputDialog should throw assertion error if option not found in list`() {
        val panel = OptionPaneLauncher()
        panel.clickButton(text = "ListInput")

        val err = shouldThrow<AssertionError> { selectOptionFromInputDialog("Pick a number", 101) }
        err.message shouldBe "Input dialog did not contain desired list option 101. Options: ${(1..100).toList()}"
    }

    @Test
    fun `gets correct dialog message for a long multi-line message`() {
        val message = """This is a big long message.
            With multiple new lines.
            
            And spaces between some lines.
            
            And Swing does silly things when rendering them.
        """.trimMargin()

        runAsync {
            JOptionPane.showMessageDialog(
                null,
                message,
                "Information",
                JOptionPane.INFORMATION_MESSAGE,
            )
        }

        val dlg = getOptionPaneDialog(JOptionPane.INFORMATION_MESSAGE)
        dlg.getDialogMessage() shouldBe message
        dlg.dispose()
    }

    @Test
    fun `Can find OptionPane dialog among other unrelated JDialogs`() {
        val dlg = JDialog().apply {
            add(OptionPaneLauncher())
            isVisible = true
        }

        dlg.clickButton(text = "Error")

        expectErrorDialog("Something went wrong")
    }
}