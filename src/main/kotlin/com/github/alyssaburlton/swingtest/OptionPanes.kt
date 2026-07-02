package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.JOptionPane
import javax.swing.text.JTextComponent

/**
 * Question
 */
fun waitForQuestionDialog(message: String, answer: String, title: String? = null, timeout: Int = 5000) {
    waitForOptionPaneDialog(JOptionPane.QUESTION_MESSAGE, title, timeout = timeout)

    expectQuestionDialog(message, answer, title)
}

fun expectQuestionDialog(message: String, answer: String, title: String? = null) {
    expectOptionPaneDialog(JOptionPane.QUESTION_MESSAGE, message,  answer, title)
}

/**
 * Error
 */
fun waitForErrorDialog(message: String, title: String = "Error", timeout: Int = 5000) {
    waitForOptionPaneDialog(JOptionPane.ERROR_MESSAGE, title, timeout = timeout)

    expectErrorDialog(message, title)
}

fun expectErrorDialog(message: String, title: String? = null) {
    expectOptionPaneDialog(JOptionPane.ERROR_MESSAGE, message, "OK", title)
}

/**
 * Info
 */
fun waitForInfoDialog(message: String, title: String? = null, timeout: Int = 5000) {
    waitForOptionPaneDialog(JOptionPane.INFORMATION_MESSAGE, title, timeout = timeout)

    expectInfoDialog(message)
}
fun expectInfoDialog(message: String, title: String? = null) {
    expectOptionPaneDialog(JOptionPane.INFORMATION_MESSAGE, message, "OK", title)
}

private fun expectOptionPaneDialog(messageType: Int, message: String, buttonText: String, title: String? = null) {
    val dlg = getOptionPaneDialog(messageType, title)
    dlg.getDialogMessage() shouldBe message
    dlg.clickButton(text = buttonText)
}


fun selectFromOptionDialog(selection: String, title: String? = null) {
    val dialog = getOptionPaneDialog(JOptionPane.PLAIN_MESSAGE, title)
    dialog.clickButton(text = selection, async = true)
}

fun typeIntoInputDialog(text: String, title: String? = null) {
    val dlg = getOptionPaneDialog(JOptionPane.PLAIN_MESSAGE, title)
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

private fun getOptionPaneDialog(type: Int, title: String? = null) =
    getWindow<JDialog> { it.getChild<JOptionPane>().messageType == type && (title == null || it.title == title) && it.isVisible }

private fun waitForOptionPaneDialog(type: Int, title: String? = null, timeout: Int = 5000) {
    waitForAssertion(timeout) { getOptionPaneDialog(type, title) }
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

fun dismissDialog(title: String) {
    val dialog = getWindow<JDialog> { it.title == title && it.isVisible }
    dialog.dispose()
    flushEdt()
}

fun cancelDialog(title: String) {
    val dialog = getWindow<JDialog> { it.title == title && it.isVisible }
    dialog.clickCancel(async = true)
}