package ph.txtdis.service;

import static java.math.BigDecimal.ZERO;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.NumberUtils.isPositive;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.Customer;
import ph.txtdis.domain.CustomerSalesVolume;
import ph.txtdis.domain.Item;
import ph.txtdis.domain.Price;
import ph.txtdis.domain.SalesOrder;
import ph.txtdis.domain.SalesOrderItem;
import ph.txtdis.domain.SmsLog;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.repository.CustomerSalesVolumeRepository;
import ph.txtdis.repository.ItemRepository;
import ph.txtdis.repository.SalesOrderRepository;

@Service("salesOrderService")
public class SalesOrderService {

	private static Logger logger = getLogger(SalesOrderService.class);

	@Value("${minimum.inventory.level}")
	private String minimumInventoryLevel;

	@Autowired
	private CustomerSalesVolumeRepository customerSalesVolumeRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private SalesOrderRepository salesOrderRepository;

	private SalesOrder salesOrder;

	public void processDecidedSalesOrder(SmsLog log, Customer c, String salesOrderId) throws NotFoundException {
		Long id = Long.valueOf(salesOrderId);
		SalesOrder so = salesOrderRepository.findByIdAndCustomer(id, c);
		if (so == null)
			throw new NotFoundException(c.getName() + " and/or S/O No." + salesOrderId);
		so.setSentLog(log);
		salesOrderRepository.save(so);
	}

	public void computeSalesOrderItemQuantitiesBasedOnStockOnHand(Customer customer, String itemSmsId,
			String stockOnHand) {
		// TODO Quantity for new products
		Item item = itemRepository.findBySmsId(itemSmsId.trim());
		CustomerSalesVolume v = customerSalesVolumeRepository.findByCustomerAndItem(customer, item);
		if (v == null)
			return;
		BigDecimal stockDifference = new BigDecimal(stockOnHand).subtract(minimumStock(v));
		logger.info("StockOnHand = " + stockOnHand);
		logger.info("StockDifference = " + stockDifference);
		if (isPositive(stockDifference))
			return;
		addSalesOrderItem(customer, item, stockDifference);

	}

	private BigDecimal minimumStock(CustomerSalesVolume v) {
		BigDecimal min = v.getAvgDailyQty()
				.multiply(new BigDecimal(minimumInventoryLevel).multiply(new BigDecimal("2")));
		logger.info("Item  = " + v.getItem().getName());
		logger.info("Avg Daily Qty = " + v.getAvgDailyQty());
		logger.info("Min Stock = " + min);
		return min;

	}

	private void addSalesOrderItem(Customer customer, Item item, BigDecimal stockDifference) {
		int qty = stockDifference.negate().round(new MathContext(0, RoundingMode.CEILING)).intValue();
		if (salesOrder == null)
			salesOrder = new SalesOrder(LocalDate.now(), customer);
		addSalesOrderItem(item, qty);
	}

	private void addSalesOrderItem(Item item, int qty) {
		List<SalesOrderItem> details = new ArrayList<>(salesOrder.getDetails());
		details.add(new SalesOrderItem(item, qty));
		salesOrder.setDetails(details);
	}

	public void setSentLog(SmsLog smsLog) {
		salesOrder.setSentLog(smsLog);
	}

	public SalesOrder save() {
		SalesOrder so = salesOrderRepository.save(salesOrder);
		if (so.getSentLog() != null)
			salesOrder = null;
		else
			salesOrder = so;
		return so;
	}

	public BigDecimal computeTotalAmount(SalesOrder so) {
		return so.getDetails().stream().map(d -> price(d).multiply(new BigDecimal(d.getQty()))).reduce(ZERO,
				BigDecimal::add);
	}

	private BigDecimal price(SalesOrderItem d) {
		try {
			return d.getItem().getPriceList().stream().max(Price::compareTo).get().getPriceValue();
		} catch (Exception e) {
			logger.info("Item w/o Price = " + d.getItem());
			return BigDecimal.ZERO;
		}
	}
}
