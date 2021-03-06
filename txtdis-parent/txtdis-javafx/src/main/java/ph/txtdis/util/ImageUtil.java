package ph.txtdis.util;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

public class ImageUtil {
	public static byte[] defaultImageBytes(String name) {
		return BinaryUtils.toBytes(defaultImageStream(name));
	}

	public static InputStream defaultImageStream(String name) {
		return ImageUtil.class.getResourceAsStream("/image/" + name + ".jpg");
	}

	public static ImageView getDefaultImageView(String name) {
		return toImageView(defaultImageStream(name));
	}

	public static ImageView toImageView(InputStream imageStream) {
		Image image = new Image(imageStream, 200, 0, true, true);
		return new ImageView(image);
	}

	public static byte[] imageBytesFromFile(Scene scene) {
		File file = selectImageFile((Stage) scene.getWindow());
		return file == null ? null : BinaryUtils.toBytes(file);
	}

	public static File selectImageFile(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load image");
		return fileChooser.showOpenDialog(stage);
	}

	public static InputStream loadInputStream(Stage stage, String defaultImageName) {
		File file = selectImageFile(stage);
		return file == null ? defaultImageStream(defaultImageName) : BinaryUtils.inputStream(file);
	}

	public static File selectImageFile(Label label) {
		Scene scene = label.getScene();
		Stage stage = (Stage) scene.getWindow();
		return selectImageFile(stage);
	}

	public static ImageView toImageView(byte[] imagebytes) {
		InputStream imageStream = new ByteArrayInputStream(imagebytes);
		return toImageView(imageStream);
	}
}
