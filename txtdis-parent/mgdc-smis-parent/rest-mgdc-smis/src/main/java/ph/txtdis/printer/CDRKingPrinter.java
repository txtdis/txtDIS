package ph.txtdis.printer;

import static gnu.io.CommPortIdentifier.PORT_SERIAL;
import static gnu.io.SerialPort.DATABITS_8;
import static gnu.io.SerialPort.PARITY_NONE;
import static gnu.io.SerialPort.STOPBITS_1;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;

import org.springframework.beans.factory.annotation.Value;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;

public abstract class CDRKingPrinter<T> {

	private static final byte M = (byte) 0;

	private static final byte N144_FEED = (byte) 16;

	private static final byte N2 = (byte) 0;

	private static final char ASTERISK = 42;

	private static final char AT = 64;

	private static final char CHAR_PER_LINE = 4;

	private static final char DLE = 16;

	private static final char ESC = 27;

	private static final char EOT = 4;

	private static final char EXCLAMATION = 33;

	private static final char HUGE = 0b0011_0001;

	private static final char J = 74;

	private static final char N = 78;

	private static final char NARROW_42CPL = 1;

	private static final char PRINTER_STATUS = 1;

	protected static final int PAPER_WIDTH = 40;

	protected OutputStream output;

	protected InputStream input;

	protected PrintStream printer;

	protected SerialPort serial;

	@Value("${com.port}")
	private String comPort;

	protected T entity;

	public void print(T entity) throws Exception {

		this.entity = entity;

		Enumeration<?> portIdentifiers = CommPortIdentifier.getPortIdentifiers();
		CommPortIdentifier portId = null;
		String portName = null;

		while (portIdentifiers.hasMoreElements()) {
			portId = (CommPortIdentifier) portIdentifiers.nextElement();
			if (portId.getPortType() == PORT_SERIAL && portId.getName().equals(comPort)) {
				portName = portId.getName();
				break;
			}
		}

		try {
			if (portName == null) {
				throw new Exception(comPort + " cannot be found;\n"
						+ "ensure printer is on and plugged in to said port,\nthen reboot server");
			} else {
				serial = portId.open(portName, 100);
				serial.setSerialPortParams(9600, DATABITS_8, STOPBITS_1, PARITY_NONE);
				input = serial.getInputStream();
				output = serial.getOutputStream();
				printer = new PrintStream(output, true);
				print();
			}
		} catch (PortInUseException e) {
			e.printStackTrace();
			throw new Exception(comPort + " is in use;\nclose other server apps.");
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
			throw new Exception("Printer must be\na CDRKing brand.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("No signal from printer;\nrestart it and try again.");
		} finally {
			try {
				if (input != null)
					input.close();
				if (output != null)
					output.close();
				if (printer != null)
					printer.close();
				if (serial != null)
					serial.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("Cannot reset printer; restart all");
			}
		}
	}

	protected abstract void print() throws Exception;

	protected void printLarge() throws IOException {
		output.write(ESC);
		output.write(EXCLAMATION);
		output.write(HUGE);
	}

	protected void printLogo(int[][] value) throws IOException {
		for (int i = 0; i < value.length; i++) {
			int width = value[i].length;
			output.write(ESC);
			output.write(ASTERISK);
			output.write(M);
			output.write((byte) width); // n1
			output.write(N2);
			for (int j = 0; j < width; j++)
				output.write((byte) value[i][j]);
			output.write(ESC);
			output.write(J);
			output.write(N144_FEED);
		}
	}

	protected void printNormal() throws IOException {
		output.write(ESC);
		output.write(N);
		output.write(CHAR_PER_LINE);
		output.write(NARROW_42CPL);
		output.write(ESC);
		output.write(AT);
		printer.println();
	}

	protected void waitForPrintingToEnd() throws IOException, InterruptedException {
		for (int i = 0; i < 10; i++) {
			int buffer = serial.getOutputBufferSize();
			if (buffer == 0) {
				output.write(DLE);
				output.write(EOT);
				output.write(PRINTER_STATUS);
			}
			Thread.sleep(1000);
		}
	}
}
