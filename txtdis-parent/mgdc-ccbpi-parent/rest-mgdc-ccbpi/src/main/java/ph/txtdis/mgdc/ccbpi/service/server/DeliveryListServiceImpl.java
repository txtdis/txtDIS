package ph.txtdis.mgdc.ccbpi.service.server;

import static org.apache.log4j.Logger.getLogger;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.ccbpi.domain.BillableDetailEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.BomEntity;
import ph.txtdis.mgdc.ccbpi.repository.BillingDetailRepository;
import ph.txtdis.mgdc.ccbpi.repository.DeliveryListRepository;

@Service("deliveryListService")
public class DeliveryListServiceImpl //
	extends AbstractSpunSavedBillableService //
	implements DeliveryListService {

	private static Logger logger = getLogger(DeliveryListServiceImpl.class);

	@Autowired
	private BillingDetailRepository detailRepository;

	@Autowired
	private DeliveryListRepository ddlRepository;

	@Override
	public Billable find(LocalDate date, String route) {
		BillableEntity b =
			ddlRepository.findFirstByCustomerNullAndOrderDateAndSuffixNotNullAndSuffixContaining(date, route);
		return toModel(b);
	}

	@Override
	public List<BomEntity> getBomList(String route, LocalDate start, LocalDate end) {
		List<BillableEntity> l = deliveryList(route, start, end);
		return l == null ? Collections.emptyList() : toBomList(l);
	}

	private List<BillableEntity> deliveryList(String route, LocalDate start, LocalDate end) {
		return ddlRepository.findByCustomerNullAndSuffixNotNullAndSuffixContainingAndOrderDateBetween( //
			routeName(route), start, end);
	}

	private String routeName(String route) {
		return route.equalsIgnoreCase("ALL") ? "" : route;
	}

	@Override
	public List<BillableDetailEntity> getDetailEntityList(String itemVendorNo,
	                                                      String route,
	                                                      LocalDate start,
	                                                      LocalDate end) {
		List<BillableDetailEntity> list = detailRepository
			.findByItemVendorIdAndBillingCustomerNullAndBillingSuffixNotNullAndBillingSuffixContainingAndBillingOrderDateBetween(
				//
				itemVendorNo, routeName(route), start, end);
		logger.info("\n    DeliveryListDetails = " + list);
		return list == null ? Collections.emptyList() : list;
	}

	@Override
	protected BillableEntity firstEntity() {
		return ddlRepository.findFirstByCustomerNullAndSuffixNotNullOrderByIdAsc();
	}

	@Override
	protected BillableEntity nextEntity(Long id) {
		return ddlRepository.findFirstByCustomerNullAndSuffixNotNullAndIdGreaterThanOrderByIdAsc(id);
	}

	@Override
	protected BillableEntity lastEntity() {
		return ddlRepository.findFirstByCustomerNullAndSuffixNotNullOrderByIdDesc();
	}

	@Override
	protected BillableEntity previousEntity(Long id) {
		return ddlRepository.findFirstByCustomerNullAndSuffixNotNullAndIdLessThanOrderByIdDesc(id);
	}
}