package ph.txtdis.mgdc.ccbpi.service.server;

import static java.util.Collections.emptyList;
import static ph.txtdis.type.PriceType.DEALER;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Price;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.domain.PriceEntity;
import ph.txtdis.mgdc.repository.PriceRepository;
import ph.txtdis.mgdc.service.server.PricingTypeService;
import ph.txtdis.type.ItemType;

@Service("priceService")
public class PriceServiceImpl //
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
			.filter(
				p -> p.getType().toString().equals(pricingType) && p.getIsValid() == true && !p.getStartDate().isAfter(d))
			.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())) //
			.orElse(null);
	}

	@Override
	public List<PriceEntity> getNewAndOldPriceEntities(ItemEntity e, Item i) {
		List<PriceEntity> priceEntities = new ArrayList<>(e.getPriceList());
		List<Price> prices = getNewPricesNeedingApproval(i);
		List<LocalDate> dates = startAndEndDatesOfTheNewPrice(prices);
		return getNewAndOldPriceEntities(priceEntities, prices, dates);
	}

	@Override
	public List<Price> getNewPricesNeedingApproval(Item i) {
		return i.getPriceList().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	private List<LocalDate> startAndEndDatesOfTheNewPrice(List<Price> prices) {
		return prices == null ? emptyList()
			: prices.stream() //
			.filter(p -> p.getType().getName().equalsIgnoreCase(DEALER.toString()))//
			.map(p -> p.getStartDate()) //
			.sorted() //
			.collect(Collectors.toList());
	}

	private List<PriceEntity> getNewAndOldPriceEntities(List<PriceEntity> priceEntities,
	                                                    List<Price> prices,
	                                                    List<LocalDate> dates) {
		if (!dates.isEmpty() && dates.size() == 2)
			priceEntities = deletedPricesInBetweenTheStartAndEndDatesOfTheNewPrice(priceEntities, dates);
		priceEntities.addAll(toEntities(prices));
		return priceEntities.stream().distinct().collect(Collectors.toList());
	}

	private List<PriceEntity> deletedPricesInBetweenTheStartAndEndDatesOfTheNewPrice(List<PriceEntity> priceEntities,
	                                                                                 List<LocalDate> dates) {
		return priceEntities.stream()
			.filter(p -> !p.getStartDate().isBefore(dates.get(0)) && !p.getStartDate().isAfter(dates.get(1)))
			.collect(Collectors.toList());
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
		e.setRemarks(p.getRemarks());
		e.setIsValid(true);
		e.setDecidedBy("SYSGEN");
		e.setDecidedOn(ZonedDateTime.now());
		return e;
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

	public ZonedDateTime decidedOn(PriceEntity e, Price p) {
		if (p.getDecidedBy() != null && e.getDecidedOn() == null)
			return ZonedDateTime.now();
		return p.getDecidedOn();
	}

	@Override
	public List<Price> toModels(List<PriceEntity> l) {
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
