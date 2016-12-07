package ph.txtdis.service;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ph.txtdis.util.NumberUtils.isZero;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.CustomerEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.domain.ItemTreeEntity;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;
import ph.txtdis.repository.BillingRepository;
import ph.txtdis.repository.ItemTreeRepository;
import ph.txtdis.util.DateTimeUtils;

public abstract class AbstractSalesVolumeService implements SalesVolumeService, ReportUom {

	@Autowired
	private BillingRepository billingRepository;

	@Autowired
	private ItemTreeRepository itemTreeRepository;

	@Value("${vendor.id}")
	private String vendorId;

	@Override
	public List<SalesVolume> list(LocalDate startDate, LocalDate endDate)
			throws DateBeforeGoLiveException, EndDateBeforeStartException {
		List<BillableEntity> i = billingRepository
				.findByNumIdNotNullAndRmaNullAndCustomerIdNotAndOrderDateBetweenOrderByOrderDateAscPrefixAscNumIdAscSuffixAsc(
						vendorId(), //
						DateTimeUtils.verifyDateIsOnOrAfterGoLive(startDate, goLive()), //
						DateTimeUtils.validateEndDate(startDate, endDate, goLive()));
		return dataDump(i);
	}

	private Long vendorId() {
		return Long.valueOf(vendorId);
	}

	protected abstract LocalDate goLive();

	private String category(ItemFamilyEntity productLine) {
		if (productLine == null)
			return null;
		ItemTreeEntity t = itemTreeRepository.findByFamilyId(productLine.getId());
		return t.getParent().getName();
	}

	private String channelName(CustomerEntity c) {
		try {
			return c.getChannel().getName();
		} catch (Exception e) {
			return "NO CHANNEL";
		}
	}

	private List<SalesVolume> dataDump(List<BillableEntity> i) {
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

	private SalesVolume toDataDump(BillableDetailEntity d) {
		SalesVolume v = new SalesVolume();
		ItemEntity i = d.getItem();
		ItemFamilyEntity prodLine = i.getFamily();
		CustomerEntity c = d.getBilling().getCustomer();
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
}