import javax.swing.SwingUtilities

fun flushEdt() {
    val lambda = {}
    SwingUtilities.invokeAndWait(lambda)
}