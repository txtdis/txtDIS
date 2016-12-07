package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;

@Service("orderConfirmationService")
public class OrderConfirmationServiceImpl extends AbstractSpunBillableService implements OrderConfirmationService {

	private static final List<String> WAREHOUSE_SALES_AND_PARTIAL_DELIVERY = Arrays.asList("EWHS", "EPAR");

	private static Logger logger = getLogger(OrderConfirmationServiceImpl.class);

	@Override
	public Billable find(Long customerVendorId, LocalDate orderDate, Long orderNo) {
		BillableEntity b = repository.findByCustomerVendorIdAndNumIdAndSuffixNotNullAndOrderDate(customerVendorId,
				orderNo, orderDate);
		return toDTO(b);
	}

	@Override
	public Billable findByBookingId(Long id) {
		BillableEntity b = repository.findByBookingId(id);
		return toDTO(b);
	}

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository
				.findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndOrderDateOrderByIdAsc(d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public List<Billable> findUnpickedOn(LocalDate d) {
		List<BillableEntity> l = repository
				.findByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndPrefixNullAndSuffixNotInAndDueDateAndPickingNull(
						WAREHOUSE_SALES_AND_PARTIAL_DELIVERY, d);
		logger.info("\n    UnpickedOrderConfirmations = " + l);
		return toList(l);
	}

	@Override
	public List<BomEntity> getRouteGroupedBomList(LocalDate start, LocalDate end) {
		List<BillableEntity> l = repository
				.findByCustomerNotNullAndNumIdNotNullAndPrefixNullAndSuffixNotNullAndSuffixNotInAndDueDateBetween(
						WAREHOUSE_SALES_AND_PARTIAL_DELIVERY, start, end);
		logger.info("\n    OrderConfirmations = " + l);
		return l == null ? Collections.emptyList() : toRouteGroupedBomList(l);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc(id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullAndIdLessThanOrderByIdDesc(id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullOrderByIdAsc();
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerNotNullAndNumIdNotNullAndSuffixNotNullOrderByIdDesc();
		return toDTO(b);
	}
}