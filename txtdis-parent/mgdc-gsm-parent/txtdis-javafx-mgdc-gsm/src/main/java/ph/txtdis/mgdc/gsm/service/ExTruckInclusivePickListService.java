package ph.txtdis.mgdc.gsm.service;

import ph.txtdis.exception.AlreadyAssignedPersonException;

import java.util.List;

public interface ExTruckInclusivePickListService {

	List<String> listCollectors();

	void setCollectorUponValidation(String collector) throws AlreadyAssignedPersonException;
}
