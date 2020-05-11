import org.junit.Test;

import javax.swing.*;
import java.awt.event.MouseListener;

import static org.mockito.Mockito.*;

public class ComponentInteractionsInteropTest {
    @Test
    public void doClick() {
        JLabel label = new JLabel();

        MouseListener listener = mock(MouseListener.class);
        label.addMouseListener(listener);

        ComponentInteractionsKt.doClick(label);

        verify(listener).mouseClicked(any());
    }

    @Test
    public void doubleClick() {
        JLabel label = new JLabel();

        MouseListener listener = mock(MouseListener.class);
        label.addMouseListener(listener);

        ComponentInteractionsKt.doubleClick(label);

        verify(listener).mouseClicked(any());
    }
}
