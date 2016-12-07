package ph.txtdis.service;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Booking;
import ph.txtdis.exception.AlreadyAssignedPersonException;
import ph.txtdis.type.UserType;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService implements ExTruckPickListService {

	@Autowired
	private SalesOrderService salesOrderService;

	@Override
	public List<String> listCollectors() {
		String s = get().getThirdPerson();
		if (s != null)
			return asList(s);
		return isNew() ? collectors() : null;
	}

	private List<String> collectors() {
		return userService.listNamesByRole(UserType.COLLECTOR);
	}

	@Override
	protected List<Booking> listUnpicked() {
		return salesOrderService.listUnpicked(getPickDate());
	}

	@Override
	public void setCollectorUponValidation(String collector) throws AlreadyAssignedPersonException {
		if (collector == null || collector.isEmpty() || !isNew())
			return;
		if (hasPersonBeenAssignedInOtherTrucks(collector))
			throw new AlreadyAssignedPersonException(collector);
		get().setThirdPerson(collector);
	}
}
