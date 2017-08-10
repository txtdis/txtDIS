package ph.txtdis.mgdc.ccbpi.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Booking;
import ph.txtdis.exception.AlreadyAssignedPersonException;
import ph.txtdis.mgdc.service.AbstractPickListService;
import ph.txtdis.type.UserType;

import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

@Service("pickListService")
public class PickListServiceImpl //
	extends AbstractPickListService //
	implements CokePickListService {

	private static Logger logger = getLogger(PickListServiceImpl.class);

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	@Override
	public String getIconName() {
		return getTypeMap().icon("loadOut");
	}

	@Override
	public boolean isAppendable() {
		return isNew() && (getBookings() == null || getBookings().isEmpty());
	}

	@Override
	public List<String> listCollectors() {
		String c = get().getLeadAssistant();
		return c == null ? userService.listNamesByRole(UserType.COLLECTOR) : asList(c);
	}

	@Override
	protected List<Booking> listUnpicked() {
		List<Booking> bookings = orderConfirmationService.listUnpicked(getPickDate());
		logger.info("\n    Bookings@listUnpicked = " + bookings);
		return bookings;
	}

	@Override
	protected String routeName(Booking b) {
		return b.getDeliveryRoute();
	}

	@Override
	public void setAssistantUponValidation(String h) throws AlreadyAssignedPersonException {
		get().setAssistant(h);
	}

	@Override
	public void setCollector(String c) {
		get().setLeadAssistant(c);
	}

	@Override
	public void setDriverUponValidation(String d) throws AlreadyAssignedPersonException {
		get().setDriver(d);
	}
}
