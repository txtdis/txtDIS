package ph.txtdis.printer;

import ph.txtdis.exception.InvalidException;

public interface ThermalPrinter
	extends ReceiptPrinter {

	void cut() throws InvalidException;
}