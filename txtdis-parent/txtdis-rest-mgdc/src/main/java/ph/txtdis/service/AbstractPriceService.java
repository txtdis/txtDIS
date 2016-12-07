package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.PriceEntity;
import ph.txtdis.domain.PricingTypeEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.Price;
import ph.txtdis.repository.PriceRepository;

public abstract class AbstractPriceService implements PriceService {

	@Autowired
	private PriceRepository repository;

	@Autowired
	private PrimaryChannelService channelService;

	@Autowired
	private PricingTypeService pricingService;

	@Override
	public boolean decisionOnNewPricingMade(ItemEntity e, Item t) {
		return pendingPriceEntitiesExist(e) && decisionOnNewPricingMade(t);
	}

	private boolean pendingPriceEntitiesExist(ItemEntity e) {
		return !getPendingPriceEntities(e).isEmpty();
	}

	private List<PriceEntity> getPendingPriceEntities(ItemEntity e) {
		return e.getPriceList().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	private boolean decisionOnNewPricingMade(Item t) {
		return getPendingPrices(t).isEmpty();
	}

	@Override
	public BigDecimal getLatest(String pricingType, ItemEntity i) {
		try {
			PricingTypeEntity pricing = pricingService.findEntityByName(pricingType);
			return i.getPriceList().stream()
					.filter(p -> p.getType().equals(pricing) && p.getIsValid() != null && p.getIsValid())
					.max((a, b) -> a.getStartDate().compareTo(b.getStartDate())).get().getPriceValue();
		} catch (Exception e) {
			e.printStackTrace();
			return BigDecimal.ZERO;
		}
	}

	@Override
	public List<Price> getPendingPrices(Item i) {
		return i.getPriceList().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	@Override
	public List<PriceEntity> getUpdatedPricingDecisions(ItemEntity e, Item t) {
		return e.getPriceList().stream().map(p -> updateNewPricingDecisions(p, t)).collect(Collectors.toList());
	}

	private PriceEntity updateNewPricingDecisions(PriceEntity e, Item i) {
		Optional<Price> o = i.getPriceList().stream()//
				.filter(t -> equalStartDatesAndPricingTypesAndChannelLimits(e, t))//
				.findAny();
		if (o.isPresent() && e.getIsValid() == null) {
			Price p = o.get();
			e.setIsValid(p.getIsValid());
			e.setRemarks(p.getRemarks());
			e.setDecidedBy(p.getDecidedBy());
			e.setDecidedOn(ZonedDateTime.now());
		}
		return e;
	}

	private boolean equalStartDatesAndPricingTypesAndChannelLimits(PriceEntity e, Price t) {
		return t.getStartDate().isEqual(e.getStartDate()) && t.getType().getName().equals(e.getType().getName())
				&& channelService.areEqual(e.getChannelLimit(), t.getChannelLimit());
	}

	@Override
	public List<PriceEntity> newPrices(ItemEntity e, Item i) {
		List<PriceEntity> l = new ArrayList<>(e.getPriceList());
		l.addAll(toEntities(getPendingPrices(i)));
		return l;
	}

	@Override
	public List<PriceEntity> toEntities(List<Price> l) {
		return l == null ? null : l.stream().map(p -> toEntity(p)).collect(Collectors.toList());
	}

	private PriceEntity toEntity(Price p) {
		PriceEntity e = new PriceEntity();
		e.setPriceValue(p.getPriceValue());
		e.setStartDate(p.getStartDate());
		e.setChannelLimit(channelService.toEntity(p.getChannelLimit()));
		e.setType(pricingService.toEntity(p.getType()));
		e.setIsValid(p.getIsValid());
		e.setDecidedBy(p.getDecidedBy());
		e.setDecidedOn(p.getDecidedOn());
		return e;
	}

	@Override
	public List<Price> toList(List<PriceEntity> l) {
		return l == null ? null : l.stream().map(p -> toDTO(p)).collect(Collectors.toList());
	}

	private Price toDTO(PriceEntity e) {
		Price p = new Price();
		p.setPriceValue(e.getPriceValue());
		p.setStartDate(e.getStartDate());
		p.setChannelLimit(channelService.toDTO(e.getChannelLimit()));
		p.setType(pricingService.toDTO(e.getType()));
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
}
