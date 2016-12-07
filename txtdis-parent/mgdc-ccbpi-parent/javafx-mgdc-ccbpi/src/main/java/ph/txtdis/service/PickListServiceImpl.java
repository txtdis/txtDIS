package ph.txtdis.service;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Booking;
import ph.txtdis.exception.AlreadyAssignedPersonException;
import ph.txtdis.type.UserType;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService implements CokePickListService {

	@Autowired
	private BookingService bookingService;

	@Override
	public List<String> listCollectors() {
		String c = get().getThirdPerson();
		if (c != null)
			return asList(c);
		return isNew() ? collectors() : null;
	}

	private List<String> collectors() {
		return userService.listNamesByRole(UserType.COLLECTOR);
	}

	@Override
	public boolean isAppendable() {
		return isNew();
	}

	@Override
	protected List<Booking> listUnpicked() {
		return bookingService.listUnpicked(getPickDate());
	}

	@Override
	public void setCollector(String c) {
		get().setThirdPerson(c);
	}

	@Override
	public void setDriverUponValidation(String d) throws AlreadyAssignedPersonException {
		get().setDriver(d);
	}

	@Override
	public void setHelperUponValidation(String h) throws AlreadyAssignedPersonException {
		get().setHelper(h);
	}
}
