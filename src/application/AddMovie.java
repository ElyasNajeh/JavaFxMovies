package application;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class AddMovie {
	// Reference to main movie list and catalog
	ObservableList<Movie> movieList = Main.movieList;
	TableView<Movie> movieTable = Main.movieTable;
	MovieCatalog movieCatalog = Main.movieCatalog;
	IconButton backButton, addButton, clearButton;
	Label titleLabel, descriptionLabel, yearLabel, ratingLabel;
	TextField titleField, ratingField;
	TextArea descripField;
	DatePicker dateField;
	Alerts alerts = new Alerts();

	// Clear all input fields
	public void clear() {
		titleField.clear();
		descripField.clear();
		ratingField.clear();
		dateField.setValue(null);
	}

	// Add button click to add movie
	public void addButton() {
		String movieTitle = titleField.getText();
		String movieDescription = descripField.getText();
		String rating = ratingField.getText();
		if (rating == null || rating.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Movie Rating cant be Empty");
			return;
		}
		if (!rating.matches("\\d+")) {
			alerts.ErrorAlert("Error", "Movie Rating Can be Only Numbers");
			return;

		}
		if (dateField.getValue() == null) {
			alerts.ErrorAlert("Error", "Movie Date cant be empty");
			return;
		}

		// Check if the selected date is not in the future
		String selectedDate = dateField.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (selectedDate.compareTo(todayDate) > 0) {
			alerts.ErrorAlert("Error", "Movie date cant be in the future");
			return;
		}
		int selectedYear = dateField.getValue().getYear();
		Double movieRating = Double.parseDouble(rating);

		// validation for title, description, and rating range
		boolean isValid = validation(movieTitle, movieDescription, movieRating);
		if (!isValid) {
			return;
		}

		Movie addMovie = new Movie(movieTitle, movieDescription, selectedYear, movieRating);

		// Confirm before adding
		boolean confirmation = alerts.ConfiramtionAlert("Info", "Are You sure you Want to Add This Movie ?");
		if (!confirmation) {
			return;
		}
		// Insert into the movie catalog
		boolean add = movieCatalog.insert(addMovie);
		if (add == true) {
			movieList.add(addMovie);
			alerts.InfoAlert("Success", "Movie Added Succesfully,Thanks");
			clear();

		} else {
			alerts.ErrorAlert("Error", "This Movie is Already Exists ,Please Enter Another Movie Title to Add");
		}

	}
	// Validates title, description, and rating range

	public boolean validation(String title, String description, Double rating) {
		if (title == null || title.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Movie title Cant be Empty");
			return false;
		}
		if (!title.matches("^[a-zA-Z0-9 ]+$")) {
			alerts.ErrorAlert("Error", "Movie title must contain Only Characters");
			return false;
		}
		if (description == null || description.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Movie Description cant be Empty");
			return false;
		}
		if (!description.matches("[a-zA-Z0-9\\s.,!?()'-]+")) {
			alerts.ErrorAlert("Error", "Category Description must contain Only Letters and Numbers");
			return false;
		}

		if (rating < 0.0 || rating > 10.0) {
			alerts.ErrorAlert("Error", "Movie rating must be between 0.0 and 10.0");
			return false;
		}

		return true;
	}

	public void Display() {
		Stage stage = new Stage();
		MenuBar menuBar = Main.createmenuBar(stage);

		GridPane addMovie = new GridPane();
		addMovie.setPadding(new Insets(30));
		addMovie.setHgap(50);
		addMovie.setVgap(40);

		titleLabel = new Label("Movie Title :");
		titleLabel.getStyleClass().add("fancy-label");
		titleField = new TextField();
		titleField.getStyleClass().add("fancy-textfield");
		addMovie.add(titleLabel, 0, 0);
		addMovie.add(titleField, 1, 0);

		descriptionLabel = new Label("Movie Description :");
		descriptionLabel.getStyleClass().add("fancy-label");
		descripField = new TextArea();
		descripField.getStyleClass().add("fancy-textfield");
		addMovie.add(descriptionLabel, 0, 1);
		addMovie.add(descripField, 1, 1);

		yearLabel = new Label("Movie Release Year :");
		yearLabel.getStyleClass().add("fancy-label");
		dateField = new DatePicker();
		dateField.getStyleClass().add("fancy-textfield");
		addMovie.add(yearLabel, 0, 2);
		addMovie.add(dateField, 1, 2);

		ratingLabel = new Label("Move Rating :");
		ratingLabel.getStyleClass().add("fancy-label");
		ratingField = new TextField();
		ratingField.getStyleClass().add("fancy-textfield");
		addMovie.add(ratingLabel, 0, 3);
		addMovie.add(ratingField, 1, 3);

		HBox allButtons = new HBox(100);
		backButton = new IconButton("Back", "/application/icons8-back-50.png");
		backButton.setOnAction(x -> {
			stage.close();
		});
		addButton = new IconButton("Add Movie", "/application/icons8-add-50.png");
		addButton.setOnAction(x -> {
			addButton();
		});

		clearButton = new IconButton("Clear", "/application/icons8-clear-50.png");
		clearButton.setOnAction(x -> {
			clear();
		});

		backButton.setPrefWidth(200);
		addButton.setPrefWidth(200);
		clearButton.setPrefWidth(200);
		allButtons.getChildren().addAll(backButton, addButton, clearButton);
		allButtons.setAlignment(Pos.CENTER);

		BorderPane addScreen = new BorderPane();

		addScreen.setTop(menuBar);
		addScreen.setLeft(addMovie);
		addScreen.setBottom(allButtons);

		Scene scene = new Scene(addScreen, 400, 300);
		stage.setScene(scene);
		stage.setTitle("Movie Managment System");
		stage.setMaximized(true);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.getIcons().add(new Image("/application/icons8-movies-50.png"));
		stage.show();
	}
}
