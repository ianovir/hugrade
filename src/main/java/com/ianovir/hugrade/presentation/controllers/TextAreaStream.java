package com.ianovir.hugrade.presentation.controllers;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;

public class TextAreaStream extends OutputStream {

    private TextArea console;

    public TextAreaStream(TextArea console) {
        this.console = console;
    }

    public void appendText(String valueOf) {
        Platform.runLater(() -> console.appendText(valueOf));
    }

    public void write(int b) {
        appendText(String.valueOf((char)b));
    }
}
