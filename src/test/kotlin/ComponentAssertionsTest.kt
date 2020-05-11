import io.kotlintest.shouldNotThrowAny
import io.kotlintest.shouldThrow
import org.junit.Test
import javax.swing.JLabel

class ComponentAssertionsTest {
    @Test
    fun `shouldBeEnabled should verify whether a component is enabled`() {
        val label = JLabel()
        label.isEnabled = true

        shouldNotThrowAny {
            label.shouldBeEnabled()
        }

        label.isEnabled = false
        shouldThrow<AssertionError> {
            label.shouldBeEnabled()
        }
    }

    @Test
    fun `shouldBeDisabled should verify whether a component is disabled`() {
        val label = JLabel()
        label.isEnabled = false

        shouldNotThrowAny {
            label.shouldBeDisabled()
        }

        label.isEnabled = true
        shouldThrow<AssertionError> {
            label.shouldBeDisabled()
        }
    }

    @Test
    fun `shouldBeVisible should verify whether a component is visible`() {
        val label = JLabel()
        label.isVisible = true

        shouldNotThrowAny {
            label.shouldBeVisible()
        }

        label.isVisible = false
        shouldThrow<AssertionError> {
            label.shouldBeVisible()
        }
    }

    @Test
    fun `shouldNotBeVisible should verify whether a component is not visible`() {
        val label = JLabel()
        label.isVisible = false

        shouldNotThrowAny {
            label.shouldNotBeVisible()
        }

        label.isVisible = true
        shouldThrow<AssertionError> {
            label.shouldNotBeVisible()
        }
    }
}