package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractSalesRevenueService implements SalesRevenueService {

	@Autowired
	private BillingRepository billingRepository;

	@Value("${vendor.id}")
	private String vendorId;

	@Override
	public List<SalesRevenue> list(LocalDate startDate, LocalDate endDate)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		List<BillableEntity> i = billingRepository
				.findByNumIdNotNullAndRmaNullAndCustomerIdNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
						vendorId(), //
						DateTimeUtils.verifyDateIsOnOrAfterGoLive(startDate, goLive()), //
						DateTimeUtils.validateEndDate(startDate, endDate, goLive()));
		return salesRevenues(i);
	}

	private Long vendorId() {
		return Long.valueOf(vendorId);
	}

	protected abstract LocalDate goLive();

	private List<SalesRevenue> salesRevenues(List<BillableEntity> i) {
		try {
			return i.stream()//
					.collect(Collectors.groupingBy(BillableEntity::getCustomer, //
							mapping(BillableEntity::getTotalValue, reducing(ZERO, BigDecimal::add))))//
					.entrySet().stream().map(d -> toSalesRevenue(d))//
					.sorted().collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private SalesRevenue toSalesRevenue(Entry<CustomerEntity, BigDecimal> d) {
		CustomerEntity c = d.getKey();
		SalesRevenue r = new SalesRevenue();
		r.setId(c.getId());
		r.setSeller(c.getSeller());
		r.setCustomer(c.getName());
		r.setValue(getValue(d));
		return r;
	}

	protected BigDecimal getValue(Entry<CustomerEntity, BigDecimal> d) {
		BigDecimal v = d.getValue();
		return v != null ? v : BigDecimal.ZERO;
	}
}