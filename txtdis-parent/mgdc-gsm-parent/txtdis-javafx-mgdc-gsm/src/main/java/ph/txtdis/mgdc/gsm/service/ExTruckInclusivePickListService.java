package ph.txtdis.mgdc.gsm.service;

import java.util.List;

import ph.txtdis.exception.AlreadyAssignedPersonException;

public interface ExTruckInclusivePickListService {

	List<String> listCollectors();

	void setCollectorUponValidation(String collector) throws AlreadyAssignedPersonException;
}
