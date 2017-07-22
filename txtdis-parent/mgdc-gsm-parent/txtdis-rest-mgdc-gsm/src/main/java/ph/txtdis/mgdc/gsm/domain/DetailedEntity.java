package ph.txtdis.mgdc.gsm.domain;

import java.util.List;

public interface DetailedEntity {

	List<? extends ItemQuantifiedEntityDetail> getDetails();
}
