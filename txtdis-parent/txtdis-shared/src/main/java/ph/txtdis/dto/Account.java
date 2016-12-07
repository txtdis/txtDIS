package ph.txtdis.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Account extends AbstractCreationTracked<Long> implements Comparable<Account>, Keyed<Long> {

	private String seller;

	private LocalDate startDate;

	@Override
	public int compareTo(Account a) {
		return getStartDate().compareTo(a.getStartDate());
	}
}
