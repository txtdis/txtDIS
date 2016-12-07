package ph.txtdis.util;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.dto.SalesforceSalesInfo;

@Component("typeMap")
public class TypeMapImpl extends AbstractCreditNoteAndInvoiceBookletAndReceivableTrackedWithItemTreeTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public TypeMapImpl() {
		put("salesforceAccounts", new TypeReference(null, new ParameterizedTypeReference<List<SalesforceAccount>>() {
		}));
		put("salesforceSalesInfos", new TypeReference(null, new ParameterizedTypeReference<List<SalesforceSalesInfo>>() {
		}));
	}
}
