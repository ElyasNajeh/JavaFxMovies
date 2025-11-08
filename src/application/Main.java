package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class Main extends Application {
	// Static variables to manage movie catalog
	static MovieCatalog movieCatalog = new MovieCatalog();
	static TableView<Movie> movieTable = new TableView<>();
	static ObservableList<Movie> movieList = FXCollections.observableArrayList();
	static IconButton searchButton, refreshButton, addButton, updateButton, removeButton, clearData, topRankedButton,
			leastRankedButton;
	static TextField searchField;
	static Alerts alerts = new Alerts();
	static boolean columnsAdded = false; // ensures columns are only added once

	public void start(Stage stage) {

		BorderPane MainScreen = new BorderPane();

		MainScreen.setTop(createmenuBar(stage));
		MainScreen.setCenter(createTableView(stage));
		MainScreen.setBottom(createVbox(stage));

		Scene scene = new Scene(MainScreen, 400, 400);
		stage.setScene(scene);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		stage.setTitle("Movie Managment System");
		stage.setMaximized(true);
		stage.getIcons().add(new Image("/application/icons8-movies-50.png"));
		stage.show();

	}

	public static void main(String[] args) {
		launch(args);
	}

	// creates the top menu bar with file operations and statistics
	public static MenuBar createmenuBar(Stage stage) {
		MenuBar menuBar = new MenuBar();

		// Add Movie menu
		Menu addMovie1 = new Menu();
		MenuItem addMovie11 = new MenuItem("Add Movie");
		addMovie11.setOnAction(x -> {
			AddMovie addMovie = new AddMovie();
			addMovie.Display();
		});
		addMovie1.getItems().add(addMovie11);
		addMovie1.setGraphic(createIcon("/application/icons8-add-properties-50.png"));

		// Display Statistics menu
		Menu displayStatistical1 = new Menu();
		MenuItem displayStatistical11 = new MenuItem("Display Statistical");
		displayStatistical11.setOnAction(x -> {
			DisplayStatistical displayStatistical = new DisplayStatistical();
			displayStatistical.Display();
		});
		displayStatistical1.getItems().add(displayStatistical11);
		displayStatistical1.setGraphic(createIcon("/application/icons8-analytics-64.png"));

		// Load movies from file
		Menu loadItem1 = new Menu();
		MenuItem loadItem11 = new MenuItem("Load Movies");
		loadItem11.setOnAction(x -> {
			movieCatalog.loadFromFile();
		});
		loadItem1.getItems().addAll(loadItem11);
		loadItem1.setGraphic(createIcon("/application/icons8-load-from-file-48.png"));

		// Save movies to file
		Menu saveItem1 = new Menu();
		MenuItem saveItem11 = new MenuItem("Save Movies");
		saveItem11.setOnAction(x -> {
			movieCatalog.saveToFile();
		});
		saveItem1.getItems().add(saveItem11);
		saveItem1.setGraphic(createIcon("/application/icons8-save-50.png"));

		// Exit menu
		Menu exit1 = new Menu();
		MenuItem exit11 = new MenuItem("Exit");
		exit11.setOnAction(x -> {
			stage.close();
		});
		exit1.getItems().add(exit11);
		exit1.setGraphic(createIcon("/application/icons8-exit-50.png"));

		menuBar.getMenus().addAll(addMovie1, displayStatistical1, loadItem1, saveItem1, exit1);
		return menuBar;
	}

	private static ImageView createIcon(String path) {
		Image img = new Image(path);
		ImageView iv = new ImageView(img);
		iv.setFitWidth(70);
		iv.setFitHeight(70);
		return iv;
	}

	// Creates the table view that displays movies
	private static TableView createTableView(Stage stage) {

		if (!columnsAdded) {

			// Add table columns for title, description, year, rating

			TableColumn<Movie, String> titleCol = new TableColumn<>("Movie Title");
			titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));

			TableColumn<Movie, String> descriptionCol = new TableColumn<>("Movie Description");
			descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));

			TableColumn<Movie, Integer> yearCol = new TableColumn<>("Movie Release Year");
			yearCol.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

			TableColumn<Movie, Double> ratingCol = new TableColumn<>("Movie Rating");
			ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));

			descriptionCol.setMinWidth(450);
			movieList.clear();
			movieTable.getColumns().addAll(titleCol, descriptionCol, yearCol, ratingCol);
			movieTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
			movieTable.setItems(movieList);
			columnsAdded = true;
		}

		// Enable update/remove buttons when a row is clicked
		movieTable.setOnMouseClicked(e -> {
			if (movieTable.getSelectionModel().getSelectedIndex() >= 0) {
				updateButton.setDisable(false);
				removeButton.setDisable(false);
			}
		});
		return movieTable;
	}

	// Creates bottom part with search, add, update, clear, and ranked buttons
	private static VBox createVbox(Stage stage) {
		VBox mainVbox = new VBox();
		HBox someButtons = new HBox(25);
		HBox searchButtons = new HBox(25);

		// Button to add a new movie
		addButton = new IconButton("Add Movie", "/application/icons8-add-50.png");
		addButton.setOnAction(x -> {
			AddMovie addMovie = new AddMovie();
			addMovie.Display();
		});
		// Button to update selected movie
		updateButton = new IconButton("Update Movie", "/application/icons8-update-64.png");
		updateButton.setDisable(true);
		updateButton.setOnAction(x -> {
			int selectedIndex = movieTable.getSelectionModel().getSelectedIndex();
			UpdateMovie updateButton = new UpdateMovie(movieList, selectedIndex);
			updateButton.Display();
		});
		// Button to delete selected movie
		removeButton = new IconButton("Remove Movie", "/application/icons8-remove-50.png");
		removeButton.setDisable(true);
		removeButton.setOnAction(x -> {
			int selectedIndex = movieTable.getSelectionModel().getSelectedIndex();
			Movie removeMovie = movieList.get(selectedIndex);
			if (selectedIndex >= 0) {
				boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
						"Are you sure you need to Delete this Movie ? ");
				if (!confirmation) {
					return;
				}
				movieCatalog.delete(removeMovie.getTitle());
				movieList.remove(selectedIndex);
				alerts.InfoAlert("Success", "Movie has been Deleted Successfully");
			}
		});
		// Button to clear all movie data
		clearData = new IconButton("Clear All Data", "/application/icons8-clear-50.png");
		clearData.setOnAction(x -> {
			if (movieList == null || movieList.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Clear,Sorry..");
				return;
			}
			boolean confirmation = alerts.ConfiramtionAlert("Confirmation",
					"Are you sure you need to Clear All The Data ?");
			if (!confirmation) {
				return;
			}
			movieCatalog.deallocate();
			movieList.clear();
			alerts.InfoAlert("Success", "Data Cleared Succesfully");
		});
		// Button to display top-rated movies
		topRankedButton = new IconButton("Top Ranked Movies", "/application/icons8-up-50.png");
		topRankedButton.setOnAction(x -> {
			if (movieList == null || movieList.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Display,Sorry..");
				return;
			}
			Double max = 0.0;
			for (Movie m : movieList) {
				if (m.getRating() > max) {
					max = m.getRating();
				}
			}
			ObservableList<Movie> topRankedMovies = FXCollections.observableArrayList();
			for (Movie m : movieList) {
				if (m.getRating() == max) {
					topRankedMovies.add(m);
				}
			}
			movieTable.setItems(topRankedMovies);
		});
		// Button to display least-rated movies
		leastRankedButton = new IconButton("Least Ranked Movies", "/application/icons8-down-50.png");
		leastRankedButton.setOnAction(x -> {
			if (movieList == null || movieList.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Display,Sorry..");
				return;
			}
			Double min = 10.0;
			for (Movie m : movieList) {
				if (m.getRating() < min) {
					min = m.getRating();
				}
			}
			ObservableList<Movie> leastRankedMovies = FXCollections.observableArrayList();
			for (Movie m : movieList) {
				if (m.getRating() == min) {
					leastRankedMovies.add(m);
				}
			}
			movieTable.setItems(leastRankedMovies);
		});
		// Search by title or year
		searchButton = new IconButton("Search", "/application/icons8-search-50.png");
		searchButton.setOnAction(x -> {
			ObservableList<Movie> resultofSearch = movieCatalog.searchMovie(searchField.getText());

			if (resultofSearch.isEmpty() && searchField.getText() != null && !searchField.getText().trim().isEmpty()) {
				alerts.ErrorAlert("Error", "No Movie Found with this Title or Release Year,Try Again");
				movieTable.setItems(movieList);
			} else {
				movieTable.setItems(resultofSearch);
			}
		});
		searchField = new TextField();
		searchField.getStyleClass().add("search-field");
		searchField.setPromptText("Search here ..");

		// Refresh button to reload all movies
		refreshButton = new IconButton("Refresh", "/application/icons8-refresh-50.png");
		refreshButton.setOnAction(x -> {
			if (movieList == null || movieList.isEmpty()) {
				alerts.ErrorAlert("Error", "No Data in Table to Refresh,Sorry..");
				return;
			}
			movieTable.setItems(movieList);
		});

		searchButtons.getChildren().addAll(topRankedButton, searchField, searchButton, leastRankedButton);
		searchButtons.setAlignment(Pos.CENTER);

		someButtons.getChildren().addAll(addButton, updateButton, removeButton, refreshButton, clearData);
		someButtons.setAlignment(Pos.CENTER);

		mainVbox.setSpacing(10);
		mainVbox.setPadding(new Insets(10));
		mainVbox.getChildren().addAll(searchButtons, someButtons);
		mainVbox.setAlignment(Pos.CENTER);

		return mainVbox;
	}
}
