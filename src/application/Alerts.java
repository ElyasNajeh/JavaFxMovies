package application;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

public class Alerts {

	public boolean ConfiramtionAlert(String title, String messege) {
		// Returns true if user clicks OK
		Alert a = new Alert(AlertType.CONFIRMATION);
		a.setTitle(title);
		a.setContentText(messege);
		a.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		a.getDialogPane().getStyleClass().add("custom-alert");
		Optional<ButtonType> res = a.showAndWait();
		return res.isPresent() && res.get() == ButtonType.OK;
	}

	// Shows an error alert
	public void ErrorAlert(String title, String messege) {
		Alert a = new Alert(AlertType.ERROR);
		a.setTitle(title);
		a.setContentText(messege);
		a.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		a.getDialogPane().getStyleClass().add("custom-alert");
		a.showAndWait();
	}

	// Shows an information alert
	public void InfoAlert(String title, String messege) {
		Alert a = new Alert(AlertType.INFORMATION);
		a.setTitle(title);
		a.setContentText(messege);
		a.getDialogPane().getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		a.getDialogPane().getStyleClass().add("custom-alert");
		a.showAndWait();

	}

}
