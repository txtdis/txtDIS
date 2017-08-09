package ph.txtdis.printer;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import javafx.scene.image.Image;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import ph.txtdis.exception.InvalidException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import static gnu.io.CommPortIdentifier.PORT_SERIAL;
import static gnu.io.SerialPort.*;
import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.log4j.Logger.getLogger;

@Service("cdrKingPrinter")
public class CDRKingPrinterImpl //
	implements CDRKingPrinter {

	public static final int PAPER_WIDTH = 40;

	public static final int HALF_COLUMN = (PAPER_WIDTH / 2) - 2;

	public static final int LARGE_FONT_PAPER_WIDTH = PAPER_WIDTH / 2;

	public static final int SUBHEADER_LABEL_WIDTH = 9;

	private static final char ASTERISK = 42;

	private static final char AT = 64;

	private static final char CHAR_PER_LINE = 4;

	private static final char ESC = 27;

	private static final char EXCLAMATION = 33;

	private static final char HUGE = 0b0011_0001;

	private static final char J = 74;

	private static final byte M = (byte) 0;

	private static final char N = 78;

	private static final char NARROW_42CPL = 1;

	private static final byte N144_FEED = (byte) 16;

	private static final byte N2 = (byte) 0;

	private static Logger logger = getLogger(CDRKingPrinterImpl.class);

	private SerialPort serial;

	private OutputStream output;

	private PrintStream printer;

	@Override
	public void build() throws InvalidException {
		Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;
		String portName = null;

		while (portIdentifiers.hasMoreElements()) {
			portId = (CommPortIdentifier) portIdentifiers.nextElement();
			if (portId.getPortType() == PORT_SERIAL && portId.getName().equals("COM9")) {
				portName = portId.getName();
				break;
			}
		}

		if (portName == null)
			throw new InvalidException(
				"COM9 cannot be found;\nensure printer is on and plugged in to said port,\nthen reboot server");

		try {
			serial = portId.open(portName, 100);
			serial.setSerialPortParams(9600, DATABITS_8, STOPBITS_1, PARITY_NONE);
		} catch (PortInUseException e) {
			logger.error(e.getStackTrace());
			throw new InvalidException("COM9 is in use;\nclose other server apps.");
		} catch (UnsupportedCommOperationException e) {
			logger.error(e.getStackTrace());
			throw new InvalidException("Printer must be\na CDRKing brand.");
		} finally {
			if (serial != null)
				serial.close();
		}

		try ( //
		      OutputStream os = serial.getOutputStream(); //
		      PrintStream ps = new PrintStream(os, true) //
		) {
			this.output = os;
			this.printer = ps;
		} catch (IOException e) {
			logger.error(e.getStackTrace());
			throw new InvalidException("No signal from printer;\nrestart it and try again.");
		}
	}

	@Override
	public void print(String text) {
		printer.print(text);
	}

	@Override
	public void printCenter(String text) {
		println(center(text, PAPER_WIDTH));
	}

	@Override
	public void println(String text) {
		printer.println(text);
	}

	@Override
	public void printDashes(int count) throws InvalidException {
		if (count > PAPER_WIDTH)
			count = PAPER_WIDTH;
		println(leftPad("", count, "-"));
	}
	@Override
	public void printDoubleDashes(int count) throws InvalidException {
		if (count > PAPER_WIDTH)
			count = PAPER_WIDTH;
		println(leftPad("", count, "="));
	}

	@Override
	public void printEndOfPage() {
		println(leftPad("", PAPER_WIDTH, "_"));
		printLines(4);
	}

	@Override
	public void printLines(int lines) {
		for (int i = 0; i < lines; i++)
			printer.println();
	}

	@Override
	public void printLogo() throws InvalidException {
		int[][] logo = getLogo();
		for (int i = 0; i < logo.length; i++) {
			int width = logo[i].length;
			write(ESC);
			write(ASTERISK);
			write(M);
			write((byte) width); // n1
			write(N2);
			for (int j = 0; j < width; j++)
				write((byte) logo[i][j]);
			write(ESC);
			write(J);
			write(N144_FEED);
		}
	}

	private int[][] getLogo() {
		StringBuilder sb = new StringBuilder();
		Image image = new Image(this.getClass().getResourceAsStream("/image/logo.jpg"));
		int argb;
		int red;
		int width = (int) image.getWidth();
		int height = (int) image.getHeight();
		int yOffset = height % 8 / 2;
		int yLoop = height / 8;
		int[][] value = new int[yLoop][width];
		for (int i = 0; i < yLoop; i++) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < 8; y++) {
					argb = image.getPixelReader().getArgb(x, yOffset + y + i * 8);
					red = 0xFF & (argb >> 16);
					sb.append(red > 127 ? "0" : "1");
				}
				value[i][x] = Integer.parseInt(sb.toString(), 2);
			}
		}
		return value;
	}

	private void write(int i) throws InvalidException {
		try {
			output.write(i);
		} catch (IOException e) {
			logger.error(e);
			throw new InvalidException(e.getMessage());
		}
	}

	@Override
	public void printRight(String text) {
		println(leftPad(text, PAPER_WIDTH));
	}

	@Override
	public void printWide(String text) {
		println(center(text, LARGE_FONT_PAPER_WIDTH));
	}

	@Override
	public void setNormal() throws InvalidException {
		write(ESC);
		write(N);
		write(CHAR_PER_LINE);
		write(NARROW_42CPL);
		write(ESC);
		write(AT);
	}

	@Override
	public void setWide() throws InvalidException {
		write(ESC);
		write(EXCLAMATION);
		write(HUGE);
	}
}
