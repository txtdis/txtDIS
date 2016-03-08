package ph.txtdis.dto;

import java.math.BigDecimal;

public interface CustomerCredit extends Comparable<CustomerCredit>, StartDated, Validated {

	int getTermInDays();

	void setTermInDays(int day);

	int getGracePeriodInDays();

	void setGracePeriodInDays(int day);

	BigDecimal getCreditLimit();

	void setCreditLimit(BigDecimal limit);
}
