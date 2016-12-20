package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.BillableDetailEntity;
import ph.txtdis.domain.BillableEntity;
import ph.txtdis.domain.ItemEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PartnerType;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("billingService")
public class BillingServiceImpl extends AbstractBillableService implements ImportedBillingService {

	private static Logger logger = getLogger(BillingServiceImpl.class);

	private static final String BILLABLE = "billable";

	@Autowired
	private ReadOnlyService<Billable> readOnlyService;

	@Autowired
	private SavingService<Billable> savingService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		post(readOnlyService.module(BILLABLE).getList());
	}

	@Override
	@Transactional
	public Billable save(Billable t) {
		try {
			logger.info("\n    BillableToSave: " + t);
			t = super.save(t);
			return savingService.module(BILLABLE).save(t);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected List<BillableDetailEntity> setDetails(BillableEntity e, Billable b) {
		List<BillableDetailEntity> details = super.setDetails(e, b);
		BillableEntity loadOrder = findByLoadOrderId(b.getBookingId());
		if (loadOrder != null)
			updateLoadOrderSoldQtys(loadOrder, details);
		return details;
	}

	private void updateLoadOrderSoldQtys( //
			BillableEntity loadOrder, //
			List<BillableDetailEntity> billingDetails) {
		Map<ItemEntity, BillableDetailEntity> loadOrderDetailMap = loadOrder.getDetails().stream()
				.collect(Collectors.toMap(BillableDetailEntity::getItem, Function.identity()));
		for (BillableDetailEntity billingDetail : billingDetails)
			loadOrderDetailMap = updateLoadOrderSoldQtys(loadOrderDetailMap, billingDetail);
		loadOrder.setDetails(loadOrderDetailMap.values().stream().collect(Collectors.toList()));
		repository.save(loadOrder);
	}

	private Map<ItemEntity, BillableDetailEntity> updateLoadOrderSoldQtys( //
			Map<ItemEntity, BillableDetailEntity> loadOrderDetailMap, //
			BillableDetailEntity billingDetail) {
		BillableDetailEntity loadOrderDetail = loadOrderDetailMap.get(billingDetail.getItem());
		if (loadOrderDetail != null)
			loadOrderDetailMap = updateLoadOrderSoldQty(loadOrderDetailMap, billingDetail, loadOrderDetail);
		return loadOrderDetailMap;
	}

	private Map<ItemEntity, BillableDetailEntity> updateLoadOrderSoldQty(
			Map<ItemEntity, BillableDetailEntity> loadOrderDetailMap, //
			BillableDetailEntity billingDetail, //
			BillableDetailEntity loadOrderDetail) {
		loadOrderDetail.setSoldQty(loadOrderDetail.getSoldQtyInDecimals().add(billingDetail.getFinalQtyInDecimals()));
		loadOrderDetailMap.put(loadOrderDetail.getItem(), loadOrderDetail);
		return loadOrderDetailMap;
	}

	private BillableEntity findByLoadOrderId(Long id) {
		return repository.findByCustomerTypeAndBookingId(PartnerType.EX_TRUCK, id);
	}
}