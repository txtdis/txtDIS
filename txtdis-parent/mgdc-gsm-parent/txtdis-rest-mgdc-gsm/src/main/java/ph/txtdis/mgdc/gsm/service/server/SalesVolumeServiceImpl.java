package ph.txtdis.mgdc.gsm.service.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.time.LocalDate.parse;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.NumberUtils.divide;

@Service("salesVolumeService")
public class SalesVolumeServiceImpl //
	extends AbstractSalesVolumeService {

	private static Logger logger = getLogger(SalesVolumeServiceImpl.class);

	@Autowired
	private QtyPerUomService uomService;

	@Value("${vendor.dis.go.live}")
	private String emdsGoLive;

	@Override
	protected LocalDate edmsGoLive() {
		return parse(emdsGoLive);
	}

	@Override
	protected BigDecimal reportQty(BillableDetailEntity d) {
		BigDecimal qty = divide(d.getFinalQty(), uomService.getItemQtyPerUom(d.getItem(), CS));
		logger.info("\n    reportQty: " + qty);
		return qty;
	}

	@Override
	protected BigDecimal unitQty(BillableDetailEntity d) {
		logger.info("\n    unitQty: " + d.getFinalQty());
		return d.getFinalQty();
	}
}