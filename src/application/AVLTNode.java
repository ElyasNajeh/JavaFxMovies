package application;

public class AVLTNode {
	private Movie movie;
	private AVLTNode left;
	private AVLTNode right;
	private int height;

	// Constructor: creates a node with given movie and no children
	AVLTNode(Movie movie) {
		this(movie, null, null);
	}

	// Constructor with left and right children
	public AVLTNode(Movie movie, AVLTNode left, AVLTNode right) {
		this.movie = movie;
		this.left = left;
		this.right = right;
		this.height = 0; // New nodes are created with height 0
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}

	public AVLTNode getLeft() {
		return left;
	}

	public void setLeft(AVLTNode left) {
		this.left = left;
	}

	public AVLTNode getRight() {
		return right;
	}

	public void setRight(AVLTNode right) {
		this.right = right;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
