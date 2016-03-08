package ph.txtdis.printer;

import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.leftPad;

import javafx.scene.image.Image;

public abstract class Printer<T> extends CDRKingPrinter<T> {

	protected static final int LARGE_FONT_PAPER_WIDTH = PAPER_WIDTH / 2;

	protected static final int HALF_PAPER_WIDTH = LARGE_FONT_PAPER_WIDTH - 2;

	private int[][] getLogo() {
		String string;
		Image image = new Image(this.getClass().getResourceAsStream("/image/magnum.jpg"));
		int height = (int) image.getHeight();
		int yOffset = height % 8 / 2;
		int argb, red;
		int yLoop = height / 8;
		int width = (int) image.getWidth();
		int[][] value = new int[yLoop][width];
		for (int i = 0; i < yLoop; i++) {
			for (int x = 0; x < width; x++) {
				string = "";
				for (int y = 0; y < 8; y++) {
					argb = image.getPixelReader().getArgb(x, yOffset + y + i * 8);
					red = 0xFF & (argb >> 16);
					string += red > 127 ? "0" : "1";
				}
				value[i][x] = Integer.parseInt(string, 2);
			}
		}
		return value;
	}

	@Override
	protected void print() throws Exception {
		try {
			printLogo(getLogo());
			printSubheader();
			printDetails();
			printFooter();
		} catch (IOException e) {
			e.printStackTrace();
			throw new NotPrintedException();
		}
	}

	protected void printDashes() {
		printer.println(leftPad("", PAPER_WIDTH, "-"));
	}

	protected abstract void printDetails() throws Exception;

	protected void printEndOfPage() {
		printer.println(leftPad("", PAPER_WIDTH, "_"));
		printFourLines();
	}

	protected void printFourLines() {
		for (int i = 0; i < 4; i++)
			printer.println();
	}

	protected abstract void printFooter() throws IOException;

	protected abstract void printSubheader() throws IOException;

}
