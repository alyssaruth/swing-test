package com.github.alyssaburlton.swingtest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;

public class SwingSnapshotsInteropTest {
    private final String resourceLocation = "src/test/resources/__snapshots__/com.github.alyssaburlton.swingtest.SwingSnapshotsInteropTest";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @AfterEach
    public void after() {
        File dir = new File(resourceLocation);
        File snapshotFile = new File(resourceLocation + "/Image.png");

        snapshotFile.delete();
        dir.delete();
    }

    @Test
    public void shouldBeCallableFromJava() {
        System.setProperty("updateSnapshots", "true");

        JLabel label = new JLabel("Label A");
        label.setSize(200, 40);

        SwingSnapshotsKt.shouldMatchImage(label, "Image");

        Assertions.assertTrue(new File(resourceLocation + "/Image.png").exists());
    }
}
