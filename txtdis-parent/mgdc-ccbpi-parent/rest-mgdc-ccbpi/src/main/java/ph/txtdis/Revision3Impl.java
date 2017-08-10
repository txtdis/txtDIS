package ph.txtdis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ph.txtdis.domain.SyncEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.repository.CustomerRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static ph.txtdis.type.SyncType.BACKUP;
import static ph.txtdis.type.UserType.SYSGEN;
import static ph.txtdis.util.DateTimeUtils.toLocalDate;

@Component("revision3")
public class Revision3Impl //
	implements Revision3 {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private SyncRepository syncRepository;

	@Value("${go.live}")
	private String goLive;

	@Override
	@Transactional
	public void givePickUpDiscountsToSelectedCustomers() {
		Date date = DateTimeUtils.toUtilDate("2009-06-07");
		SyncEntity sync = backupSync();
		if (areDiscountsGivenAndRouteReplacementAndConversionNotDone(date, sync))
			givePickUpDiscountsToSelectedCustomersAndReplaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels(date,
				sync);
	}

	private SyncEntity backupSync() {
		return syncRepository.findOne(BACKUP);
	}

	private boolean areDiscountsGivenAndRouteReplacementAndConversionNotDone(Date date, SyncEntity sync) {
		LocalDate syncDate = toLocalDate(sync.getLastSync());
		LocalDate referenceDate = toLocalDate(date);
		return !syncDate.isEqual(referenceDate);
	}

	private void givePickUpDiscountsToSelectedCustomersAndReplaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels(
		Date date,
		SyncEntity sync) {
		givePickUpDiscountsToApprovedCustomers();
		syncRepository.save(updateSync(sync, date));
	}

	private void givePickUpDiscountsToApprovedCustomers() {
		List<Long> vendorIds =
			asList(13L, 21L, 26L, 29L, 35L, 504990464L, 503760889L, 503773439L, 504777816L, 504696211L);
		vendorIds.forEach(this::giveDiscountToCustomer);
	}

	private SyncEntity updateSync(SyncEntity sync, Date date) {
		sync.setLastSync(date);
		return sync;
	}

	private void giveDiscountToCustomer(Long vendorId) {
		CustomerEntity e = customerRepository.findByVendorId(vendorId);
		if (e != null)
			e.setCustomerDiscounts(customerDiscounts(e));
	}

	private List<CustomerDiscountEntity> customerDiscounts(CustomerEntity e) {
		CustomerDiscountEntity d = new CustomerDiscountEntity();
		d.setCustomer(e);
		d.setValue(new BigDecimal("3.00"));
		d.setStartDate(goLiveDate());
		d.setIsValid(true);
		d.setDecidedBy(SYSGEN.toString());
		d.setDecidedOn(ZonedDateTime.now());
		return singletonList(d);
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}
}
