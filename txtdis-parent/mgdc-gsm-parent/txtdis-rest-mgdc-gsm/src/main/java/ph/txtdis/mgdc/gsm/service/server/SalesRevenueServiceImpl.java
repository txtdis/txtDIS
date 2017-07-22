package ph.txtdis.mgdc.gsm.service.server;

import static java.time.LocalDate.parse;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("salesRevenueService")
public class SalesRevenueServiceImpl //
		extends AbstractSalesRevenueService {

	@Value("${vendor.dis.go.live}")
	private String edmsGoLive;

	@Override
	protected LocalDate edmsGoLive() {
		return parse(edmsGoLive);
	}
}