package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.QtyPerUomEntity;

public interface ReportUom {

	default QtyPerUomEntity getReportUom(ItemEntity i) {
		return i.getQtyPerUomList().stream().filter(q -> q.getReported() != null && q.getReported() == true).findAny()
				.get();
	}

	default BigDecimal getReportUomQty(BillableDetailEntity d) {
		return divide(d.getUnitQty(), getUnitQtyPerReportUom(d.getItem()));
	}

	default BigDecimal getUnitQtyPerReportUom(ItemEntity i) {
		try {
			return getReportUom(i).getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}
}
