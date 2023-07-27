package com.github.alyssaburlton.swingtest

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.awt.BorderLayout
import java.io.File
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JDialog
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.SwingUtilities
import javax.swing.table.DefaultTableModel

@ExtendWith(SwingTestCleanupExtension::class)
class SwingDebugTest {
    @Test
    fun `Should be able to dump a simple component tree`() {
        val frame = JFrame()
        frame.name = "MyFrame"
        frame.title = "A Window"
        val panel = JPanel()
        frame.contentPane = panel
        panel.layout = BorderLayout(0, 0)
        val buttonPanel = JPanel()
        buttonPanel.add(JButton("Ok"))
        buttonPanel.add(JButton("Cancel"))
        panel.add(buttonPanel, BorderLayout.SOUTH)

        val northPanel = JPanel()
        northPanel.add(JLabel("Filter:"))
        val combo = JComboBox<String>()
        combo.addItem("One")
        combo.addItem("Two")
        northPanel.add(combo)
        panel.add(northPanel, BorderLayout.NORTH)

        val scrollPane = JScrollPane()
        val table = JTable()

        val model = DefaultTableModel()
        model.addColumn("Index")
        model.addColumn("Name")
        model.addColumn("Value")
        model.addRow(arrayOf(1, "One", "1"))
        model.addRow(arrayOf(2, "Two", "2"))
        table.model = model

        scrollPane.setViewportView(table)
        panel.add(scrollPane, BorderLayout.CENTER)

        val tree = frame.generateComponentTree()
        tree shouldBe """
            JFrame - "A Window" - BorderLayout [name: MyFrame]
            |- [Center] JRootPane - RootLayout
              |- JPanel - FlowLayout [name: null.glassPane]
              |- JLayeredPane - null [name: null.layeredPane]
                |- JPanel - BorderLayout
                  |- [South] JPanel - FlowLayout
                    |- JButton - "Ok"
                    |- JButton - "Cancel"
                  |- [North] JPanel - FlowLayout
                    |- JLabel - "Filter:"
                    |- JComboBox<String> - 2 items (Selected: One)
                  |- [Center] JScrollPane - UIResource
                    |- JViewport - ViewportLayout
                      |- JTable - [Index, Name, Value] - 2 rows
                    |- ScrollBar - VERTICAL
                    |- ScrollBar - HORIZONTAL

        """.trimIndent()
    }

    @Test
    fun `Should be able to dump a useful component tree for a file selector`() {
        val panel = JPanel()
        val directory = File("/")
        val chooser = JFileChooser(directory)
        SwingUtilities.invokeLater { chooser.showOpenDialog(panel) }
        flushEdt()

        val window = findWindow<JDialog> { it.title == "Open" }!!
        val tree = window.generateComponentTree()
        tree shouldBe """
            JDialog - "Open" - BorderLayout [name: dialog0]
            |- [Center] JRootPane - RootLayout
              |- JPanel - FlowLayout [name: null.glassPane]
              |- JLayeredPane - null [name: null.layeredPane]
                |- JPanel - BorderLayout [name: null.contentPane]
                  |- [Center] JFileChooser - BorderLayout
                    |- [North] JPanel - BorderLayout
                      |- [After] JPanel - BoxLayout
                        |- JButton - "null" (ToolTip: "Up One Level")
                        |- Filler - null
                        |- JButton - "" (ToolTip: "Home")
                        |- Filler - null
                        |- JButton - "null" (ToolTip: "Create New Folder")
                        |- Filler - null
                        |- JToggleButton - "" (ToolTip: "List")
                        |- JToggleButton - "" (ToolTip: "Details")
                      |- [Before] JLabel - "Look In:"
                      |- [Center]  JComboBox<File> - 1 items (Selected: /)
                    |- [After] JPanel - BorderLayout
                    |- [Center] FilePane - BorderLayout
                      |- [Center] JPanel - BorderLayout
                        |- [Center] JScrollPane - UIResource
                          |- JViewport - ViewportLayout
                            |- sun.swing.FilePane${'$'}4 - null
                              |- CellRendererPane - null
                          |- ScrollBar - VERTICAL
                          |- ScrollBar - HORIZONTAL
                    |- [South] JPanel - BoxLayout
                      |- JPanel - BoxLayout
                        |- AlignedLabel - "File Name:"
                        |- javax.swing.plaf.metal.MetalFileChooserUI${'$'}3 - UpdateHandler
                      |- Filler - null
                      |- JPanel - BoxLayout
                        |- AlignedLabel - "Files of Type:"
                        |- JComboBox<AcceptAllFileFilter> - 1 items (Selected: javax.swing.plaf.basic.BasicFileChooserUI${'$'}AcceptAllFileFilter)
                      |- JPanel - ButtonAreaLayout
                        |- JButton - "Open" (ToolTip: "Open selected file")
                        |- JButton - "Cancel" (ToolTip: "Abort file chooser dialog")

        """.trimIndent()
    }
}
