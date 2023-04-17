package com.github.alyssaburlton.swingtest

import com.github.romankh3.image.comparison.ImageComparison
import com.github.romankh3.image.comparison.ImageComparisonUtil
import com.github.romankh3.image.comparison.model.ImageComparisonState
import io.kotest.assertions.fail
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assumptions
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.Icon
import javax.swing.JComponent

const val ENV_UPDATE_SNAPSHOT = "updateSnapshots"
const val ENV_SCREENSHOT_OS = "screenshotOs"
private const val DEFAULT_WIDTH = 200
private const val DEFAULT_HEIGHT = 200

fun Icon.shouldMatch(other: Icon)
{
    toBufferedImage().isEqual(other.toBufferedImage()) shouldBe true
}
private fun Icon.toBufferedImage(): BufferedImage
{
    val bi = BufferedImage(iconWidth, iconHeight, BufferedImage.TYPE_INT_RGB)
    val g = bi.createGraphics()
    paintIcon(null, g, 0, 0)
    g.dispose()
    return bi
}

fun JComponent.toBufferedImage(): BufferedImage {
    val width = getWidthForSnapshot()
    val height = getHeightForSnapshot()
    
    val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
    val g2 = img.createGraphics()
    paint(g2)
    return img
}

private fun JComponent.getWidthForSnapshot(): Int = when {
    width > 0 -> width
    preferredSize.width > 0 -> preferredSize.width
    else -> DEFAULT_WIDTH
}

private fun JComponent.getHeightForSnapshot(): Int = when {
    height > 0 -> height
    preferredSize.height > 0 -> preferredSize.height
    else -> DEFAULT_HEIGHT
}

fun JComponent.shouldMatchImage(imageName: String) {
    verifyOs()

    val overwrite = System.getProperty(ENV_UPDATE_SNAPSHOT) == "true"
    val img = toBufferedImage()

    val callingSite = Throwable().stackTrace[1].className
    val imgPath = "src/test/resources/__snapshots__/$callingSite"

    val file = File("$imgPath/$imageName.png")
    if (!file.exists() && !overwrite) {
        fail("Snapshot image not found: $imgPath/$imageName.png. Run with system property -DupdateSnapshots=true to write for the first time.")
    }

    file.mkdirs()

    if (overwrite) {
        ImageIO.write(img, "png", file)
    } else {
        val savedImg = ImageIO.read(file)
        val result = ImageComparison(savedImg, img).compareImages()

        if (result.imageComparisonState != ImageComparisonState.MATCH) {
            val failedFile = File("$imgPath/$imageName.failed.png")
            ImageIO.write(img, "png", failedFile)

            val comparisonFile = File("$imgPath/$imageName.comparison.png")
            ImageIO.write(result.result, "png", comparisonFile)

            fail("Snapshot image did not match: $imgPath/$imageName.png. Run with system property -DupdateSnapshots=true to overwrite.")
        }
    }
}

private fun verifyOs() {
    val osForScreenshots = (System.getProperty(ENV_SCREENSHOT_OS) ?: "").lowercase()

    val os = System.getProperty("os.name").lowercase()
    if (osForScreenshots.isNotEmpty()) {
        Assumptions.assumeTrue(os.contains(osForScreenshots),
            "Wrong OS for screenshot tests (wanted $osForScreenshots, found $os)"
        )
    }
}

fun BufferedImage.isEqual(other: BufferedImage): Boolean {
    if (width != other.width || height != other.height) return false
    return getPointList(width, height).all { getRGB(it.x, it.y) == other.getRGB(it.x, it.y) }
}

fun getPointList(width: Int, height: Int): List<Point> {
    val yRange = 0 until height
    val xRange = 0 until width

    return yRange.map { y -> xRange.map { x -> Point(x, y) } }.flatten()
}
