package com.schooltracker.proyectofinal.View;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar extends HBox {

    private final Label titleLabel;
    private final TextField searchField;
    private final HBox actionsBox;
    private final QuickButton themeToggleButton;
    private final QuickButton weekView;
    private final QuickButton calendarView;
    private final QuickButton dayView;

    private boolean isDarkMode = true;

    public NavBar(String appTitle) {
        // --- 1. Basic HBox Setup ---
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(15);
        this.setMaxWidth(Double.MAX_VALUE);
        this.getStyleClass().add("nav-bar");

        // --- 2. Title Section ---
        this.titleLabel = new Label(appTitle);
        this.titleLabel.getStyleClass().add("nav-title");

        // --- 3. Functions ---
        this.searchField = new TextField();
        this.searchField.setPromptText("Search...");
        this.searchField.getStyleClass().add("nav-search");
        this.searchField.setPrefWidth(220);

        this.weekView = new QuickButton("Week");

        this.dayView = new QuickButton("Day");

        this.calendarView = new QuickButton("Calendar");

        // --- 4. Spacer ---
        // Pushes everything after it to the far right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- 5. Custom Actions Zone ---
        this.actionsBox = new HBox(10);
        this.actionsBox.setAlignment(Pos.CENTER);

        // --- 6. Theme Toggle Button ---
        this.themeToggleButton = new QuickButton("🌙");

        // --- 7. Assemble Children ---
        this.getChildren().addAll(
                titleLabel,
                dayView,
                weekView,
                calendarView,
                spacer,
                actionsBox,
                themeToggleButton
        );
    }

    // ==========================================
    // Framework Helper API Methods
    // ==========================================

    /**
     * Set or change the title displayed in the NavBar.
     */
    public void setTitle(String title) {
        this.titleLabel.setText(title);
    }

    /**
     * Add a custom action button to the right side of the navbar.
     */
    public void addAction(Button button) {
        button.getStyleClass().add("nav-action-button");
        this.actionsBox.getChildren().add(button);
    }

    /**
     * Set a listener when the search field text changes.
     */
    public void onSearch(java.util.function.Consumer<String> searchConsumer) {
        this.searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchConsumer.accept(newValue);
        });
    }

    /**
     * Simple theme toggle handler.
     */
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            this.themeToggleButton.setText("🌙");
            this.getStyleClass().remove("light-theme");
        } else {
            this.themeToggleButton.setText("☀️");
            if (!this.getStyleClass().contains("light-theme")) {
                this.getStyleClass().add("light-theme");
            }
        }
    }
}