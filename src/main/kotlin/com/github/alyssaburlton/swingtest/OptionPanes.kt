package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JList
import javax.swing.text.JTextComponent

fun expectErrorDialog(message: String) {
    val error = getErrorDialog { it.isVisible }
    error.getDialogMessage() shouldBe message
    error.clickOk(async = true)
}

fun waitForErrorDialog(message: String) {
    waitForAssertion { findErrorDialog { it.isVisible } shouldNotBe null }

    expectErrorDialog(message)
}

fun expectInfoDialog(message: String) {
    val info = getInfoDialog()
    info.getDialogMessage() shouldBe message
    info.clickOk(async = true)
}

fun waitForInfoDialog(message: String) {
    waitForAssertion { findInfoDialog { it.isVisible } shouldNotBe null }

    expectInfoDialog(message)
}


private fun getInfoDialog() = findInfoDialog()!!

fun findInfoDialog(predicate: (window: JDialog) -> Boolean = { true }) =
    findOptionPaneDialog("Information", predicate)

fun expectQuestionDialog(message: String, answer: String) {
    val question = getQuestionDialog { it.isVisible }
    question.getDialogMessage() shouldBe message

    question.clickButton(text = answer)
}

fun getQuestionDialog(predicate: (window: JDialog) -> Boolean = { true }) = getOptionPaneDialog("Question", predicate)

fun findQuestionDialog() = findOptionPaneDialog("Question")

fun getErrorDialog(predicate: (window: JDialog) -> Boolean = { true }) =
    getOptionPaneDialog("Error", predicate)

fun findErrorDialog(predicate: (window: JDialog) -> Boolean = { true }) =
    findOptionPaneDialog("Error", predicate)

fun waitForQuestionDialog(): JDialog = waitForWindow<JDialog> { it.title == "Question" }

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

private fun findOptionPaneDialog(
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