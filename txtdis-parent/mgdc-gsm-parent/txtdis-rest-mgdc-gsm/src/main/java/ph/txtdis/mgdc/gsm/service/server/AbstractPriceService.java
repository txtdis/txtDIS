package ph.txtdis.mgdc.gsm.service.server;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Price;
import ph.txtdis.mgdc.domain.PriceEntity;
import ph.txtdis.mgdc.gsm.domain.ItemEntity;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.repository.PriceRepository;
import ph.txtdis.mgdc.service.server.PricingTypeService;
import ph.txtdis.type.ItemType;

public abstract class AbstractPriceService //
		implements PriceService {

	@Autowired
	private PriceRepository repository;

	@Autowired
	private PricingTypeService pricingService;

	@Override
	public BigDecimal getCurrentValue(String pricingType, ItemEntity i) {
		return getLatestValue(pricingType, i, LocalDate.now());
	}

	@Override
	public BigDecimal getLatestValue(String pricingType, ItemEntity i, LocalDate d) {
		PriceEntity price = lastest(pricingType, i, d);
		return price == null ? BigDecimal.ZERO : price.getPriceValue();
	}

	private PriceEntity lastest(String pricingType, ItemEntity i, LocalDate d) {
		if (i.getType() == ItemType.FREE)
			return null;
		List<PriceEntity> prices = i.getPriceList();
		return prices.stream() //
				.filter(p -> p.getType().toString().equals(pricingType) && p.getIsValid() == true && !p.getStartDate().isAfter(d))
				.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())) //
				.orElse(null);
	}

	@Override
	public List<PriceEntity> getNewAndOldPriceEntities(ItemEntity e, Item i) {
		List<PriceEntity> l = new ArrayList<>(e.getPriceList());
		l.addAll(toEntities(getNewPricesNeedingApproval(i)));
		return l.stream().distinct().collect(Collectors.toList());
	}

	@Override
	public List<Price> getNewPricesNeedingApproval(Item i) {
		return i.getPriceList().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	@Override
	public PriceEntity getRegularEntity(String pricingType, ItemEntity i) {
		return lastest(pricingType, i, LocalDate.MAX);
	}

	@Override
	public BigDecimal getRegularValue(String pricingType, ItemEntity i) {
		return getLatestValue(pricingType, i, LocalDate.MAX);
	}

	@Override
	public boolean hasDecisionOnNewPricingBeenMade(ItemEntity e, Item i) {
		return isDecisionOnAnANewPriceEntityNeeded(e) && hasDecisionOnANewPriceBeenMade(i);
	}

	private boolean isDecisionOnAnANewPriceEntityNeeded(ItemEntity e) {
		return e.getPriceList().stream().anyMatch(p -> p.getIsValid() == null);
	}

	private boolean hasDecisionOnANewPriceBeenMade(Item i) {
		return getNewPricesNeedingApproval(i).isEmpty();
	}

	@Override
	public List<PriceEntity> toEntities(List<Price> l) {
		return l == null ? null : l.stream().map(p -> toEntity(p)).collect(Collectors.toList());
	}

	private PriceEntity toEntity(Price p) {
		PriceEntity e = new PriceEntity();
		e.setPriceValue(p.getPriceValue());
		e.setStartDate(p.getStartDate());
		e.setType(pricingService.toEntity(p.getType()));
		return setDecisionData(e, p);
	}

	private PriceEntity setDecisionData(PriceEntity e, Price p) {
		e.setIsValid(p.getIsValid());
		e.setRemarks(p.getRemarks());
		e.setDecidedBy(p.getDecidedBy());
		e.setDecidedOn(decidedOn(e, p));
		return e;
	}

	public ZonedDateTime decidedOn(PriceEntity e, Price p) {
		if (p.getDecidedBy() != null && e.getDecidedOn() == null)
			return ZonedDateTime.now();
		return p.getDecidedOn();
	}

	@Override
	public List<Price> toList(List<PriceEntity> l) {
		return l == null ? null : l.stream().map(p -> toDTO(p)).collect(Collectors.toList());
	}

	private Price toDTO(PriceEntity e) {
		Price p = new Price();
		p.setPriceValue(e.getPriceValue());
		p.setStartDate(e.getStartDate());
		p.setType(pricingService.toModel(e.getType()));
		p.setIsValid(e.getIsValid());
		p.setRemarks(e.getRemarks());
		p.setDecidedBy(e.getDecidedBy());
		p.setDecidedOn(e.getDecidedOn());
		p.setCreatedBy(e.getCreatedBy());
		p.setCreatedOn(e.getCreatedOn());
		return p;
	}

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<PriceEntity> updatePricingDecisions(ItemEntity e, Item i) {
		return e.getPriceList().stream().map(p -> updateNewPricingDecisions(p, i)).collect(Collectors.toList());
	}

	private PriceEntity updateNewPricingDecisions(PriceEntity e, Item i) {
		Optional<Price> o = i.getPriceList().stream() //
				.filter(t -> areStartDatesAndPricingTypesAndChannelLimitsAllTheSame(e, t)) //
				.findAny();
		return o.isPresent() && e.getIsValid() == null ? setDecisionData(e, o.get()) : e;
	}

	private boolean areStartDatesAndPricingTypesAndChannelLimitsAllTheSame(PriceEntity e, Price i) {
		return i.getStartDate().isEqual(e.getStartDate()) //
				&& i.getType().getName().equals(e.getType().getName());
	}
}
