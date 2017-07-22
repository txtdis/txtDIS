package ph.txtdis.mgdc.gsm.service.server;

import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static ph.txtdis.type.UomType.CS;
import static ph.txtdis.util.NumberUtils.divide;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.mgdc.gsm.domain.BillableDetailEntity;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.repository.BillingDetailRepository;
import ph.txtdis.mgdc.gsm.repository.CustomerDiscountRepository;

@Service("customerDiscountService")
public class CustomerDiscountServiceImpl //
		implements CustomerDiscountService {

	@Autowired
	private BillingDetailRepository detailRepository;

	@Autowired
	private CustomerDiscountRepository repository;

	@Autowired
	private QtyPerUomService uomService;

	@Autowired
	private ItemService itemService;

	@Override
	public void cancelDiscountsOfOutletsWithAverageMonthlySalesBelowRequiredQty( //
			BigDecimal noOfmonths, BigDecimal requiredQty, List<BillableEntity> billings) {
		if (billings != null)
			billings.stream() //
					.collect(groupingBy( //
							BillableEntity::getCustomer, //
							Collectors.toList())) //
					.entrySet().stream() //
					.collect(toMap( //
							Map.Entry::getKey, //
							e -> qtyInCases(e)))
					.entrySet().stream() //
					.filter(s -> isBelowRequiredQty(s, noOfmonths, requiredQty)) //
					.flatMap(s -> repository.findByCustomerAndIsValidTrue(s.getKey()).stream()) //
					.forEach(d -> cancel(d));
	}

	private BigDecimal qtyInCases(Entry<CustomerEntity, List<BillableEntity>> e) {
		return e.getValue().stream() //
				.flatMap(b -> detailRepository.findByBilling(b).stream()) //
				.map(d -> qtyInCases(d)) //
				.reduce(ZERO, BigDecimal::add);
	}

	private BigDecimal qtyInCases(BillableDetailEntity d) {
		BigDecimal qtyPerCase = uomService.getItemQtyPerUom(d.getItem(), CS);
		return divide(d.getFinalQty(), qtyPerCase);
	}

	private boolean isBelowRequiredQty(Entry<CustomerEntity, BigDecimal> s, BigDecimal noOfmonths, BigDecimal requiredQty) {
		return divide(s.getValue(), noOfmonths).compareTo(requiredQty) < 0;
	}

	private void cancel(CustomerDiscountEntity d) {
		d.setIsValid(false);
		d.setRemarks("REVOKED: BELOW REQD QTY/MONTH");
		d.setDecidedBy("SUB-PAR STT");
		d.setDecidedOn(ZonedDateTime.now());
		repository.save(d);
	}

	@Override
	public List<CustomerDiscountEntity> getNewAndOldCustomerDiscounts(CustomerEntity e, Customer c) {
		List<CustomerDiscountEntity> l = new ArrayList<>(e.getCustomerDiscounts());
		l.addAll(toEntities(newCustomerDiscountsNeedingApproval(c), e));
		return l;
	}

	private List<CustomerDiscount> newCustomerDiscountsNeedingApproval(Customer c) {
		return c.getDiscounts().stream().filter(p -> p.getIsValid() == null).collect(toList());
	}

	@Override
	public boolean hasDecisionOnNewCustomerDiscountsBeenMade(CustomerEntity e, Customer c) {
		return isDecisionOnAnANewCustomerDiscountEntityNeeded(e) && hasDecisionOnANewCustomerDiscountBeenMade(c);
	}

	private boolean isDecisionOnAnANewCustomerDiscountEntityNeeded(CustomerEntity e) {
		return e.getCustomerDiscounts().stream().anyMatch(p -> p.getIsValid() == null);
	}

	private boolean hasDecisionOnANewCustomerDiscountBeenMade(Customer c) {
		return newCustomerDiscountsNeedingApproval(c).isEmpty();
	}

	@Override
	public List<CustomerDiscountEntity> toEntities(List<CustomerDiscount> l, CustomerEntity c) {
		return l == null ? null : l.stream().map(d -> toEntity(d, c)).collect(toList());
	}

	private CustomerDiscountEntity toEntity(CustomerDiscount d, CustomerEntity c) {
		CustomerDiscountEntity e = findSavedEntity(d);
		if (e == null)
			e = newEntity(d, c);
		return setDecisionData(e, d);
	}

	private CustomerDiscountEntity setDecisionData(CustomerDiscountEntity e, CustomerDiscount d) {
		e.setIsValid(d.getIsValid());
		e.setRemarks(d.getRemarks());
		e.setDecidedBy(d.getDecidedBy());
		e.setDecidedOn(decidedOn(d));
		return e;
	}

	private ZonedDateTime decidedOn(CustomerDiscount d) {
		return d.getIsValid() == null ? null : ZonedDateTime.now();
	}

	private CustomerDiscountEntity findSavedEntity(CustomerDiscount d) {
		Long id = d.getId();
		return id == null ? null : repository.findOne(id);
	}

	private CustomerDiscountEntity newEntity(CustomerDiscount d, CustomerEntity c) {
		CustomerDiscountEntity e = new CustomerDiscountEntity();
		e.setCustomer(c);
		e.setItem(itemService.toEntity(d.getItem()));
		e.setValue(d.getDiscount());
		e.setStartDate(d.getStartDate());
		return e;
	}

	@Override
	public List<CustomerDiscount> toModels(List<CustomerDiscountEntity> l) {
		return l == null ? null : l.stream().map(e -> toModel(e)).collect(Collectors.toList());
	}

	private CustomerDiscount toModel(CustomerDiscountEntity e) {
		CustomerDiscount d = new CustomerDiscount();
		d.setId(e.getId());
		d.setItem(itemService.toModel(e.getItem()));
		d.setIsValid(e.getIsValid());
		d.setDiscount(e.getValue());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		return d;
	}

	@Override
	public List<CustomerDiscountEntity> updateCustomerDiscountDecisions(CustomerEntity e, Customer c) {
		return e.getCustomerDiscounts().stream() //
				.map(d -> updateNewCustomerDiscountDecisions(d, c)) //
				.collect(toList());
	}

	private CustomerDiscountEntity updateNewCustomerDiscountDecisions(CustomerDiscountEntity e, Customer c) {
		Optional<CustomerDiscount> o = c.getDiscounts().stream() //
				.filter(t -> areStartDatesAndItemsTheSame(e, t)) //
				.findAny();
		return o.isPresent() && e.getIsValid() == null ? setDecisionData(e, o.get()) : e;
	}

	private boolean areStartDatesAndItemsTheSame(CustomerDiscountEntity e, CustomerDiscount d) {
		return e.getStartDate().isEqual(d.getStartDate()) && e.getItem().getId().equals(d.getItem().getId());
	}

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}
}
