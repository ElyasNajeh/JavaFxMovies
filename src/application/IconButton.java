package application;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconButton extends Button {
	// Constructor: takes button text and the path to the icon image
	public IconButton(String text, String imagePath) {
		super(text);
		ImageView iv = new ImageView(new Image(imagePath));
		iv.setFitWidth(20);
		iv.setFitHeight(20);
		this.setGraphic(iv);
		this.setGraphicTextGap(10);
		this.getStyleClass().setAll("footer-button");
	}
}