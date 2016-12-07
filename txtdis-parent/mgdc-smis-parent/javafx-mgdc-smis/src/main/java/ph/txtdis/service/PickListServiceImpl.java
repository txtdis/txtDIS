package ph.txtdis.service;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Booking;
import ph.txtdis.exception.AlreadyAssignedPersonException;

@Service("pickListService")
public class PickListServiceImpl extends AbstractPickListService implements PreSellPickListService {

	@Autowired
	private BookingService bookingService;

	@Override
	public List<String> listAsstHelpers() {
		return get().getHelper() == null ? allHelpers() : asstHelpers();
	}

	private List<String> asstHelpers() {
		String s = get().getThirdPerson();
		return s == null ? null : asList(s);
	}

	@Override
	protected List<Booking> listUnpicked() {
		return bookingService.listUnpicked(getPickDate());
	}

	@Override
	public void setAsstHelperUponValidation(String asstHelper) throws AlreadyAssignedPersonException {
		setThirdPersonUponValidation(asstHelper);
	}
}
