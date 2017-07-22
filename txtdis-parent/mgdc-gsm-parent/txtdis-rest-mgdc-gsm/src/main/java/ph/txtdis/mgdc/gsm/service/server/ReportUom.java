package ph.txtdis.mgdc.gsm.service.server;

import static java.math.BigDecimal.ZERO;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;

import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.domain.QtyPerUomEntity;

public interface ReportUom {

	default QtyPerUomEntity getReportUom(ItemEntity i) {
		return i.getQtyPerUomList().stream().filter(q -> q.getReported() == true).findAny().get();
	}

	default BigDecimal getReportUomQty(BillableDetailEntity d) {
		return divide(d.getInitialQty(), getUnitQtyPerReportUom(d.getItem()));
	}

	default BigDecimal getUnitQtyPerReportUom(ItemEntity i) {
		try {
			return getReportUom(i).getQty();
		} catch (Exception e) {
			return ZERO;
		}
	}
}
