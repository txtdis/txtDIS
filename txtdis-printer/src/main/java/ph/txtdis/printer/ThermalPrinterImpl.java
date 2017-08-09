package ph.txtdis.printer;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import ph.txtdis.exception.InvalidException;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;

import static javax.print.PrintServiceLookup.lookupPrintServices;
import static org.apache.commons.lang3.StringUtils.center;
import static org.apache.commons.lang3.StringUtils.leftPad;
import static org.apache.log4j.Logger.getLogger;

@Component("thermalPrinter")
public class ThermalPrinterImpl //
	implements ThermalPrinter {

	public static final int PAPER_WIDTH = 40;

	public static final int HALF_COLUMN = (PAPER_WIDTH / 2) - 2;

	public static final int LARGE_FONT_PAPER_WIDTH = PAPER_WIDTH / 2;

	public static final int SUBHEADER_LABEL_WIDTH = 9;

	private static final String PRINTER = "Local Thermal Printer";

	private static Logger logger = getLogger(ThermalPrinterImpl.class);

	private DocFlavor flavor;

	private DocPrintJob job;

	@Override
	public void build() throws InvalidException {
		flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
		job = createPrintJob(flavor);
	}

	private DocPrintJob createPrintJob(DocFlavor flavor) throws InvalidException {
		PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
		PrintService[] services = lookupPrintServices(flavor, pras);
		PrintService service = findPrintService(services);
		return service.createPrintJob();
	}

	private PrintService findPrintService(PrintService[] services) throws InvalidException {
		for (PrintService service : services)
			if (service.getName().equalsIgnoreCase(PRINTER))
				return service;
		throw new InvalidException(PRINTER + " not found.");
	}

	@Override
	public void cut() throws InvalidException {
		printLines(5);
		print(0x1d, 'V', 1);
		printLines(2);
	}

	@Override
	public void printLines(int lines) throws InvalidException {
		for (int i = 0; i < lines; i++)
			println("");
	}

	private void print(int esc, char c, int n) throws InvalidException {
		try {
			byte[] bytes = new byte[]{(byte) esc, (byte) c, (byte) n};
			print(bytes);
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			throw new InvalidException(e.getMessage());
		}
	}

	@Override
	public void println(String text) throws InvalidException {
		print(text + "\n");
	}

	private void print(byte[] bytes) throws InvalidException {
		try {
			Doc doc = new SimpleDoc(bytes, flavor, null);
			job.print(doc, null);
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			throw new InvalidException(e.getMessage());
		}
	}

	@Override
	public void print(String text) throws InvalidException {
		try {
			print(text.getBytes("CP437"));
		} catch (Exception e) {
			logger.error(e.getStackTrace());
			throw new InvalidException(e.getMessage());
		}
	}

	@Override
	public void printCenter(String text) throws InvalidException {
		println(center(text, PAPER_WIDTH));
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
	public void printEndOfPage() throws InvalidException {
		println(leftPad("", PAPER_WIDTH, "_"));
		printLines(4);
	}

	@Override
	public void printRight(String text) throws InvalidException {
		println(leftPad(text, PAPER_WIDTH));
	}

	@Override
	public void printWide(String text) throws InvalidException {
		println(center(text, LARGE_FONT_PAPER_WIDTH));
	}

	@Override
	public void setNormal() throws InvalidException {
		print(0x1b, '!', 0);
	}

	public void setSmall() throws InvalidException {
		print(0x1b, '!', 1);
	}

	public void setTall() throws InvalidException {
		print(0x1b, '!', 16);
	}

	@Override
	public void setWide() throws InvalidException {
		print(0x1b, '!', 32);
	}
}
