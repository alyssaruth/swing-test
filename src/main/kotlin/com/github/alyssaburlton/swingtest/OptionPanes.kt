package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.text.JTextComponent

/**
 * Question
 */
fun waitForQuestionDialog(message: String, answer: String, title: String = "Question", timeout: Int = 5000) {
    waitForOptionPaneDialog(title, timeout = timeout)

    expectQuestionDialog(message, answer, title)
}

fun expectQuestionDialog(message: String, answer: String, title: String = "Question") {
    expectOptionPaneDialog(message, title, answer)
}

/**
 * Error
 */
fun waitForErrorDialog(message: String, title: String = "Error", timeout: Int = 5000) {
    waitForOptionPaneDialog(title, timeout = timeout)

    expectErrorDialog(message, title)
}

fun expectErrorDialog(message: String, title: String = "Error") {
    expectOptionPaneDialog(message, title, "OK")
}

/**
 * Info
 */
fun waitForInfoDialog(message: String, title: String = "Information", timeout: Int = 5000) {
    waitForOptionPaneDialog(title, timeout = timeout)

    expectInfoDialog(message)
}
fun expectInfoDialog(message: String, title: String = "Information") {
    expectOptionPaneDialog(message, title, "OK")
}

private fun expectOptionPaneDialog(message: String, title: String, buttonText: String) {
    val dlg = getOptionPaneDialog(title) { it.isVisible }
    dlg.getDialogMessage() shouldBe message
    dlg.clickButton(text = buttonText)
}


fun selectFromOptionDialog(title: String, selection: String) {
    val dialog = getOptionPaneDialog(title) { it.isVisible }
    dialog.clickButton(text = selection, async = true)
}

fun typeIntoInputDialog(title: String, text: String) {
    val dlg = getOptionPaneDialog(title) { it.isVisible }
    dlg.getChild<JTextComponent>().typeText(text)
    dlg.clickOk(async = true)
}

inline fun <reified E : Any> selectOptionFromInputDialog(title: String, value: E) {
    val dlg = getWindow<JDialog> { it.title == title && it.isVisible }

    val combo = dlg.findChild<JComboBox<E>>()
    if (combo != null) {
        if (!combo.items().contains(value)) {
            throw AssertionError(
                "Input dialog did not contain desired option $value. Options: ${combo.items()}"
            )
        }
        combo.selectedItem = value
    } else {
        val list = dlg.getChild<JList<E>>()
        if (!list.items().contains(value)) {
            throw AssertionError(
                "Input dialog did not contain desired option $value. Options: ${list.items()}"
            )
        }

        list.setSelectedValue(value, true)
    }

    dlg.clickOk(async = true)
}

inline fun <reified E> JList<E>.items(): List<E> {
    val size = model.size
    return (0 until size).map { model.getElementAt(it) }
}

fun <K> JComboBox<K>.items() = (0 until model.size).map { model.getElementAt(it) }

fun cancelOptionDialog(title: String) {
    val dialog = getOptionPaneDialog(title) { it.isVisible }
    dialog.clickCancel(async = true)
}

fun dismissDialog(title: String) {
    val dialog = getOptionPaneDialog(title) { it.isVisible }
    dialog.dispose()
    flushEdt()
}


private fun getOptionPaneDialog(title: String, predicate: (window: JDialog) -> Boolean = { true }) =
    getWindow<JDialog> { it.title == title && predicate(it) }

private fun waitForOptionPaneDialog(title: String, timeout: Int = 5000) {
    waitForAssertion(timeout) { getOptionPaneDialog(title) { it.isVisible } }
}

fun findOptionPaneDialog(
    title: String,
    predicate: (window: JDialog) -> Boolean = { true },
) = findWindow<JDialog> { it.title == title && predicate(it) }

fun JDialog.getDialogMessage(): String {
    val messageLabels = findAll<JLabel>().filter { it.name == "OptionPane.label" }
    if (messageLabels.isEmpty()) {
        throw Exception(
            "Dialog unexpectedly had no message.\n\nComponent tree:\n\n${generateComponentTree()}"
        )
    }
    return messageLabels.joinToString("\n\n") { it.text }
}