package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.mgdc.gsm.domain.*;
import ph.txtdis.mgdc.gsm.repository.BillableRepository;
import ph.txtdis.mgdc.gsm.repository.ItemTreeRepository;
import ph.txtdis.mgdc.service.server.SalesVolumeService;
import ph.txtdis.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.minBy;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.DateTimeUtils.validateEndDate;
import static ph.txtdis.util.DateTimeUtils.verifyDateIsOnOrAfterGoLive;

public abstract class AbstractSalesVolumeService //
	implements SalesVolumeService,
	ReportUom {

	@Autowired
	private BillableRepository billingRepository;

	@Autowired
	private ItemTreeRepository itemTreeRepository;

	@Value("${vendor.id}")
	private Long vendorId;

	@Override
	public List<SalesVolume> list(LocalDate startDate, LocalDate endDate) throws Exception {
		List<BillableEntity> i = billingRepository
			.findByNumIdNotNullAndRmaNullAndCustomerIdNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
//
				vendorId, //
				verifyDateIsOnOrAfterGoLive(startDate, edmsGoLive()), //
				validateEndDate(startDate, endDate, edmsGoLive()));
		return dataDump(i);
	}

	protected abstract LocalDate edmsGoLive();

	private List<SalesVolume> dataDump(List<BillableEntity> i) {
		try {
			return i.stream().flatMap(d -> d.getDetails().stream()) //
				.filter(d -> notInvalid(d)) //
				.map(d -> toDataDump(d)) //
				.filter(s -> !NumberUtils.isZero(s.getQty())) //
				.collect(toList());
		} catch (Exception e) {
			return emptyList();
		}
	}

	private Boolean notInvalid(BillableDetailEntity d) {
		Boolean isValid = d.getBilling().getIsValid();
		return isValid == null || isValid == true;
	}

	private SalesVolume toDataDump(BillableDetailEntity d) {
		ItemEntity i = d.getItem();
		ItemFamilyEntity prodLine = i.getFamily();
		BillableEntity b = d.getBilling();
		CustomerEntity c = b.getCustomer();
		LocalDate date = b.getOrderDate();

		SalesVolume v = new SalesVolume();
		v.setId(i.getId());
		v.setItem(i.getDescription());
		v.setProductLine(prodLine.getName());
		v.setCategory(category(prodLine));
		v.setQty(unitQty(d));
		v.setVol(reportQty(d));
		v.setUom(prodLine.getUom());
		v.setBillingNo(b.getOrderNo());
		v.setCustomer(c.getName());
		v.setCustomerStartDate(customerStartDate(c));
		v.setStreet(c.getStreet());
		v.setBarangay(barangay(c));
		v.setCity(city(c));
		v.setChannel(channelName(c));
		v.setSeller(c.getSeller(date));
		v.setDelivery(delivery(b));
		v.setOrderDate(date);
		v.setActive(c.getDeactivatedOn() == null);
		return v;
	}

	private String category(ItemFamilyEntity productLine) {
		try {
			return itemTreeRepository.findByFamilyId(productLine.getId()).getParent().getName();
		} catch (Exception e) {
			return null;
		}
	}

	protected BigDecimal unitQty(BillableDetailEntity d) {
		return d.getInitialQty();
	}

	protected BigDecimal reportQty(BillableDetailEntity d) {
		return getReportUomQty(d);
	}

	private LocalDate customerStartDate(CustomerEntity c) {
		return c.getRouteHistory().stream() //
			.collect(minBy(comparing(RoutingEntity::getStartDate))) //
			.orElse(new RoutingEntity()).getStartDate();
	}

	private String barangay(CustomerEntity c) {
		try {
			return c.getBarangay().getName();
		} catch (Exception e) {
			return null;
		}
	}

	private String city(CustomerEntity c) {
		try {
			return c.getCity().getName();
		} catch (Exception e) {
			return null;
		}
	}

	private String channelName(CustomerEntity c) {
		try {
			return c.getChannel().getName();
		} catch (Exception e) {
			return null;
		}
	}

	private String delivery(BillableEntity b) {
		try {
			return b.getPicking().getLeadAssistant().getName();
		} catch (Exception e) {
			return null;
		}
	}
}