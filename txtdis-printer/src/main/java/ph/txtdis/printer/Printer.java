package ph.txtdis.printer;

import ph.txtdis.exception.InvalidException;

public interface Printer<T> {

	void print(ReceiptPrinter printer, T entity) throws InvalidException;
}