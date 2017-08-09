package ph.txtdis.mgdc.gsm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.Price;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.dto.Item;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.function.BinaryOperator.maxBy;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static ph.txtdis.util.NumberUtils.zeroIfNull;

@Service("pricedBillableService")
public class BillableServiceImpl //
	implements PricedBillableService {

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	private Map<Long, BigDecimal> itemDiscountMap;

	@Override
	public BillableDetail addPriceToDetail(boolean canAvailDiscount,
	                                       BillableDetail detail,
	                                       Customer customer,
	                                       LocalDate orderDate) {
		detail.setPriceValue(getPrice(canAvailDiscount, detail, customer, orderDate));
		return detail;
	}

	private BigDecimal getPrice(boolean canAvailDiscount,
	                            BillableDetail detail,
	                            Customer customer,
	                            LocalDate orderDate) {
		BigDecimal discount = canAvailDiscount ? getDiscountValue(detail) : BigDecimal.ZERO;
		return getLatestUnitPrice(detail, customer, orderDate).subtract(zeroIfNull(discount));
	}

	private BigDecimal getDiscountValue(BillableDetail d) {
		BigDecimal discount = itemDiscountMap.get(d.getId());
		return discount == null ? BigDecimal.ZERO : discount;
	}

	private BigDecimal getLatestUnitPrice(BillableDetail d, Customer c, LocalDate date) {
		Price p = getPrice(d, c.getPrimaryPricingType().getName(), date);
		return p.getPriceValue();
	}

	private Price getPrice(BillableDetail detail, String pricing, LocalDate orderDate) {
		Item i = getItem(detail);
		return i == null ? null
			: i.getPriceList().stream() //
			.filter(p -> p.getType().getName().equalsIgnoreCase(pricing) &&
				isApprovedAndStartDateIsNotInTheFuture(p, orderDate))
			.max(comparing(Price::getStartDate)).orElse(new Price());
	}

	protected Item getItem(BillableDetail d) {
		try {
			return itemService.findById(d.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void setItemDiscountMap(Customer c, LocalDate d) {
		itemDiscountMap = new HashMap<>();
		getLatestApprovedCustomerDiscountStream(c, d).collect(groupingBy(//
			CustomerDiscount::getItem, //
			reducing(maxBy(comparing(CustomerDiscount::getStartDate))))).entrySet().forEach(e -> itemDiscountMap.put(//
			e.getKey().getId(), //
			e.getValue().orElse(new CustomerDiscount()).getDiscount()));
	}

	private Stream<CustomerDiscount> getLatestApprovedCustomerDiscountStream(Customer c, LocalDate d) {
		try {
			return c.getDiscounts().stream().filter(p -> isApprovedAndStartDateIsNotInTheFuture(p, d));
		} catch (Exception e) {
			return Stream.empty();
		}
	}
}
