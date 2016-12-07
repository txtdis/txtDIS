package ph.txtdis.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

import ph.txtdis.dto.AgingReceivableReport;
import ph.txtdis.dto.CreditNote;
import ph.txtdis.dto.CustomerReceivableReport;
import ph.txtdis.dto.InvoiceBooklet;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.dto.SalesVolume;

public abstract class AbstractCreditNoteAndInvoiceBookletAndReceivableTrackedWithItemTreeTypeMap
		extends AbstractClientTypeMap {

	private static final long serialVersionUID = -7813390170827910065L;

	public AbstractCreditNoteAndInvoiceBookletAndReceivableTrackedWithItemTreeTypeMap() {
		put("agingReceivable", new TypeReference("\ue900", new ParameterizedTypeReference<AgingReceivableReport>() {
		}));
		put("creditNote", new TypeReference("\ue93e", new ParameterizedTypeReference<CreditNote>() {
		}));
		put("creditNotes", new TypeReference(null, new ParameterizedTypeReference<List<CreditNote>>() {
		}));
		put("customerReceivable", new TypeReference("\ue900", new ParameterizedTypeReference<CustomerReceivableReport>() {
		}));
		put("invoiceBooklet", new TypeReference("\ue909", new ParameterizedTypeReference<InvoiceBooklet>() {
		}));
		put("invoiceBooklets", new TypeReference(null, new ParameterizedTypeReference<List<InvoiceBooklet>>() {
		}));
		put("itemTree", new TypeReference("\ue94d", new ParameterizedTypeReference<ItemTree>() {
		}));
		put("itemTrees", new TypeReference(null, new ParameterizedTypeReference<List<ItemTree>>() {
		}));
		put("salesRevenues", new TypeReference(null, new ParameterizedTypeReference<List<SalesRevenue>>() {
		}));
		put("salesVolumes", new TypeReference(null, new ParameterizedTypeReference<List<SalesVolume>>() {
		}));
	}
}
