package ph.txtdis.service;

import java.util.List;

import ph.txtdis.exception.AlreadyAssignedPersonException;

public interface ExTruckPickListService {

	List<String> listCollectors();

	void setCollectorUponValidation(String collector) throws AlreadyAssignedPersonException;
}
