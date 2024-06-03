package edu.uwi.soscai.config;

import java.text.DecimalFormat;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

public class Components {
    public static void bindSliderToLabel(Slider slider, Label label, String format) {
        // Listener to update label text when slider value changes
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            label.setText(String.format(format, newValue)); // Update label text with slider value formatted to one
                                                            // decimal place
        });

        // Set initial label text
        label.setText(String.format(format, slider.getValue()));
    }

    public static void bindSliderToTextField(Slider slider, TextField textField) {
        // Create a text formatter to intercept input and parse it into double
        TextFormatter<Double> textFormatter = new TextFormatter<>(new StringConverter<Double>() {
            final DecimalFormat format = new DecimalFormat("#.0");

            @Override
            public String toString(Double value) {
                return (value != null) ? format.format(value) : "";
            }

            @Override
            public Double fromString(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    return slider.getValue(); // Return slider's current value if parsing fails
                }
            }
        });

        // Bind the text formatter to the text field
        textField.setTextFormatter(textFormatter);

        // Listener to update slider value when text field input changes
        textFormatter.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                slider.setValue(Math.round(newValue)); // Round the value
            }
        });

        // Listener to update text field text when slider value changes
        slider.valueProperty().addListener((obs, oldValue, newValue) -> {
            textField.setText(String.format("%d.0", Math.round(newValue.doubleValue()))); // Round the value
        });

        // Set initial text
        textField.setText(String.format("%d.0", Math.round(slider.getValue()))); // Round the value
    }

}
