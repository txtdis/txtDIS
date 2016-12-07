package ph.txtdis.fx.table;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.service.BookingVarianceService;

@Scope("prototype")
@Component("bookingVarianceTable")
public class BookingVarianceTableImpl extends AbstractVarianceTable<BookingVarianceService>
		implements BookingVarianceTable {

	@Override
	protected void addProperties() {
		menu.setMenu(service, this);
	}
}
