# swing-test
[![Build Status](https://github.com/alexburlton/swing-test/workflows/build/badge.svg)](https://github.com/alexburlton/swing-test/actions)
 [ ![Download](https://api.bintray.com/packages/alexburlton/swing-test/swing-test/images/download.svg) ](https://bintray.com/alexburlton/swing-test/swing-test/_latestVersion)
 [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
 
swing-test is an idiomatic testing library for Java Swing components, developed in [Kotlin](https://kotlinlang.org/)

Test components with ease
-------------------------

Write simple assertions for Swing components, using the `StringSpec` style. and inspired by [kotest](https://github.com/kotest/kotest):

```kotlin
    @Test
    fun `Should enable Ok button once terms have been read`() {
        val form = MyForm()
        form.getChild<JButton>("Ok").shouldBeDisabled()
        form.clickChild<JCheckBox>("I have read the terms and conditions")
        form.getChild<JButton>("Ok").shouldBeEnabled()
    }
```

Screenshot Testing :camera_flash:
---------------------------------



Fully interoperable with Java
-----------------------------

Although swing-test is developed with Kotlin in mind, it fully supports raw Java projects too:

```
Component myComponent = new CustomComponent();
SwingSnapshotsKt.shouldMatchImage(myComponent, "Default");

List<JButton> buttons = ComponentFindersKt.findAll(panel, JButton.class);

ComponentInteractionsKt.doHover(myComponent);
```