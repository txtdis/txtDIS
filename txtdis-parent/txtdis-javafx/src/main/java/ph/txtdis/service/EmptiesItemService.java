package ph.txtdis.service;

import java.util.List;

public interface EmptiesItemService extends ItemService {

	List<String> listEmpties();

	void setEmpties(String empties);
}
