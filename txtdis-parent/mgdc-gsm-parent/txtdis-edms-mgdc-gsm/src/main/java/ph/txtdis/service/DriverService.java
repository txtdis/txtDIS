package ph.txtdis.service;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.PickList;

public interface DriverService {

	String getCode(EdmsSeller s);

	String getCode(PickList p);

	String getName(PickList p);
}
