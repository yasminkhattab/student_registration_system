package com.studentregistration.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

import java.util.Optional;

/**
 * Small helper that keeps alert dialogs consistent across controllers.
 */
public final class AlertHelper {

    private AlertHelper() {
        // Utility class: no instances.
    }

    /**
     * Shows an information dialog.
     *
     * @param owner   window that owns the dialog (may be {@code null})
     * @param title   dialog title
     * @param message message body
     */
    public static void showInfo(Window owner, String title, String message) {
        show(owner, Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Shows an error dialog.
     *
     * @param owner   window that owns the dialog (may be {@code null})
     * @param title   dialog title
     * @param message message body
     */
    public static void showError(Window owner, String title, String message) {
        show(owner, Alert.AlertType.ERROR, title, message);
    }

    /**
     * Asks the user to confirm a destructive action.
     *
     * @param owner   window that owns the dialog (may be {@code null})
     * @param title   dialog title
     * @param message message body
     * @return {@code true} when the user confirms
     */
    public static boolean confirm(Window owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null);
        initOwner(alert, owner);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Shows a non-blocking fatal error with the stack trace in expandable text.
     *
     * @param owner   window that owns the dialog (may be {@code null})
     * @param title   dialog title
     * @param message short description
     * @param details technical details, usually {@code exception.printStackTrace()} output
     */
    public static void showFatal(Window owner, String title, String message, String details) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(message);
        if (details != null && !details.isBlank()) {
            TextArea area = new TextArea(details);
            area.setEditable(false);
            area.setWrapText(true);
            area.setPrefColumnCount(60);
            area.setPrefRowCount(12);
            alert.getDialogPane().setExpandableContent(area);
        }
        initOwner(alert, owner);
        alert.showAndWait();
    }

    private static void show(Window owner, Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.getDialogPane().setContent(new Label(message));
        alert.getDialogPane().setMinWidth(420);
        initOwner(alert, owner);
        alert.showAndWait();
    }

    /**
     * Attaches a dialog to an owner only when that owner is displayable.
     *
     * <p>A {@code Stage} that has no scene yet cannot own a dialog; asking it to
     * do so throws a {@link NullPointerException} inside JavaFX. This happens
     * when a screen fails during start-up, so the owner is skipped instead of
     * letting the error dialog hide the original problem.</p>
     */
    private static void initOwner(Alert alert, Window owner) {
        if (owner != null && owner.getScene() != null) {
            alert.initOwner(owner);
        }
    }
}
