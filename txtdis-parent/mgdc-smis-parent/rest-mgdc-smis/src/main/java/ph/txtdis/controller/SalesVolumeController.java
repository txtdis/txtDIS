package ph.txtdis.controller;

import static java.time.LocalDate.parse;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static ph.txtdis.util.NumberUtils.isZero;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.BillingDetail;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.ItemFamily;
import ph.txtdis.domain.ItemTree;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.CustomerRepository;
import ph.txtdis.repository.ItemTreeRepository;
import ph.txtdis.service.ReportUom;

@RequestMapping("/salesVolumes")
@RestController("salesVolumeController")
public class SalesVolumeController implements ReportUom {

	@Autowired
	private BillingRepository billingRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ItemTreeRepository itemTreeRepository;

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
		List<SalesVolume> v = dataDump(i);
		return new ResponseEntity<>(v, OK);
	}

	private String category(ItemFamily productLine) {
		if (productLine == null)
			return null;
		ItemTree t = itemTreeRepository.findByFamily(productLine);
		return t.getParent().getName();
	}

	private String channelName(Customer c) {
		try {
			return c.getChannel().getName();
		} catch (Exception e) {
			return "NO CHANNEL";
		}
	}

	private List<SalesVolume> dataDump(List<Billing> i) {
		try {
			return i.stream().flatMap(d -> d.getDetails().stream())//
					.map(b -> toDataDump(b))//
					.filter(s -> !isZero(s.getQty()))//
					.collect(toList());
		} catch (Exception e) {
			e.printStackTrace();
			return emptyList();
		}
	}

	private LocalDate startDate() {
		return parse(goLive);
	}

	private SalesVolume toDataDump(BillingDetail d) {
		SalesVolume v = new SalesVolume();
		Item i = d.getItem();
		ItemFamily prodLine = i.getFamily();
		Customer c = d.getBilling().getCustomer();

		v.setId(i.getId());
		v.setItem(i.getDescription());
		v.setProductLine(prodLine.getName());
		v.setCategory(category(prodLine));
		v.setQty(d.getUnitQty());
		v.setVol(getReportUomQty(d));
		v.setUom(prodLine.getUom());
		v.setCustomer(c.getName());
		v.setChannel(channelName(c));
		v.setSeller(c.getSeller());
		v.setOrderDate(d.getBilling().getOrderDate());
		return v;
	}

	private LocalDate toLocalDate(Date date) throws DateBeforeGoLiveException {
		LocalDate d = date.toLocalDate();
		if (d.isBefore(startDate()))
			throw new DateBeforeGoLiveException();
		return d;
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