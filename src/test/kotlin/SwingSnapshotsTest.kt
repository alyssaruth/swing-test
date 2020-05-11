import io.kotlintest.matchers.file.shouldExist
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.EnvironmentVariables
import java.awt.Dimension
import java.io.File
import javax.swing.JLabel


class SwingSnapshotsTest {

    private val resourceLocation = "src/test/resources/__snapshots__/SwingSnapshotsTest"

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
}