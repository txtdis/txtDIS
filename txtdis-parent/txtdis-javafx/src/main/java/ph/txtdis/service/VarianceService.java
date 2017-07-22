package ph.txtdis.service;

import ph.txtdis.dto.Keyed;

public interface VarianceService<T extends Keyed<Long>> //
		extends ReportService<T> {

	default String getActualColumnName() {
		return "DDL";
	}

	default String getActualQtyMethodName() {
		return "actualQtyInFractions";
	}

	default String getBeginningColumnName() {
		return "Beginning";
	}

	default String getExpectedColumnName() {
		return "OCS";
	}

	default String getExpectedQtyMethodName() {
		return "expectedQtyInFractions";
	}

	default String getOtherColumnName() {
		return "Unpicked Qty";
	}

	default String getOtherQtyMethodName() {
		return "otherQtyInFractions";
	}

	default String getReturnedColumnName() {
		return "R/R";
	}

	default String getReturnedQtyMethodName() {
		return "returnedQtyInFractions";
	}

	default String getVarianceColumnName() {
		return "Under / (Over)";
	}

	default String getVarianceQtyMethodName() {
		return "varianceQtyInFractions";
	}
}
