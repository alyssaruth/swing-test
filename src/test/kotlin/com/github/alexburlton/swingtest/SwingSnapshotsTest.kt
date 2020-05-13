package com.github.alexburlton.swingtest

import io.kotlintest.matchers.collections.shouldContainExactly
import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import org.junit.*
import org.junit.contrib.java.lang.system.EnvironmentVariables
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

    @Rule
    @JvmField
    val environmentVariables = EnvironmentVariables()

    @Before
    fun before() {
        environmentVariables.clear(ENV_SCREENSHOT_OS, ENV_UPDATE_SNAPSHOT)
        File(resourceLocation).deleteRecursively()
    }

    @After
    fun after() {
        File(resourceLocation).deleteRecursively()
    }

    @Test
    fun `Should fail if no snapshot already exists`() {
        val label = makeComponent()

        val exception = shouldThrow<AssertionError> {
            label.shouldMatchImage("Image")
        }

        exception.message shouldBe "Snapshot image not found: $resourceLocation/Image.png. Run with env var updateSnapshots=true to write for the first time."
        File("$resourceLocation/Image.png").shouldNotExist()
    }

    @Test
    fun `Should pass and write new snapshot if in updateSnapshots mode`() {
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")

        val label = makeComponent()

        shouldNotThrowAny {
            label.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
    }

    @Test
    fun `Should skip the test if on the wrong OS`() {
        environmentVariables.set(ENV_SCREENSHOT_OS, "invalid")
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")

        val label = makeComponent()

        val exception = shouldThrow<AssumptionViolatedException> {
            label.shouldMatchImage("Image")
        }

        exception.message shouldBe "Wrong OS for screenshot tests (wanted invalid, found $os)"
        File("$resourceLocation/Image.png").shouldNotExist()
    }

    @Test
    fun `Should not skip the test if OS matches`() {
        environmentVariables.set(ENV_SCREENSHOT_OS, os)
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")

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

        exception.message shouldBe "Snapshot image did not match: $resourceLocation/Image.png. Run with env var updateSnapshots=true to overwrite."

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
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")
        shouldMatchImage(filename)
        environmentVariables.clear(ENV_UPDATE_SNAPSHOT)
    }

    private fun makeComponent(text: String = "Label A") = JLabel(text).also { it.size = Dimension(200, 40) }
}