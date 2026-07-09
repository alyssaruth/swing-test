package io.github.alyssaruth.swingtest

import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.text.JTextComponent

/**
 * Expect a JDialog with a JFileChooser child to be present. Select the specified path and confirm.
 *
 * @param path - the desired path to choose
 * @param title (optional) - the expected title of the dialog
 */
fun selectFile(path: String, title: String? = null) {
    val chooserDialog = getWindow<JDialog> { it.findChild<JFileChooser>() != null && it.title == title }
    chooserDialog.getChild<JTextComponent>().typeText(path)
    chooserDialog.clickChild<JButton>(text = title)
    flushEdt()
}
