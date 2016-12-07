package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("bookingVarianceService")
public class BookingVarianceServiceImpl extends AbstractSalesItemVarianceService implements BookingVarianceService {

	@Override
	public String getHeaderText() {
		return "Booking Variance";
	}

	@Override
	public String getModule() {
		return "bookingVariance";
	}

	@Override
	public String getActualHeader() {
		return "DDL";
	}

	@Override
	public String getExpectedHeader() {
		return "OCS";
	}
}
