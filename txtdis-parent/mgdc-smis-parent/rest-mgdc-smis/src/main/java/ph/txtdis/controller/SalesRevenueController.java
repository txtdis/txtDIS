package ph.txtdis.controller;

import static java.time.LocalDate.parse;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static java.math.BigDecimal.ZERO;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.dto.SalesRevenue;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;

@RequestMapping("/salesRevenues")
@RestController("salesRevenueController")
public class SalesRevenueController {

	@Autowired
	private BillingRepository billingRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Value("${go.live}")
	private String goLive;

	@Value("${vendor.id}")
	private String vendorId;

	private Customer vendor;

	@RequestMapping(path = "/list", method = GET)
	public ResponseEntity<?> list(@RequestParam("start") Date s, @RequestParam("end") Date e)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {

		LocalDate start = toLocalDate(s);
		LocalDate end = validateEndDate(start, e);

		List<Billing> i = billingRepository
				.findByNumIdNotNullAndRmaNullAndCustomerNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
						vendor(), start, end);
		List<SalesRevenue> v = salesRevenues(i);
		return new ResponseEntity<>(v, OK);
	}

	private List<SalesRevenue> salesRevenues(List<Billing> i) {
		try {
			return i.stream()//
					.collect(Collectors.groupingBy(Billing::getCustomer, //
							mapping(Billing::getTotalValue, reducing(ZERO, BigDecimal::add))))//
					.entrySet().stream().map(d -> toSalesRevenue(d))//
					.sorted().collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private LocalDate startDate() {
		return parse(goLive);
	}

	private LocalDate toLocalDate(Date date) throws DateBeforeGoLiveException {
		LocalDate d = date.toLocalDate();
		if (d.isBefore(startDate()))
			throw new DateBeforeGoLiveException();
		return d;
	}

	private SalesRevenue toSalesRevenue(Entry<Customer, BigDecimal> d) {
		Customer c = d.getKey();
		SalesRevenue r = new SalesRevenue();
		r.setId(c.getId());
		r.setSeller(c.getSeller());
		r.setCustomer(c.getName());
		r.setValue(d.getValue());
		return r;
	}

	private LocalDate validateEndDate(LocalDate startDate, Date endDate)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		LocalDate end = toLocalDate(endDate);
		if (end.isBefore(startDate))
			throw new EndDateBeforeStartException();
		return end;
	}

	private Customer vendor() {
		if (vendor == null)
			vendor = customerRepository.findOne(Long.valueOf(vendorId));
		return vendor;
	}
}