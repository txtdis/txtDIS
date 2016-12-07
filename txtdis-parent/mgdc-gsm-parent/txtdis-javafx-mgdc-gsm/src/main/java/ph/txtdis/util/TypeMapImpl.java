package ph.txtdis.util;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Billable;

@Component("typeMap")
public class TypeMapImpl extends AbstractCreditNoteAndInvoiceBookletAndReceivableTrackedWithItemTreeTypeMap {

	private static final long serialVersionUID = -1782679034968493608L;

	public TypeMapImpl() {
		put("loadOrder", new TypeReference("\ue951", new ParameterizedTypeReference<Billable>() {
		}));
		put("siv", new TypeReference("\ue90d", new ParameterizedTypeReference<Billable>() {
		}));
	}
}
