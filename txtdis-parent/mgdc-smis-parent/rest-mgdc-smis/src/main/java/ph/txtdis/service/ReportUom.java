package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;

import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.QtyPerUom;

public interface ReportUom {

	default QtyPerUom getReportUom(Item i) {
		return i.getQtyPerUomList().stream().filter(q -> q.getReported() != null && q.getReported() == true).findAny()
				.get();
	}

	default BigDecimal getReportUomQty(BillingDetail d) {
		return divide(d.getUnitQty(), getUnitQtyPerReportUom(d.getItem()));
	}

	default BigDecimal getUnitQtyPerReportUom(Item i) {
		try {
			return getReportUom(i).getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}
}
