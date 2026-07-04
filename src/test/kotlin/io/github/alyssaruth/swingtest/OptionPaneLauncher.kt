package io.github.alyssaruth.swingtest

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel

val TEST_OPTIONS = arrayOf("Tea", "Coffee", "Beer")

class OptionPaneLauncher(): JPanel(), ActionListener {
    private val btnQuestion = JButton("Question")
    private val btnCustomQuestion = JButton("Custom Question")
    private val btnInfo = JButton("Info")
    private val btnError = JButton("Error")
    private val btnTextInput = JButton("TextInput")
    private val btnComboInput = JButton("ComboInput")
    private val btnListInput = JButton("ListInput")

    var result: Any? = null

    init {
        addButton(btnQuestion)
        addButton(btnCustomQuestion)
        addButton(btnInfo)
        addButton(btnError)
        addButton(btnTextInput)
        addButton(btnComboInput)
        addButton(btnListInput)
    }

    private fun addButton(button: JButton) {
        add(button)
        button.addActionListener(this)
    }

    override fun actionPerformed(e: ActionEvent) {
        when (e.source) {
            btnQuestion -> launchQuestion()
            btnCustomQuestion -> launchCustomQuestion()
            btnError -> launchError()
            btnInfo -> launchInfo()
            btnTextInput -> launchTextInput()
            btnComboInput -> launchComboInput()
            btnListInput -> launchListInput()
        }
    }

    private fun launchQuestion() {
        result = JOptionPane.showConfirmDialog(
            parent,
            "Would you like a cup of tea?",
            "Question",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
        )
    }

    private fun launchCustomQuestion() {
        result = JOptionPane.showOptionDialog(
            parent,
            "What would you like to drink?",
            "Question",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            TEST_OPTIONS,
            "Beer"
        )
    }

    private fun launchError() {
        result = JOptionPane.showMessageDialog(parent, "Something went wrong", "Error", JOptionPane.ERROR_MESSAGE)
    }

    private fun launchInfo() {
        result = JOptionPane.showMessageDialog(
            parent,
            "This is a message.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE,
        )
    }

    private fun launchTextInput() {
        result =
            JOptionPane.showInputDialog(
                parent,
                "Enter some text",
                "Input",
                JOptionPane.PLAIN_MESSAGE,
            )
    }

    private fun launchComboInput() {
        result =
            JOptionPane.showInputDialog(
                parent,
                "Favourite Square?",
                "Input",
                JOptionPane.PLAIN_MESSAGE,
                null,
                arrayOf(1, 4, 9, 16, 25, 36, 49, 64),
                1,
            )
    }

    private fun launchListInput() {
        result =
            JOptionPane.showInputDialog(
                parent,
                "Pick a number",
                "Input",
                JOptionPane.PLAIN_MESSAGE,
                null,
                (1..100).toList().toTypedArray(),
                7,
            )
    }
}