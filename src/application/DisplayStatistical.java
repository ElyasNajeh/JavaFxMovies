package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DisplayStatistical {
	MovieCatalog movieCatalog = Main.movieCatalog;
	IconButton backButton, prevButton, nextButton, topRankedButton, leastRankedButton, refreshButton;
	ComboBox<String> sortBox;
	TableView<Movie> movieTable2 = new TableView<>();
	ObservableList<Movie> movieList2 = FXCollections.observableArrayList();
	boolean columnsAdded = false;
	int index = 0; // index in the hash array
	Alerts alerts = new Alerts();

	// Loads movies from the current index in sorted ascending order
	public ObservableList<Movie> loadDetails() {
		if (index >= 0 && index < movieCatalog.max_Size) {
			return movieCatalog.arrayOfAVLTree[index].inOrderMovies();
		}
		return FXCollections.observableArrayList();
	}

	// Loads movies from the current index in reverse (descending) order
	public ObservableList<Movie> loadreverseDetails() {
		if (index >= 0 && index < movieCatalog.max_Size) {
			return movieCatalog.arrayOfAVLTree[index].reverseinOrder();
		}
		return FXCollections.observableArrayList();
	}

	public void Display() {
		Stage stage = new Stage();
		MenuBar menuBar = Main.createmenuBar(stage);

		if (!columnsAdded) {
			TableColumn<Movie, String> titleCol = new TableColumn<>("Movie Title");
			titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

			TableColumn<Movie, String> descriptionCol = new TableColumn<>("Movie Description");
			descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

			TableColumn<Movie, Integer> yearCol = new TableColumn<>("Movie Release Year");
			yearCol.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

			TableColumn<Movie, Double> ratingCol = new TableColumn<>("Movie Rating");
			ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

			descriptionCol.setMinWidth(450);
			movieTable2.getColumns().addAll(titleCol, descriptionCol, yearCol, ratingCol);
			movieTable2.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			columnsAdded = true;
		}
		// Load initial movies for index 0
		movieList2 = loadDetails();
		movieTable2.setItems(movieList2);

		VBox mainVbox = new VBox(25);
		HBox someButtons = new HBox(30);
		HBox someButtons2 = new HBox(30);

		prevButton = new IconButton("Previous", "/application/icons8-previous-48.png");
		prevButton.setOnAction(x -> {
			if (index > 0) {
				index--;
				movieList2 = loadDetails();
				movieTable2.setItems(movieList2);
			}
			prevButton.setDisable(index <= 0);
			nextButton.setDisable(index >= movieCatalog.max_Size - 1);

		});

		nextButton = new IconButton("Next", "/application/icons8-next-50.png");
		nextButton.setOnAction(x -> {
			if (index < movieCatalog.max_Size - 1) {
				index++;
				movieList2 = loadDetails();
				movieTable2.setItems(movieList2);
			}

			prevButton.setDisable(index <= 0);
			nextButton.setDisable(index >= movieCatalog.max_Size - 1);
		});

		refreshButton = new IconButton("Refresh", "/application/icons8-refresh-50.png");
		refreshButton.setOnAction(x -> {
			if (movieList2 == null || movieList2.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Refresh,Sorry..");
				return;
			}
			movieTable2.setItems(movieList2);
		});

		sortBox = new ComboBox<>();
		sortBox.setPromptText("Sort By:");

		sortBox.getItems().addAll("ASC", "DESC");
		// Sorting options ASC or DESC
		sortBox.setOnAction(e -> {
			String selected = sortBox.getValue();
			if ("ASC".equals(selected)) {
				movieList2 = loadDetails();
			} else if ("DESC".equals(selected)) {
				movieList2 = loadreverseDetails();
			}
			movieTable2.setItems(movieList2);
		});

		someButtons.getChildren().addAll(prevButton, nextButton, refreshButton, sortBox);
		someButtons.setAlignment(Pos.CENTER);

		backButton = new IconButton("Back", "/application/icons8-back-50.png");
		backButton.setOnAction(x -> {
			stage.close();
		});
		topRankedButton = new IconButton("Top Ranked Movies", "/application/icons8-up-50.png");
		topRankedButton.setOnAction(x -> {
			if (movieList2 == null || movieList2.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Display,Sorry..");
				return;
			}
			Double max = 0.0;
			for (Movie m : movieList2) {
				if (m.getRating() > max) {
					max = m.getRating();
				}
			}
			ObservableList<Movie> topRankedMovies = FXCollections.observableArrayList();
			for (Movie m : movieList2) {
				if (m.getRating() == max) {
					topRankedMovies.add(m);
				}
			}
			movieTable2.setItems(topRankedMovies);
		});

		leastRankedButton = new IconButton("Least Ranked Movies", "/application/icons8-down-50.png");
		leastRankedButton.setOnAction(x -> {
			if (movieList2 == null || movieList2.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Display,Sorry..");
				return;
			}
			Double min = 10.0;
			for (Movie m : movieList2) {
				if (m.getRating() < min) {
					min = m.getRating();
				}
			}
			ObservableList<Movie> leastRankedMovies = FXCollections.observableArrayList();
			for (Movie m : movieList2) {
				if (m.getRating() == min) {
					leastRankedMovies.add(m);
				}
			}
			movieTable2.setItems(leastRankedMovies);
		});

		someButtons2.getChildren().addAll(backButton, topRankedButton, leastRankedButton);
		someButtons2.setAlignment(Pos.CENTER);

		mainVbox.getChildren().addAll(someButtons, someButtons2);
		mainVbox.setAlignment(Pos.CENTER);
		BorderPane statisticalScreen = new BorderPane();

		statisticalScreen.setTop(menuBar);
		statisticalScreen.setCenter(movieTable2);
		statisticalScreen.setBottom(mainVbox);

		Scene scene = new Scene(statisticalScreen, 400, 300);
		stage.setScene(scene);
		stage.setTitle("Movie Managment System");
		stage.setMaximized(true);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.getIcons().add(new Image("/application/icons8-movies-50.png"));
		stage.show();
	}

}
