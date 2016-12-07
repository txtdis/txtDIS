package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.BomEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.type.TransactionDirectionType;

@Service("deliveryListService")
public class DeliveryListServiceImpl extends AbstractSpunBillableService implements DeliveryListService {

	private static Logger logger = getLogger(DeliveryListServiceImpl.class);

	@Override
	public Billable findByDate(Date d) {
		BillableEntity b = repository
				.findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndOrderDateOrderByIdAsc(d.toLocalDate());
		return toDTO(b);
	}

	@Override
	public Billable find(Long shipment, String route) {
		BillableEntity b = repository.findByCustomerNullAndNumIdAndSuffix(shipment, route);
		return toDTO(b);
	}

	@Override
	public List<BomEntity> getBomList(LocalDate start, LocalDate end) {
		List<BillableEntity> l = getDeliveryList(start, end);
		logger.info("\n    DeliveryLists = " + l);
		return l == null ? Collections.emptyList() : toBomList(TransactionDirectionType.OUTGOING, l);
	}

	private List<BillableEntity> getDeliveryList(LocalDate start, LocalDate end) {
		return repository.findByCustomerNullAndNumIdNotNullAndSuffixNotNullAndOrderDateBetween(start, end);
	}

	@Override
	public List<BomEntity> getRouteGroupedBomList(LocalDate start, LocalDate end) {
		List<BillableEntity> l = getDeliveryList(start, end);
		logger.info("\n    RoutedDeliveryLists = " + l);
		return l == null ? Collections.emptyList() : toRouteGroupedBomList(l);
	}

	@Override
	public Billable next(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc(id);
		return toDTO(b);
	}

	@Override
	public Billable previous(Long id) {
		BillableEntity b = repository
				.findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullAndIdLessThanOrderByIdDesc(id);
		return toDTO(b);
	}

	@Override
	protected Billable firstSpun() {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullOrderByIdAsc();
		return toDTO(b);
	}

	@Override
	protected Billable lastSpun() {
		BillableEntity b = repository.findFirstByCustomerNullAndNumIdNotNullAndSuffixNotNullOrderByIdDesc();
		return toDTO(b);
	}
}