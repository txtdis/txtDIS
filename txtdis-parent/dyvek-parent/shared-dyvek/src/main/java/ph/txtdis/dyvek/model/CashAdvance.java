package ph.txtdis.dyvek.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractKeyed;
import ph.txtdis.dto.Keyed;

import java.math.BigDecimal;
import java.time.LocalDate;

import static ph.txtdis.util.NumberUtils.zeroIfNull;
import static ph.txtdis.util.TextUtils.blankIfNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CashAdvance //
	extends AbstractKeyed<Long> //
	implements Keyed<Long> {

	private LocalDate checkDate;

	private String customer, bank;

	private Long checkId;

	private BigDecimal totalValue, balanceValue;

	public BigDecimal getBalanceValue() {
		return zeroIfNull(balanceValue);
	}

	public BigDecimal getTotalValue() {
		return zeroIfNull(totalValue);
	}

	@Override
	public String toString() {
		return blankIfNull(getId());
	}
}
