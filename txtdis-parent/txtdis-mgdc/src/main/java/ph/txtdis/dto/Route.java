package ph.txtdis.dto;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Route //
		extends AbstractCreationTracked<Long> //
		implements Named {

	private List<Account> sellerHistory;

	private String name;

	public String getAssignedBy() {
		return getAccount() == null ? null : getAccount().getCreatedBy();
	}

	public ZonedDateTime getAssignedOn() {
		return getAccount() == null ? null : getAccount().getCreatedOn();
	}

	public String getSeller() {
		Account account = getAccount();
		return account == null ? null : account.getSellerFullName();
	}

	private Account getAccount() {
		return getAccount(LocalDate.now());
	}

	private Account getAccount(LocalDate date) {
		return getSellerHistory() == null ? null : getLatestAccount(date);
	}

	private Account getLatestAccount(LocalDate date) {
		return getSellerHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.collect(maxBy(comparing(Account::getStartDate)))//
				.orElse(new Account());
	}

	public String getSeller(LocalDate date) {
		Account account = getAccount(date);
		return account == null ? null : account.getSeller();
	}

	public String getSellerFullName() {
		Account account = getAccount();
		return account == null ? null : account.getSellerFullName();
	}

	public String getSellerFullName(LocalDate date) {
		Account account = getAccount(date);
		return account == null ? null : account.getSellerFullName();
	}

	public List<Account> getSellerHistory() {
		return sellerHistory == null ? Collections.emptyList() : sellerHistory;
	}

	@Override
	public String toString() {
		return name;
	}
}
