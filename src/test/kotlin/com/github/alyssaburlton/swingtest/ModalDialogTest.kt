package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.JOptionPane
import javax.swing.JPanel

class ModalDialogTest {

    @Test
    fun `Should be able to use async to handle modal dialogs`() {
        val scrn = TestScreen()
        scrn.getChild<JLabel>("answer-label").text shouldBe ""

        scrn.clickChild<JButton>(text = "Ask Question", async = true)

        val window = findWindow<JDialog> { it.title == "Question" }!!
        window.clickChild<JButton>(text = "Yes")
        flushEdt()

        scrn.getChild<JLabel>("answer-label").text shouldBe "Yes"
    }

    class TestScreen : JPanel(), ActionListener {
        private val button = JButton("Ask Question")
        private val label = JLabel("")

        init {
            add(button)
            add(label)

            label.name = "answer-label"

            button.addActionListener(this)
        }

        override fun actionPerformed(p0: ActionEvent?) {
            val response = JOptionPane.showConfirmDialog(
                this, "Do you like cheese?", "Question", JOptionPane.YES_NO_OPTION
            )

            if (response == JOptionPane.YES_OPTION) {
                label.text = "Yes"
            } else {
                label.text = "No"
            }
        }
    }
}
