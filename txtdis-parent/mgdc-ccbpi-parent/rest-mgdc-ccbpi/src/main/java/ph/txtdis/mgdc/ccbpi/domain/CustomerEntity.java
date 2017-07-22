package ph.txtdis.mgdc.ccbpi.domain;

import static java.time.LocalDate.now;
import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.txtdis.mgdc.domain.AbstractCustomerEntity;
import ph.txtdis.mgdc.domain.RouteEntity;

@Entity
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", //
		indexes = { //
				@Index(columnList = "name"), //
				@Index(columnList = "type"), //
				@Index(columnList = "vendor_id") //
		})
public class CustomerEntity //
		extends AbstractCustomerEntity {

	private static final long serialVersionUID = 4005115994591616684L;

	@Column(name = "vendor_id")
	private Long vendorId;

	@ManyToOne
	private ChannelEntity channel;

	@OneToMany(mappedBy = "customer", cascade = ALL)
	@OrderBy("startDate DESC, familyLimit DESC, level ASC")
	private List<CustomerDiscountEntity> customerDiscounts;

	@OneToMany(mappedBy = "customer", cascade = ALL)
	@OrderBy("startDate DESC")
	private List<RoutingEntity> routeHistory;

	public RouteEntity getRoute() {
		return getRoute(now());
	}

	public RouteEntity getRoute(LocalDate date) {
		return getRouteHistory().stream() //
				.filter(p -> !p.getStartDate().isAfter(date)) //
				.max(comparing(RoutingEntity::getStartDate)) //
				.orElse(new RoutingEntity()).getRoute();
	}

	public List<RoutingEntity> getRouteHistory() {
		return routeHistory == null ? emptyList() : routeHistory;
	}

	public String getSeller() {
		return getSeller(now());
	}

	public String getSeller(LocalDate d) {
		RouteEntity r = getRoute(d);
		return r == null ? null : r.getSeller(d);
	}

	@Override
	public String toString() {
		return getName();
	}
}
