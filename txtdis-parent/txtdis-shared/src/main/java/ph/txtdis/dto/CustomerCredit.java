package ph.txtdis.dto;

import java.math.BigDecimal;

public interface CustomerCredit //
		extends ForApproval, Comparable<CustomerCredit>, Keyed<Long>, StartDated {

	int getTermInDays();

	void setTermInDays(int day);

	int getGracePeriodInDays();

	void setGracePeriodInDays(int day);

	BigDecimal getCreditLimit();

	void setCreditLimit(BigDecimal limit);
}
