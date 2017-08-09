package ph.txtdis.printer;

import ph.txtdis.exception.InvalidException;

public interface CDRKingPrinter //
	extends ReceiptPrinter {

	void printLogo() throws InvalidException;
}