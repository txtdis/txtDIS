package ph.txtdis.dto;

import static org.apache.commons.lang3.math.Fraction.getFraction;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.Fraction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static java.math.BigDecimal.ZERO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemList extends AbstractId<Long> {

	private Item item;

	private BigDecimal priceValue;

	private Integer initialCount, returnedCount;

	@JsonIgnore
	public Fraction getInitialFraction() {
		return initialCount == null || bottlePerCase() == 0 ? Fraction.ZERO
				: getFraction(initialCount, bottlePerCase());
	}

	public long getItemCode() {
		return item == null ? null : item.getCode();
	}

	public String getItemName() {
		return item == null ? null : item.getName();
	}

	@JsonIgnore
	public Fraction getNetFraction() {
		int i = netCount();
		return i < 0 || bottlePerCase() == 0 ? Fraction.ZERO : getFraction(i, bottlePerCase());
	}

	@JsonIgnore
	public Fraction getReturnedFraction() {
		return returnedCount == null || bottlePerCase() == 0 ? Fraction.ZERO
				: getFraction(returnedCount, bottlePerCase());
	}

	public BigDecimal getSubtotalValue() {
		return price().multiply(new BigDecimal(netCount()));
	}

	private int bottlePerCase() {
		return item.getBottlePerCase();
	}

	private int initialCount() {
		return initialCount == null ? 0 : initialCount;
	}

	private int netCount() {
		return initialCount() - returnedCount();
	}

	private BigDecimal price() {
		return priceValue == null ? ZERO : priceValue;
	}

	private int returnedCount() {
		return returnedCount == null ? 0 : returnedCount;
	}
}
