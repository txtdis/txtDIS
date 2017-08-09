package ph.txtdis.mgdc.ccbpi.service;

import static java.util.Collections.emptyList;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.dto.SalesItemVariance;

@Service("bookingVarianceService")
public class BookingVarianceServiceImpl //
	extends AbstractSalesItemVarianceService //
	implements BookingVarianceService {

	private String route;

	@Override
	public String getHeaderName() {
		return "Booking Variance";
	}

	@Override
	public String getSubhead() {
		return getRoute() + " on " + super.getSubhead();
	}

	@Override
	public String getRoute() {
		return route == null ? "ALL" : route;
	}

	@Override
	public void setRoute(String name) {
		route = name;
	}

	@Override
	public List<SalesItemVariance> list() {
		try {
			String endPt = "/list?route=" + getRoute() + "&start=" + getStartDate() + "&end=" + getEndDate();
			return getRestClientServiceForLists().module(getModuleName()).getList(endPt);
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public String getModuleName() {
		return "bookingVariance";
	}

	@Override
	public void reset() {
		super.reset();
		route = null;
	}
}
