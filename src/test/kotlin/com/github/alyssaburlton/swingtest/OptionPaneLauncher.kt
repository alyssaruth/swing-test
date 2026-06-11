package com.github.alyssaburlton.swingtest

import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JOptionPane
import javax.swing.JPanel

class OptionPaneLauncher(): JPanel(), ActionListener {
    private val btnQuestion = JButton("Question")
    private val btnInfo = JButton("Info")
    private val btnError = JButton("Error")
    private val btnTextInput = JButton("TextInput")
    private val btnComboInput = JButton("ComboInput")
    private val btnListInput = JButton("ListInput")

    var result: Any? = null

    init {
        addButton(btnQuestion)
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
}