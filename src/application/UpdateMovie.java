package application;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UpdateMovie {
	ObservableList<Movie> movieList = Main.movieList;
	MovieCatalog movieCatalog = Main.movieCatalog;
	AddMovie toValidation = new AddMovie();
	IconButton backButton, updateButton, prevButton, nextButton;
	Label titleLabel, descriptionLabel, yearLabel, ratingLabel;
	TextField titleField, dateField, ratingField;
	TextArea descripField;
	int index;
	Alerts alerts = new Alerts();

	// Constructor that receives the list and selected index
	public UpdateMovie(ObservableList<Movie> movieList, int index) {
		this.movieList = movieList;
		this.index = index;
	}

	// Loads data of the selected movie into the input fields
	public void loadIndexDetails(TextField titleField, TextArea descripField, TextField dateField,
			TextField ratingField) {
		if (index >= 0 && index < movieList.size()) {
			Movie infoMovie = movieList.get(index);
			titleField.setText(infoMovie.getTitle());
			descripField.setText(infoMovie.getDescription());
			dateField.setText(infoMovie.getReleaseYear() + "");
			ratingField.setText(infoMovie.getRating() + "");
		}
		// Disable navigation buttons
		prevButton.setDisable(index <= 0);
		nextButton.setDisable(index >= movieList.size() - 1);
	}

	public void updateButton() {
		Movie infoMovie = movieList.get(index);
		String newMovieTitle = titleField.getText();
		String newMovieDescription = descripField.getText();
		String year = dateField.getText();
		String rating = ratingField.getText();
		if (year == null || year.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Movie Release Year Cant be Empty");
			return;
		}
		if (!year.matches("\\d+")) {
			alerts.ErrorAlert("Error", "Movie Release Year must be number Only");
			return;
		}

		if (rating == null || rating.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Movie Rating Cant be Empty");
			return;
		}

		int newMoviereleaseYear = Integer.parseInt(year);
		if (newMoviereleaseYear > 2025 || newMoviereleaseYear < 1950) {
			alerts.ErrorAlert("Error", "Movie Release Year must be between 1950 and 2025");
			return;
		}

		double newMovieRating = Double.parseDouble(rating);

		// Reuse the validation from AddMovie
		boolean isValid = toValidation.validation(newMovieTitle, newMovieDescription, newMovieRating);
		if (!isValid) {
			return;
		}
		boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
				"Are you sure you need to Update Information for This Movie ?");
		if (!confirmation) {
			return;
		}
		// Check what has changed: title or year
		// Nothing
		if (infoMovie.getTitle().equals(newMovieTitle) && infoMovie.getReleaseYear() == newMoviereleaseYear) {

		}
		// Only title changed
		else if (!infoMovie.getTitle().equals(newMovieTitle) && infoMovie.getReleaseYear() == newMoviereleaseYear) {
			movieCatalog.updateTitleTree(infoMovie);
			infoMovie.setTitle(newMovieTitle);
		}
		// Only year changed

		else if (infoMovie.getTitle().equals(newMovieTitle) && infoMovie.getReleaseYear() != newMoviereleaseYear) {
			movieCatalog.updateYearTree(infoMovie);
			infoMovie.setReleaseYear(newMoviereleaseYear);

		}
		// Both title and year changed
		else {
			movieCatalog.updateTitleTree(infoMovie);
			movieCatalog.updateYearTree(infoMovie);
			infoMovie.setTitle(newMovieTitle);
			infoMovie.setReleaseYear(newMoviereleaseYear);
		}
		// Update other fields
		infoMovie.setDescription(newMovieDescription);
		infoMovie.setRating(newMovieRating);
		movieList.set(index, infoMovie);
		alerts.InfoAlert("Success", "Movie Information has been Updated Successfully");

	}

	public void Display() {
		Stage stage = new Stage();
		MenuBar menuBar = Main.createmenuBar(stage);

		GridPane updateMovie = new GridPane();
		updateMovie.setPadding(new Insets(30));
		updateMovie.setHgap(50);
		updateMovie.setVgap(40);

		titleLabel = new Label("Movie Title :");
		titleLabel.getStyleClass().add("fancy-label");
		titleField = new TextField();
		titleField.getStyleClass().add("fancy-textfield");
		updateMovie.add(titleLabel, 0, 0);
		updateMovie.add(titleField, 1, 0);

		descriptionLabel = new Label("Movie Description :");
		descriptionLabel.getStyleClass().add("fancy-label");
		descripField = new TextArea();
		descripField.getStyleClass().add("fancy-textfield");
		updateMovie.add(descriptionLabel, 0, 1);
		updateMovie.add(descripField, 1, 1);

		yearLabel = new Label("Movie Release Year :");
		yearLabel.getStyleClass().add("fancy-label");
		dateField = new TextField();
		dateField.getStyleClass().add("fancy-textfield");
		updateMovie.add(yearLabel, 0, 2);
		updateMovie.add(dateField, 1, 2);

		ratingLabel = new Label("Move Rating :");
		ratingLabel.getStyleClass().add("fancy-label");
		ratingField = new TextField();
		ratingField.getStyleClass().add("fancy-textfield");
		updateMovie.add(ratingLabel, 0, 3);
		updateMovie.add(ratingField, 1, 3);

		prevButton = new IconButton("Previous", "/application/icons8-previous-48.png");
		prevButton.setOnAction(x -> {
			if (index > 0) {
				index--;
				loadIndexDetails(titleField, descripField, dateField, ratingField);
			}

		});

		nextButton = new IconButton("Next", "/application/icons8-next-50.png");
		nextButton.setOnAction(x -> {
			if (index < movieList.size() - 1) {
				index++;
				loadIndexDetails(titleField, descripField, dateField, ratingField);
			}
		});

		backButton = new IconButton("Back", "/application/icons8-back-50.png");
		backButton.setOnAction(x -> {
			stage.close();
		});
		updateButton = new IconButton("Update Movie", "/application/icons8-update-64.png");
		updateButton.setOnAction(x -> {
			updateButton();
		});

		loadIndexDetails(titleField, descripField, dateField, ratingField);

		VBox allButtons = new VBox(20);
		HBox someButtons1 = new HBox(30);
		HBox someButtons2 = new HBox(30);
		someButtons1.getChildren().addAll(prevButton, nextButton);
		someButtons1.setAlignment(Pos.CENTER);

		someButtons2.getChildren().addAll(backButton, updateButton);
		someButtons2.setAlignment(Pos.CENTER);

		allButtons.getChildren().addAll(someButtons1, someButtons2);
		allButtons.setAlignment(Pos.CENTER);

		BorderPane updateScreen = new BorderPane();

		updateScreen.setTop(menuBar);
		updateScreen.setLeft(updateMovie);
		updateScreen.setBottom(allButtons);

		Scene scene = new Scene(updateScreen, 400, 300);
		stage.setScene(scene);
		stage.setTitle("Movie Managment System");
		stage.setMaximized(true);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.getIcons().add(new Image("/application/icons8-movies-50.png"));
		stage.show();
	}

}
