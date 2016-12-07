package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.table.BookingVarianceTable;
import ph.txtdis.service.BookingVarianceService;

@Scope("prototype")
@Component("bookingVarianceApp")
public class BookingVarianceAppImpl
		extends AbstractTotaledReportApp<BookingVarianceTable, BookingVarianceService, SalesItemVariance>
		implements BookingVarianceApp {

	@Override
	protected int noOfTotalDisplays() {
		return 4;
	}
}
