package com.github.alexburlton.swingtest

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.opentest4j.TestAbortedException
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JComponent
import javax.swing.JLabel

class SwingSnapshotsTest {
    private val resourceLocation =
        "src/test/resources/__snapshots__/com.github.alexburlton.swingtest.SwingSnapshotsTest"
    private val os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH)

    @BeforeEach
    fun before() {
        System.clearProperty(ENV_SCREENSHOT_OS)
        System.clearProperty(ENV_UPDATE_SNAPSHOT)
        File(resourceLocation).deleteRecursively()
    }

    @AfterEach
    fun after() {
        File(resourceLocation).deleteRecursively()
    }

    @Test
    fun `Should fail if no snapshot already exists`() {
        val label = makeComponent()

        val exception = shouldThrow<AssertionError> {
            label.shouldMatchImage("Image")
        }

        exception.message shouldBe "Snapshot image not found: $resourceLocation/Image.png. Run with system property -DupdateSnapshots=true to write for the first time."
        File("$resourceLocation/Image.png").shouldNotExist()
    }

    @Test
    fun `Should pass and write new snapshot if in updateSnapshots mode`() {
        System.setProperty(ENV_UPDATE_SNAPSHOT, "true")

        val label = makeComponent()

        shouldNotThrowAny {
            label.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
    }

    @Test
    fun `Should skip the test if on the wrong OS`() {
        System.setProperty(ENV_SCREENSHOT_OS, "invalid")
        System.setProperty(ENV_UPDATE_SNAPSHOT, "true")

        val label = makeComponent()

        val exception = shouldThrow<TestAbortedException> {
            label.shouldMatchImage("Image")
        }

        exception.message shouldBe "Assumption failed: Wrong OS for screenshot tests (wanted invalid, found $os)"
        File("$resourceLocation/Image.png").shouldNotExist()
    }

    @Test
    fun `Should not skip the test if OS matches`() {
        System.setProperty(ENV_SCREENSHOT_OS, os)
        System.setProperty(ENV_UPDATE_SNAPSHOT, "true")

        val label = makeComponent()

        shouldNotThrowAny {
            label.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
    }

    @Test
    fun `Should fail and write out the correct comparison file`() {
        val comp = makeComponent("Label A")
        comp.createImageFile("Image")

        val otherLabel = makeComponent("Label B")

        val exception = shouldThrow<AssertionError> {
            otherLabel.shouldMatchImage("Image")
        }

        exception.message shouldBe "Snapshot image did not match: $resourceLocation/Image.png. Run with system property -DupdateSnapshots=true to overwrite."

        val originalFile = File("$resourceLocation/Image.png")
        val failedFile = File("$resourceLocation/Image.failed.png")

        originalFile.shouldExist()
        failedFile.shouldExist()

        ImageIO.read(originalFile).isEqual(comp.toBufferedImage()) shouldBe true
        ImageIO.read(failedFile).isEqual(otherLabel.toBufferedImage()) shouldBe true
    }

    @Test
    fun `Should succeed if snapshots genuinely match`() {
        val comp = makeComponent("Label A")
        comp.createImageFile("Image")

        val otherLabel = makeComponent("Label A")
        shouldNotThrowAny {
            otherLabel.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
        File("$resourceLocation/Image.failed.png").shouldNotExist()
    }

    @Test
    fun `Should write a snapshot of the correct size`() {
        val labelA = JLabel("A")
        labelA.size = Dimension(500, 15)

        val labelB = JLabel("B")
        labelB.preferredSize = Dimension(400, 25)

        val labelC = JLabel("C")
        labelC.preferredSize = Dimension(0, 0)

        labelA.createImageFile("A")
        labelB.createImageFile("B")
        labelC.createImageFile("C")

        val fileA = File("$resourceLocation/A.png")
        val fileB = File("$resourceLocation/B.png")
        val fileC = File("$resourceLocation/C.png")

        ImageIO.read(fileA).width shouldBe 500
        ImageIO.read(fileA).height shouldBe 15

        ImageIO.read(fileB).width shouldBe 400
        ImageIO.read(fileB).height shouldBe 25

        ImageIO.read(fileC).width shouldBe 200
        ImageIO.read(fileC).height shouldBe 200
    }

    @Test
    fun `Should generate a list of points from a width and height`() {
        val points = getPointList(3, 2)
        points.shouldContainExactly(Point(0, 0), Point(1, 0), Point(2, 0), Point(0, 1), Point(1, 1), Point(2, 1))
    }

    @Test
    fun `Should compare buffered images pixel by pixel`() {
        val colors = List(25) { Color.RED.rgb }.toIntArray()

        val img = BufferedImage(5, 5, BufferedImage.TYPE_4BYTE_ABGR)
        img.setRGB(0, 0, 5, 5, colors, 0, 5)

        val img2 = BufferedImage(5, 5, BufferedImage.TYPE_4BYTE_ABGR)
        img2.setRGB(0, 0, 5, 5, colors, 0, 5)
        img.isEqual(img2) shouldBe true

        img2.setRGB(0, 0, Color.GREEN.rgb)
        img.isEqual(img2) shouldBe false

        img.setRGB(0, 0, Color.GREEN.rgb)
        img.isEqual(img2) shouldBe true
    }

    private fun JComponent.createImageFile(filename: String) {
        System.setProperty(ENV_UPDATE_SNAPSHOT, "true")
        shouldMatchImage(filename)
        System.clearProperty(ENV_UPDATE_SNAPSHOT)
    }

    private fun makeComponent(text: String = "Label A") = JLabel(text).also { it.size = Dimension(200, 40) }
}