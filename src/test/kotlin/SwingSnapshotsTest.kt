import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.matchers.file.shouldNotExist
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import org.junit.*
import org.junit.contrib.java.lang.system.EnvironmentVariables
import java.awt.Dimension
import java.io.File
import java.util.*
import javax.swing.JLabel


class SwingSnapshotsTest {

    private val resourceLocation = "src/test/resources/__snapshots__/SwingSnapshotsTest"
    private val os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH)

    @Rule
    @JvmField
    val environmentVariables = EnvironmentVariables()

    @Before
    fun before() {
        environmentVariables.clear()
        File(resourceLocation).deleteRecursively()
    }

    @After
    fun after() {
        File(resourceLocation).deleteRecursively()
    }

    @Test
    fun `Should fail if no snapshot already exists`() {
        val label = JLabel("Label A")
        label.size = Dimension(200, 40)

        val exception = shouldThrow<AssertionError> {
            label.shouldMatchImage("Image")
        }

        exception.message shouldBe "Snapshot image not found: $resourceLocation/Image.png. Run with env var updateSnapshots=true to write for the first time."
        File("$resourceLocation/Image.png").shouldNotExist()
    }

    @Test
    fun `Should pass and write new snapshot if in updateSnapshots mode`() {
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")

        val label = JLabel("Label A")
        label.size = Dimension(200, 40)

        shouldNotThrowAny {
            label.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
    }

    @Test
    fun `Should skip the test if on the wrong OS`() {
        environmentVariables.set(ENV_SCREENSHOT_OS, "invalid")
        environmentVariables.set(ENV_UPDATE_SNAPSHOT, "true")

        val label = JLabel("Label A")
        label.size = Dimension(200, 40)

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

        val label = JLabel("Label A")
        label.size = Dimension(200, 40)

        shouldNotThrowAny {
            label.shouldMatchImage("Image")
        }

        File("$resourceLocation/Image.png").shouldExist()
    }
}