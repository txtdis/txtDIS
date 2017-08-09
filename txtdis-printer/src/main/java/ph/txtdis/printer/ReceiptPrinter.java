package ph.txtdis.printer;

import ph.txtdis.exception.InvalidException;

public interface ReceiptPrinter {

	void build() throws InvalidException;

	void print(String text) throws InvalidException;

	void printCenter(String text) throws InvalidException;

	void printDashes(int count) throws InvalidException;

	void printDoubleDashes(int count) throws InvalidException;

	void printEndOfPage() throws InvalidException;

	void printLines(int lines) throws InvalidException;

	void println(String text) throws InvalidException;

	void printRight(String text) throws InvalidException;

	void printWide(String text) throws InvalidException;

	void setNormal() throws InvalidException;

	void setWide() throws InvalidException;
}