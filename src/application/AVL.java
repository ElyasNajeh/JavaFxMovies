package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AVL {
	private AVLTNode root;

	AVL() {
		this.root = null;
	}

	public AVLTNode getRoot() {
		return root;
	}

	public void setRoot(AVLTNode root) {
		this.root = root;
	}

	// Get height of a node (used for balancing)
	public int height(AVLTNode e) {
		if (e == null) {
			return -1;
		} else {
			return e.getHeight();
		}
	}

	// right rotation
	private AVLTNode rotatewithLeftChild(AVLTNode k2) { // rotate to right
		if (k2.getLeft() == null) {
			return k2;
		}
		AVLTNode k1 = k2.getLeft();
		k2.setLeft(k1.getRight());
		k1.setRight(k2);
		k2.setHeight(Math.max(height(k2.getRight()), height(k2.getLeft())) + 1);
		k1.setHeight(Math.max(height(k1.getLeft()), height(k2)) + 1);
		return k1;

	}

	public AVLTNode rotatewithLeftChild() {
		return rotatewithLeftChild(root);
	}

	// left rotation
	private AVLTNode rotatewithRightChild(AVLTNode k1) { // rotate to left
		if (k1.getRight() == null) {
			return k1;
		}
		AVLTNode k2 = k1.getRight();
		k1.setRight(k2.getLeft());
		k2.setLeft(k1);
		k1.setHeight(Math.max(height(k1.getRight()), height(k1.getLeft())) + 1);
		k2.setHeight(Math.max(height(k2.getRight()), height(k1)) + 1);
		return k2;

	}

	public AVLTNode rotatewithRightChild() {
		return rotatewithRightChild(root);
	}

	private AVLTNode DoubleWithLeftChild(AVLTNode k3) { // left then right
		if (k3.getLeft() != null) {
			k3.setLeft(rotatewithRightChild(k3.getLeft()));
		}
		return rotatewithLeftChild(k3);
	}

	public AVLTNode DoubleWithLeftChild() {
		return DoubleWithLeftChild(root);
	}

	private AVLTNode DoubleWithRightChild(AVLTNode k1) { // right then left
		if (k1.getRight() != null) {
			k1.setRight(rotatewithLeftChild(k1.getRight()));
		}
		return rotatewithRightChild(k1);
	}

	public AVLTNode DoubleWithRightChild() {
		return DoubleWithRightChild(root);
	}

	// Balance a node
	private AVLTNode balance(AVLTNode k1) {
		if (k1 == null) {
			return null;
		}
		int balanceFactor = height(k1.getLeft()) - height(k1.getRight());
		if (balanceFactor > 1) {
			if (height(k1.getLeft().getLeft()) >= height(k1.getLeft().getRight())) {
				k1 = rotatewithRightChild(k1);
			} else {
				k1 = DoubleWithLeftChild(k1);
			}
		} else if (balanceFactor < -1) {
			if (height(k1.getRight().getRight()) >= height(k1.getRight().getLeft())) {
				k1 = rotatewithLeftChild(k1);
			} else {
				k1 = DoubleWithRightChild(k1);
			}
		}
		return k1;
	}

	public AVLTNode balance() {
		return balance(root);
	}

	private boolean contains(String title, AVLTNode current) {
		if (current == null) {
			return false;
		} else if (title.compareTo(current.getMovie().getTitle()) < 0) {
			return contains(title, current.getLeft());
		} else if (title.compareTo(current.getMovie().getTitle()) > 0) {
			return contains(title, current.getRight());
		}

		return true;
	}

	public boolean contains(String title) {
		return contains(title, root);
	}

	private AVLTNode find(String title, AVLTNode current) {
		if (current == null) {
			return null;
		} else if (title.compareTo(current.getMovie().getTitle()) < 0) {
			return find(title, current.getLeft());
		} else if (title.compareTo(current.getMovie().getTitle()) > 0) {
			return find(title, current.getRight());
		}
		return current;
	}

	public AVLTNode find(String title) {
		return find(title, root);
	}

	private AVLTNode insert(Movie movie, AVLTNode current) {
		if (current == null) {
			current = new AVLTNode(movie);
		} else if (movie.getTitle().compareTo(current.getMovie().getTitle()) < 0) {
			current.setLeft(insert(movie, current.getLeft()));
		} else if (movie.getTitle().compareTo(current.getMovie().getTitle()) > 0) {
			current.setRight(insert(movie, current.getRight()));
		}
		current.setHeight(Math.max(height(current.getLeft()), height(current.getRight())) + 1);
		return balance(current);
	}

	public void insert(Movie movie) {
		root = insert(movie, root);
	}

	private AVLTNode findMin(AVLTNode k1) {
		if (k1 == null) {
			return null;
		} else if (k1.getLeft() == null) {
			return k1;
		} else {
			return findMin(k1.getLeft());
		}
	}

	private AVLTNode delete(Movie movie, AVLTNode current) {
		if (current == null) {
			return null;
		} else if (movie.getTitle().compareTo(current.getMovie().getTitle()) < 0) {
			current.setLeft(delete(movie, current.getLeft()));
		} else if (movie.getTitle().compareTo(current.getMovie().getTitle()) > 0) {
			current.setRight(delete(movie, current.getRight()));
		} else {
			if (current.getLeft() != null && current.getRight() != null) {
				current.setMovie(findMin(current.getRight()).getMovie());
				current.setRight(delete(current.getMovie(), current.getRight()));
			} else {
				current = (current.getLeft() != null) ? current.getLeft() : current.getRight();
			}
		}
		if (current != null) {
			current.setHeight(Math.max(height(current.getLeft()), height(current.getRight())) + 1);
			current = balance(current);
		}
		return current;
	}

	public void delete(Movie movie) {
		root = delete(movie, root);
	}

	private void findYear(int year, AVLTNode current, ObservableList<Movie> list) {
		if (current == null) {
			return;
		} else if (year < current.getMovie().getReleaseYear()) {
			findYear(year, current.getLeft(), list);
		} else if (year > current.getMovie().getReleaseYear()) {
			findYear(year, current.getRight(), list);
		} else {
			list.add(current.getMovie());
			findYear(year, current.getRight(), list);
		}
	}

	public ObservableList<Movie> findYear(int year) {
		ObservableList<Movie> list = FXCollections.observableArrayList();
		findYear(year, root, list);
		return list;
	}

	public AVLTNode insertByYear(Movie movie, AVLTNode current) {
		if (current == null) {
			current = new AVLTNode(movie);
		} else if (movie.getReleaseYear() < current.getMovie().getReleaseYear()) {
			current.setLeft(insertByYear(movie, current.getLeft()));
		} else if (movie.getReleaseYear() >= current.getMovie().getReleaseYear()) {
			current.setRight(insertByYear(movie, current.getRight()));
		}
		current.setHeight(Math.max(height(current.getLeft()), height(current.getRight())) + 1);
		return balance(current);
	}

	public void insertByYear(Movie movie) {
		root = insertByYear(movie, root);
	}

	private AVLTNode deleteByYear(Movie movie, AVLTNode current) {
		if (current == null) {
			return null;
		} else if (movie.getReleaseYear() < current.getMovie().getReleaseYear()) {
			current.setLeft(deleteByYear(movie, current.getLeft()));
		} else if (movie.getReleaseYear() >= current.getMovie().getReleaseYear()) {
			current.setRight(deleteByYear(movie, current.getRight()));
		} else {
			if (current.getLeft() != null && current.getRight() != null) {
				current.setMovie(findMin(current.getRight()).getMovie());
				current.setRight(deleteByYear(current.getMovie(), current.getRight()));
			} else {
				current = (current.getLeft() != null) ? current.getLeft() : current.getRight();
			}
		}
		if (current != null) {
			current.setHeight(Math.max(height(current.getLeft()), height(current.getRight())) + 1);
			current = balance(current);
		}
		return current;
	}

	public void deleteByYear(Movie movie) {
		root = deleteByYear(movie, root);
	}

	private void inOrder(AVLTNode current, ObservableList<Movie> list) { // left --> root --> right
		if (current != null) {
			inOrder(current.getLeft(), list);
			list.add(current.getMovie());
			inOrder(current.getRight(), list);
		} else {
			return;
		}
	}

	public ObservableList<Movie> inOrderMovies() {
		ObservableList<Movie> list = FXCollections.observableArrayList();
		inOrder(root, list);
		return list;
	}

	private void reverseinOrder(AVLTNode current, ObservableList<Movie> list) {// right --> root --> left
		if (current != null) {
			inOrder(current.getRight(), list);
			list.add(current.getMovie());
			inOrder(current.getLeft(), list);
		} else {
			return;
		}
	}

	public ObservableList<Movie> reverseinOrder() {
		ObservableList<Movie> list = FXCollections.observableArrayList();
		reverseinOrder(root, list);
		return list;
	}
}
