package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.Routing;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Customer;
import ph.txtdis.mgdc.service.PricingTypeService;
import ph.txtdis.mgdc.service.RouteService;
import ph.txtdis.type.PartnerType;
import ph.txtdis.util.DateTimeUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ph.txtdis.type.PriceType.DEALER;

@Service("customerService")
public class CustomerServiceImpl //
	extends AbstractItemDeliveredCustomerService //
	implements CokeCustomerService {

	@Autowired
	private ChannelService channelService;

	@Autowired
	private PricingTypeService pricingTypeService;

	@Autowired
	private RouteService routeService;

	@Value("${go.live}")
	private String goLive;

	@Override
	public BigDecimal getCustomerDiscountValue(Long customerId, Long itemId, LocalDate dueDate) {
		try {
			return findByVendorId(customerId) //
				.getDiscounts().stream() //
				.filter(p -> !p.getStartDate().isAfter(dueDate) && p.getIsValid() == true) //
				.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())) //
				.get().getDiscount();
		} catch (Exception e) {
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<String> listRoutes() {
		return channelService.listNames();
	}

	@Override
	public List<String> listTruckRoutes() {
		return routeService.listNames();
	}

	@Override
	public Customer save(Customer c, Long vendorId, String name, String vendorRoute, String deliveryRoute)
		throws Exception {
		if (c == null)
			c = newCustomer(vendorId, name, vendorRoute);
		c.setRouteHistory(routes(deliveryRoute));
		return save(c);
	}

	private Customer newCustomer(Long vendorId, String name, String vendorRoute) throws Exception {
		Customer c = new Customer();
		c.setVendorId(vendorId);
		c.setName(name);
		c.setChannel(channel(vendorRoute));
		c.setPrimaryPricingType(dealerPrice());
		c.setType(PartnerType.OUTLET);
		return c;
	}

	private List<Routing> routes(String name) throws Exception {
		Routing r = new Routing();
		r.setRoute(routeService.findByName(name));
		r.setStartDate(goLive());
		return Arrays.asList(r);
	}

	private Channel channel(String name) {
		try {
			return channelService.findByName(name);
		} catch (Exception e) {
			return null;
		}
	}

	private PricingType dealerPrice() throws Exception {
		return pricingTypeService.findByName(DEALER.toString());
	}

	private LocalDate goLive() {
		return DateTimeUtils.toDate(goLive);
	}

	@Override
	public List<Customer> search(String name) throws Exception {
		List<Customer> l = super.search(name);
		return l == null ? null //
			: l.stream() //
			.filter(c -> c.getVendorId() != null) //
			.map(c -> setVendorIdAsId(c)) //
			.collect(Collectors.toList());
	}

	private Customer setVendorIdAsId(Customer c) {
		c.setId(c.getVendorId());
		return c;
	}

	@Override
	public List<Customer> list() {
		List<Customer> l = super.list();
		return l == null ? null : l.stream() //
			.filter(c -> c.getVendorId() != null).collect(Collectors.toList());
	}
}
