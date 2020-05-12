import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;

import javax.swing.*;
import java.io.File;

public class SwingSnapshotsInteropTest {
    private String resourceLocation = "src/test/resources/__snapshots__/SwingSnapshotsInteropTest";

    @Rule
    public EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @After
    public void after() {
        File dir = new File(resourceLocation);
        File snapshotFile = new File(resourceLocation + "/Image.png");

        snapshotFile.delete();
        dir.delete();
    }

    @Test
    public void shouldBeCallableFromJava() {
        environmentVariables.set("updateSnapshots", "true");

        JLabel label = new JLabel("Label A");
        label.setSize(200, 40);

        SwingSnapshotsKt.shouldMatchImage(label, "Image");

        Assert.assertTrue(new File(resourceLocation + "/Image.png").exists());
    }
}
