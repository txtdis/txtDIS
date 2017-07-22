package ph.txtdis.mgdc.domain;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractNamedCreatedKeyedEntity;
import ph.txtdis.dto.Named;

@Data
@Entity
@Table(name = "route")
@EqualsAndHashCode(callSuper = true)
public class RouteEntity //
		extends AbstractNamedCreatedKeyedEntity<Long> //
		implements Named {

	private static final long serialVersionUID = -593813397375404049L;

	@JoinColumn(name = "route_id")
	@OneToMany(cascade = ALL, fetch = EAGER)
	private List<AccountEntity> sellerHistory;

	public String getSeller(LocalDate date) {
		return getSellerHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.max(comparing(AccountEntity::getStartDate)) //
				.orElse(new AccountEntity()).getSeller();
	}

	public List<AccountEntity> getSellerHistory() {
		return sellerHistory == null ? emptyList() : sellerHistory;
	}
}
