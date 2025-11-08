package application;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MovieCatalog {
	int max_Size = 17; // Initial hash table size (must be a prime number)
	AVL[] arrayOfAVLTree; // Hash table: each index holds an AVL tree
	AVL yearTree; // AVL tree to support search by year
	Alerts alerts = new Alerts();

	// Constructor: initializes the hash table and year tree
	MovieCatalog() {
		arrayOfAVLTree = new AVL[max_Size];
		for (int i = 0; i < max_Size; i++) {
			arrayOfAVLTree[i] = new AVL();
		}
		yearTree = new AVL();
	}

	// Converts a string into a number hash using base 32
	public int hashString(String key) {
		int hash = 0;
		int base = 1;

		for (int i = 0; i < key.length(); i++) {
			hash += key.charAt(i) * base;
			base *= 32;
		}
		return hash;
	}

//	hash function and reduces to valid array index
	public int hashFunction(String key) {
		return Math.abs(hashString(key)) % max_Size; // to ensure it is non - negative
	}
	// Checks if a number is prime

	public boolean isPrime(int num) {
		if (num < 2) {
			return false;
		}
		for (int i = 2; i < num; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	// Finds the next prime number after 2 * max_Size
	public int LargestPrimetoRehash(int size) {
		int temp = 2 * max_Size;
		for (int i = temp + 1; i < temp * 2; i++) {
			if (isPrime(i)) {
				return i;
			}
		}
		return 2;
	}
	// Calculates the average height

	private double AverageHeightofAllAVL() {
		int count = 0; // number of non-empty AVL trees
		int totalHeight = 0; // sum of all tree heights
		for (AVL tree : arrayOfAVLTree) {
			if (tree != null && tree.getRoot() != null) {
				totalHeight += tree.getRoot().getHeight();// add height of this tree
				count++;
			}
		}
		if (count == 0) { // avoid division by zero no information
			return 0;
		} else {
			return (double) totalHeight / count; // return average
		}
	}

	// Rehashes the entire hash table when average height becomes bigger than 3
	private void rehash() {
		int new_Size = LargestPrimetoRehash(max_Size);
		AVL[] newArrayofAVL = new AVL[new_Size];
		for (int i = 0; i < new_Size; i++) {
			newArrayofAVL[i] = new AVL();
		}
		for (AVL oldTree : arrayOfAVLTree) {
			ObservableList<Movie> movies = oldTree.inOrderMovies();

			for (Movie movie : movies) {
				int index = hashFunction(movie.getTitle());
				newArrayofAVL[index].insert(movie);
			}
		}
		arrayOfAVLTree = newArrayofAVL;
		max_Size = new_Size;

	}

	// Inserts a movie into the hash table and year tree
	public boolean insert(Movie movie) {
		int index = hashFunction(movie.getTitle());
		AVLTNode exist = arrayOfAVLTree[index].find(movie.getTitle());
		if (exist == null) {
			arrayOfAVLTree[index].insert(movie);
			yearTree.insertByYear(movie);
			if (AverageHeightofAllAVL() > 3) {
				rehash();
			}
			return true;
		} else {
			return false; // movie already exists
		}

	}

	// When title is updated, remove and re-insert the movie
	public void updateTitleTree(Movie movie) {
		int index = hashFunction(movie.getTitle());
		arrayOfAVLTree[index].delete(movie);
		arrayOfAVLTree[index].insert(movie);

	}

	// When year is updated, remove and re-insert from the year AVL tree
	public void updateYearTree(Movie movie) {
		yearTree.deleteByYear(movie);
		yearTree.insertByYear(movie);
	}

	// Searches for movies by title or year
	public ObservableList<Movie> searchMovie(String search) {
		ObservableList<Movie> resultofSearch = FXCollections.observableArrayList();
		if (search == null || search.trim().isEmpty()) {
			alerts.ErrorAlert("Error", "Please Enter Movie Title or Movie Release Year to Search");
			return resultofSearch;
		}
		// Check if its a number (year)
		boolean Num = true;
		for (int i = 0; i < search.length(); i++) {
			if (!Character.isDigit(search.charAt(i))) {
				Num = false;
				break;
			}
		}
		// If its a number, search in yearTree
		if (Num) {
			int find = Integer.parseInt(search);
			ObservableList<Movie> byYear = yearTree.findYear(find);
			resultofSearch.addAll(byYear);
		}
		// Search by title in the hash array
		int index = hashFunction(search);
		AVLTNode exist = arrayOfAVLTree[index].find(search);
		if (exist != null) {
			resultofSearch.add(exist.getMovie());
		}
		return resultofSearch;
	}

	// Deletes a movie from both title-based hash and yearTree
	public void delete(String title) {
		int index = hashFunction(title);
		AVLTNode exist = arrayOfAVLTree[index].find(title);
		if (exist != null) {
			arrayOfAVLTree[index].delete(exist.getMovie());
			yearTree.deleteByYear(exist.getMovie());
			return;
		} else {
			alerts.ErrorAlert("Error", "This Movie Does not Exist,Sorry ");
			return;
		}
	}

	// Removes all movie data from memory
	public void deallocate() {
		if (yearTree.getRoot() != null) {
			yearTree.setRoot(null);
		}

		for (int i = 0; i < max_Size; i++) {
			if (arrayOfAVLTree[i].getRoot() != null) {
				arrayOfAVLTree[i].setRoot(null);
			}
		}
	}

	// Loads movies from a text file using FileChooser
	public void loadFromFile() {
		FileChooser fc = new FileChooser();
		fc.setTitle("Select Movie File");
		fc.setInitialDirectory(new File("C:\\Users\\HP\\eclipse-workspace\\FxMovies"));
		Stage stage = new Stage();
		File f = fc.showOpenDialog(stage);

		if (f == null) {
			alerts.ErrorAlert("Error", "No file selected. Please select a file.");
			return;
		}

		try (Scanner scanner = new Scanner(f)) {
			while (scanner.hasNextLine()) {
				// Each movie is expected to take 4 lines, then a Empty line
				String titleLine = scanner.nextLine().trim();
				String descLine = scanner.nextLine().trim();
				String yearLine = scanner.nextLine().trim();
				String ratingLine = scanner.nextLine().trim();
				// skip Empty line
				if (scanner.hasNextLine())
					scanner.nextLine();

				try {
					String movieTitle = titleLine.substring("Title:".length()).trim();
					String movieDescription = descLine.substring("Description:".length()).trim();
					int releaseYear = Integer.parseInt(yearLine.substring("Release Year:".length()).trim());
					double rating = Double.parseDouble(ratingLine.substring("Rating:".length()).trim());

					if (movieTitle == null || movieTitle.isEmpty())
						continue;
					if (rating < 0 || rating > 10) {
						continue;
					}

					Movie loadedMovie = new Movie(movieTitle, movieDescription, releaseYear, rating);
					insert(loadedMovie);
					Main.movieList.add(loadedMovie);

				} catch (Exception e) {
					alerts.ErrorAlert("Error", "Could not read entry: \n" + titleLine);
				}
			}

			alerts.InfoAlert("Success", "File read Successfully!");

		} catch (IOException e) {
			alerts.ErrorAlert("Error", "Error while reading the file.");
		}
	}

	// Saves all movies from AVL tree to a same file
	public void saveToFile() {
		File file = new File("C:\\Users\\HP\\eclipse-workspace\\FxMovies\\movies.txt");
		try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
			for (int i = 0; i < max_Size; i++) {
				ObservableList<Movie> movies = arrayOfAVLTree[i].inOrderMovies();
				for (Movie movie : movies) {
					writer.println(buildUserData(movie)); // write movie data
					writer.println(); // add Empty Line line between entries
				}
			}

			alerts.InfoAlert("Success", "Movies data saved successfully!");
		} catch (IOException e) {
			alerts.ErrorAlert("Error", "Failed to Save Movies data");
		}
	}

	// Formats movie data as text (used when saving to file)
	private String buildUserData(Movie movie) {
		return "Title: " + movie.getTitle() + "\n" + "Description: " + movie.getDescription() + "\n" + "Release Year: "
				+ movie.getReleaseYear() + "\n" + "Rating: " + movie.getRating();
	}
}
