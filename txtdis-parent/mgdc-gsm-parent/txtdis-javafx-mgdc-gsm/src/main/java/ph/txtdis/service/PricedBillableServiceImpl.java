package ph.txtdis.service;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Customer;
import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.VolumeDiscount;

@Service("pricedBillableService")
public class PricedBillableServiceImpl implements PricedBillableService {

	private static Logger logger = getLogger(PricedBillableServiceImpl.class);

	@Autowired
	private ItemService itemService;

	private Map<Long, BigDecimal> itemDiscountMap;

	@Override
	public BillableDetail addPriceToDetail(BillableDetail detail, Customer customer, LocalDate orderDate) {
		detail.setPriceValue(getPrice(detail, customer, orderDate));
		return detail;
	}

	private BigDecimal getPrice(BillableDetail detail, Customer customer, LocalDate orderDate) {
		BigDecimal discount = getDiscountValue(detail);
		VolumeDiscount volDisc = itemService.getLatestVolumeDiscount(customer.getChannel(), orderDate);
		if (volDisc != null)
			discount = discount.add(getVolumeDiscountValue(volDisc, detail.getUnitQty()));
		return getLatestUnitPrice(detail, customer, orderDate).subtract(discount);
	}

	private BigDecimal getDiscountValue(BillableDetail d) {
		logger.info("\n    BillableDetail = " + d);
		logger.info("\n    ItemDiscountMap = " + itemDiscountMap);
		BigDecimal discount = itemDiscountMap.get(d.getId());
		return discount == null ? BigDecimal.ZERO : discount;
	}

	private BigDecimal getLatestUnitPrice(BillableDetail d, Customer c, LocalDate date) {
		Price p = getPrice(d, c.getPrimaryPricingType().getName(), date);
		if (p == null && c.getAlternatePricingType() != null)
			p = getPrice(d, c.getAlternatePricingType().getName(), date);
		return p.getPriceValue();
	}

	protected Item getItem(BillableDetail d) {
		try {
			return itemService.find(d.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Price getPrice(BillableDetail detail, String pricing, LocalDate orderDate) {
		Item i = getItem(detail);
		return i == null ? null
				: i.getPriceList().stream()
						.filter(p -> p.getType().getName().equalsIgnoreCase(pricing)
								&& isApprovedAndStartDateIsNotInTheFuture(p, orderDate))
						.max(Price::compareTo).orElse(new Price());
	}

	private BigDecimal getVolumeDiscountValue(VolumeDiscount volDisc, int qty) {
		return qty < volDisc.getCutoff() ? BigDecimal.ZERO : volDisc.getDiscount();
	}

	@Override
	public void setItemDiscountMap(Customer c, LocalDate d) {
		itemDiscountMap = new HashMap<>();
		getLatestApprovedCustomerDiscountStream(c, d)
				.collect(groupingBy(//
						CustomerDiscount::getItem, //
						reducing(maxBy(comparing(CustomerDiscount::getStartDate)))))
				.entrySet()
				.forEach(e -> itemDiscountMap.put(//
						e.getKey().getId(), //
						e.getValue().orElse(new CustomerDiscount()).getDiscount()));
	}

	private Stream<CustomerDiscount> getLatestApprovedCustomerDiscountStream(Customer c, LocalDate d) {
		try {
			return c.getCustomerDiscounts().stream().filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, d));
		} catch (Exception e) {
			return Stream.empty();
		}
	}
}
