import io.kotlintest.fail
import org.junit.Assume
import java.awt.Point
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import javax.swing.JComponent

const val ENV_UPDATE_SNAPSHOT = "updateSnapshots"
const val ENV_SCREENSHOT_OS = "screenshotOs"

fun JComponent.shouldMatchImage(imageName: String) {
    val overwrite = System.getenv(ENV_UPDATE_SNAPSHOT) == "true"
    val osForScreenshots = (System.getenv(ENV_SCREENSHOT_OS) ?: "").toLowerCase(Locale.ENGLISH)

    val os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH)
    if (osForScreenshots.isNotEmpty()) {
        Assume.assumeTrue("Wrong OS for screenshot tests: $os", os.contains(osForScreenshots))
    }

    val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
    val g2 = img.createGraphics()
    paint(g2)

    val callingSite = Throwable().stackTrace[1].className
    val imgPath = "src/test/resources/__snapshots__/$callingSite"

    val file = File("$imgPath/$imageName.png")
    if (!file.exists() && !overwrite) {
        fail("Snapshot image not found: ${file.path}. Run with env var updateSnapshots=true to write for the first time.")
    }

    file.mkdirs()

    if (overwrite) {
        ImageIO.write(img, "png", file)
    } else {
        val savedImg = ImageIO.read(file)
        val match = img.isEqual(savedImg)
        if (!match) {
            val failedFile = File("$imgPath/$imageName.failed.png")
            ImageIO.write(img, "png", failedFile)
            fail("Snapshot image did not match: ${file.path}. Run with env var updateSnapshots=true to overwrite.")
        }
    }
}

private fun BufferedImage.isEqual(other: BufferedImage): Boolean {
    if (width != other.width || height != other.height) return false
    return getPointList(width, height).all { getRGB(it.x, it.y) == other.getRGB(it.x, it.y) }
}

private fun getPointList(width: Int, height: Int): List<Point> {
    val yRange = 0 until height
    val xRange = 0 until width

    return yRange.map { y -> xRange.map { x -> Point(x, y) } }.flatten()
}