package io.github.alyssaruth.swingtest

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.io.File
import javax.swing.JFileChooser

@ExtendWith(SwingTestCleanupExtension::class)
class FileChooserTest {
    @Test
    fun `Should be able to open a specified file`() {
        val fc = JFileChooser()
        runAsync { fc.showOpenDialog(null) }

        val path = javaClass.getResource("/images/Hornet.png")!!.path
        selectFile(path, "Open")

        fc.selectedFile.path shouldBe path
    }

    @Test
    fun `Should be able to select a specified directory`() {
        val fc = JFileChooser()
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        runAsync { fc.showDialog(null, "Select a directory") }

        val path = File(javaClass.getResource("/images/Hornet.png")!!.path).parentFile.path
        selectFile(path, "Select a directory")

        fc.selectedFile.path shouldBe path
    }

    @Test
    fun `Should error if title is not as specified`() {
        val fc = JFileChooser()
        runAsync { fc.showOpenDialog(null) }

        val path = javaClass.getResource("/images/Hornet.png")!!.path

        val err = shouldThrow<AssertionError> {
            selectFile(path, "Other title")
        }

        err.message shouldContain "Window not found for predicate"
    }

    @Test
    fun `Should not find other dialogs`() {
        val launcher = OptionPaneLauncher()
        launcher.clickButton(text = "TextInput")

        val path = javaClass.getResource("/images/Hornet.png")!!.path

        val err = shouldThrow<AssertionError> {
            selectFile(path)
        }

        err.message shouldContain "Window not found for predicate"
    }
}