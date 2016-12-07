package ph.txtdis.service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;

public interface HelperService {

	String getCode(PickList p);

	String getCode(EdmsSeller s);
}
