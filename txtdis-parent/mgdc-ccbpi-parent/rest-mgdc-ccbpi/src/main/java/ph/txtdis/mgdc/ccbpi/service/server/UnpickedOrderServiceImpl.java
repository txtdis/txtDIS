package ph.txtdis.mgdc.ccbpi.service.server;

import static org.apache.log4j.Logger.getLogger;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.PickListEntity;
import ph.txtdis.mgdc.ccbpi.repository.OrderConfirmationRepository;

@Service("unpickedOrderService")
public class UnpickedOrderServiceImpl implements UnpickedOrderService {

	private static Logger logger = getLogger(UnpickedOrderServiceImpl.class);

	@Autowired
	private PickListService pickListService;

	@Autowired
	private OrderConfirmationRepository ocsRepository;

	@Override
	public List<BillableEntity> list(String collector, LocalDate start, LocalDate end) {
		List<BillableEntity> allUnpickedOrders = ocsRepository.findByCustomerNotNullAndDueDateBetweenAndPickingNull(start, end);
		logger.info("\n    UnpickedOrderConfirmations@list = " + allUnpickedOrders);
		return filterCollector(allUnpickedOrders, collector, start, end);
	}

	private List<BillableEntity> filterCollector(List<BillableEntity> allUnpickedOrders, String collector, LocalDate start, LocalDate end) {
		try {
			return collector.isEmpty() ? allUnpickedOrders : unpickedOrders(allUnpickedOrders, collector, start, end);
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	private List<BillableEntity> unpickedOrders(List<BillableEntity> allUnpickedOrders, String collector, LocalDate start, LocalDate end) {
		List<BillableEntity> combinedUnpickedOrders = new ArrayList<>();
		for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1L))
			combinedUnpickedOrders.addAll(unpickedOrders(allUnpickedOrders, collector, date));
		return combinedUnpickedOrders;
	}

	private List<BillableEntity> unpickedOrders(List<BillableEntity> allUnpickedOrders, String collector, LocalDate date) {
		List<PickListEntity> pickLists = pickListService.list(collector, date, date);
		return pickLists == null ? Collections.emptyList() //
				: allUnpickedOrders.stream() //
						.filter(e -> e.getDueDate().isEqual(date) && areRoutesTheSame(pickLists, e)) //
						.collect(Collectors.toList());
	}

	private boolean areRoutesTheSame(List<PickListEntity> pickLists, BillableEntity b) {
		try {
			String route = b.getCustomer().getRoute().getName();
			logger.info("\n    CustomerRoute@areCollectorsTheSame = " + route);
			return route.equalsIgnoreCase(collector(pickLists));
		} catch (Exception e) {
			return false;
		}
	}

	private String collector(List<PickListEntity> pickLists) throws Exception {
		String collector = pickLists.stream() //
				.min((a, b) -> a.getId().compareTo(b.getId())) //
				.get().getBillings().get(0) //
				.getCustomer().getRoute().getName();
		logger.info("\n    PickListRoute = " + collector);
		return collector;
	}
}
