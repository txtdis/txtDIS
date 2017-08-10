package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

public interface EmptiesService {

	boolean isEmpties();

	void setEmpties(boolean b);

	void setEmpties(String empties);

	List<String> listEmpties();
}
