package ph.txtdis.mgdc.ccbpi.domain;

import java.util.List;

public interface DetailedEntity {

	List<? extends ItemQuantifiedEntityDetail> getDetails();
}
