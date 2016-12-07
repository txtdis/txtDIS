package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.info.Information;

public interface InvoiceBookletService extends Listed<InvoiceBooklet>, SavedByEntity<InvoiceBooklet>, Titled {

	void checkForDuplicates(String prefix, Long id, String suffix) throws Exception;

	InvoiceBooklet find(String p, Long id, String s) throws Exception;

	int getLinesPerPage();

	boolean isOffSite();

	List<String> listUsers();

	InvoiceBooklet save(String prefix, String suffix, Long start, Long end, String issuedTo)
			throws Information, Exception;
}