package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.InvoiceBooklet;

public interface InvoiceBookletService extends IdService<InvoiceBooklet, Long> {

	InvoiceBooklet findById(String prefix, Long id, String suffix);

	InvoiceBooklet linesPerPage();

	List<InvoiceBooklet> list();
}