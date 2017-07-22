package ph.txtdis.mgdc.ccbpi.service;

import java.util.List;

public interface EmptiesService {

	boolean isEmpties();

	List<String> listEmpties();

	void setEmpties(boolean b);

	void setEmpties(String empties);
}
