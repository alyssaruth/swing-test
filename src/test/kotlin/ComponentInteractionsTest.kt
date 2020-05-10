
import io.mockk.MockKMatcherScope
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifySequence
import org.junit.Test
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JLabel

class ComponentInteractionsTest
{
    @Test
    fun `doClick should call mouseClicked and mouseReleased in order, on all listeners`()
    {
        val listenerOne = mockk<MouseListener>(relaxed = true)
        val listenerTwo = mockk<MouseListener>(relaxed = true)

        val label = JLabel()
        label.addMouseListener(listenerOne)
        label.addMouseListener(listenerTwo)

        label.doClick()

        verifySequence {
            listenerOne.mouseClicked(any())
            listenerOne.mouseReleased(any())
        }

        verifySequence {
            listenerTwo.mouseClicked(any())
            listenerTwo.mouseReleased(any())
        }
    }

    @Test
    fun `Should simulate a double click`()
    {
        val label = JLabel()
        val listener = mockk<MouseListener>(relaxed = true)

        label.addMouseListener(listener)
        label.doubleClick()

        verify { listener.mouseClicked(eventWithClickCount(2)) }
        verify { listener.mouseReleased(eventWithClickCount(2)) }
    }

    private fun MockKMatcherScope.eventWithClickCount(count: Int) = match<MouseEvent> {
        it.clickCount == count
    }
}