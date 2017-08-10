package ph.txtdis.mgdc.ccbpi.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.type.BeverageType;

import java.math.BigDecimal;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.log4j.Logger.getLogger;

@Service("remittanceVarianceService")
public class RemittanceVarianceServiceImpl //
	extends AbstractSalesItemVarianceService //
	implements RemittanceVarianceService {

	private static Logger logger = getLogger(RemittanceVarianceServiceImpl.class);

	@Autowired
	private CokeRemittanceService remittanceService;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	@Autowired
	private OrderConfirmationService orderConfirmationService;

	private BigDecimal loadOutValue, returnedValue, remittedValue;

	private List<SalesItemVariance> itemVariances;

	private String collector;

	public RemittanceVarianceServiceImpl() {
		super();
		loadOutValue = BigDecimal.ZERO;
		returnedValue = BigDecimal.ZERO;
		remittedValue = BigDecimal.ZERO;
		itemVariances = emptyList();
		collector = "ALL";
	}

	@Override
	public String getCollector() {
		return collector;
	}

	@Override
	public void setCollector(String name) {
		collector = name;
	}

	@Override
	public String getExpectedColumnName() {
		return "Load-out Qty";
	}

	@Override
	public String getReturnedColumnName() {
		return "Load-in Qty";
	}

	@Override
	public String getVarianceColumnName() {
		return "Delivered Qty";
	}

	@Override
	public String getVarianceQtyMethodName() {
		return "nonNegativeVarianceQtyInFractions";
	}

	@Override
	public String getHeaderName() {
		return "Remittance Variance";
	}

	@Override
	public BigDecimal getLoadOutValue() {
		return loadOutValue = orderConfirmationService.getDeliveredValue(collector(), getStartDate(), getEndDate());
	}

	private String collector() {
		return collector.equals("ALL") ? "" : collector;
	}

	@Override
	public BigDecimal getRemittanceVarianceValue() {
		return returnedValue.add(remittedValue).subtract(loadOutValue);
	}

	@Override
	public BigDecimal getRemittedValue() {
		return remittedValue = remittanceService.getTotalValue(collector(), getStartDate(), getEndDate());
	}

	@Override
	public BigDecimal getReturnedValue() {
		return returnedValue =
			itemVariances.stream().map(v -> v.getReturnedValue()).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Override
	public String getSubhead() {
		return collector + ": " + super.getSubhead();
	}

	@Override
	public List<BigDecimal> getTotals(List<SalesItemVariance> l) {
		return asList(unpickedQty(l), bookedQty(l), returnedQty(l), varianceQty(l), value(l));
	}

	private BigDecimal unpickedQty(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getOtherQty);
	}

	@Override
	protected BigDecimal returnedQty(List<SalesItemVariance> l) {
		return l.stream().filter(v -> isFullGoods(v)).map(SalesItemVariance::getReturnedQty)
			.reduce(ZERO, BigDecimal::add);
	}

	@Override
	protected BigDecimal varianceQty(List<SalesItemVariance> l) {
		return bookedQty(l).subtract(returnedQty(l));
	}

	@Override
	protected BigDecimal value(List<SalesItemVariance> l) {
		return total(l, SalesItemVariance::getReturnedValue);
	}

	private boolean isFullGoods(SalesItemVariance v) {
		try {
			return itemService.findByName(v.getItem()) //
				.getFamily().getName() //
				.equalsIgnoreCase(BeverageType.FULL_GOODS.toString());
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public List<SalesItemVariance> list() {
		try {
			String endPt = "/list?receivedFrom=" + collector() + "&start=" + getStartDate() + "&end=" + getEndDate();
			logger.info("\n    endPoint = " + endPt);
			return itemVariances = getRestClientServiceForLists().module(getModuleName()).getList(endPt);
		} catch (Exception e) {
			return emptyList();
		}
	}

	@Override
	public String getModuleName() {
		return "remittanceVariance";
	}
}
