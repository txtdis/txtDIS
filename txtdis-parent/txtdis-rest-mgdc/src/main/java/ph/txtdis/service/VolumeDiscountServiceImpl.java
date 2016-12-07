package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemEntity;
import ph.txtdis.domain.VolumeDiscountEntity;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.repository.VolumeDiscountRepository;

@Service("volumeDiscountService")
public class VolumeDiscountServiceImpl implements VolumeDiscountService {

	@Autowired
	private VolumeDiscountRepository repository;

	@Autowired
	private PrimaryChannelService channelService;

	@Override
	public void updateDecisionData(String[] s) {
		updateDecisionData(repository, s);
	}

	@Override
	public List<VolumeDiscountEntity> toEntities(List<VolumeDiscount> l) {
		return l == null ? null : l.stream().map(d -> toEntity(d)).collect(Collectors.toList());
	}

	private VolumeDiscountEntity toEntity(VolumeDiscount t) {
		VolumeDiscountEntity d = new VolumeDiscountEntity();
		d.setChannelLimit(channelService.toEntity(t.getChannelLimit()));
		d.setCutoff(t.getCutoff());
		d.setDiscountValue(t.getDiscount());
		d.setIsValid(t.getIsValid());
		d.setRemarks(t.getRemarks());
		d.setStartDate(t.getStartDate());
		d.setType(t.getType());
		d.setUom(t.getUom());
		if (t.getDecidedBy() != null && t.getDecidedOn() == null) {
			d.setDecidedBy(t.getDecidedBy());
			d.setDecidedOn(ZonedDateTime.now());
		}
		return d;
	}

	@Override
	public List<VolumeDiscount> toList(List<VolumeDiscountEntity> l) {
		return l == null ? null : l.stream().map(d -> toDTO(d)).collect(Collectors.toList());
	}

	private VolumeDiscount toDTO(VolumeDiscountEntity e) {
		VolumeDiscount d = new VolumeDiscount();
		d.setId(e.getId());
		d.setChannelLimit(channelService.toDTO(e.getChannelLimit()));
		d.setCutoff(e.getCutoff());
		d.setDiscount(e.getDiscountValue());
		d.setIsValid(e.getIsValid());
		d.setRemarks(e.getRemarks());
		d.setStartDate(e.getStartDate());
		d.setType(e.getType());
		d.setUom(e.getUom());
		d.setDecidedBy(e.getDecidedBy());
		d.setDecidedOn(e.getDecidedOn());
		d.setCreatedBy(e.getCreatedBy());
		d.setCreatedOn(e.getCreatedOn());
		return d;
	}

	@Override
	public boolean decisionOnNewVolumeDiscountMade(ItemEntity e, Item t) {
		return pendingVolumeDiscountsExist(e) && decisionOnNewPricingMade(t);
	}

	private boolean decisionOnNewPricingMade(Item t) {
		return getPendingVolumeDiscounts(t).isEmpty();
	}

	private boolean pendingVolumeDiscountsExist(ItemEntity e) {
		return !getPendingVolumeDiscounts(e).isEmpty();
	}

	private List<VolumeDiscountEntity> getPendingVolumeDiscounts(ItemEntity e) {
		return e.getVolumeDiscounts().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	private List<VolumeDiscount> getPendingVolumeDiscounts(Item t) {
		return t.getVolumeDiscounts().stream().filter(p -> p.getIsValid() == null).collect(Collectors.toList());
	}

	@Override
	public List<VolumeDiscountEntity> newDiscounts(ItemEntity e, Item t) {
		List<VolumeDiscountEntity> l = new ArrayList<>(e.getVolumeDiscounts());
		l.addAll(toEntities(getPendingVolumeDiscounts(t)));
		return l;
	}

	@Override
	public List<VolumeDiscountEntity> getUpdatedVolumeDiscountDecisions(ItemEntity e, Item t) {
		return e.getVolumeDiscounts().stream().map(p -> updateNewPricingDecisions(p, t)).collect(Collectors.toList());
	}

	private VolumeDiscountEntity updateNewPricingDecisions(VolumeDiscountEntity e, Item i) {
		Optional<VolumeDiscount> o = i.getVolumeDiscounts().stream()//
				.filter(t -> equalStartDatesAndChannelLimits(e, t))//
				.findAny();
		if (o.isPresent() && e.getIsValid() == null) {
			VolumeDiscount p = o.get();
			e.setIsValid(p.getIsValid());
			e.setRemarks(p.getRemarks());
			e.setDecidedBy(p.getDecidedBy());
			e.setDecidedOn(ZonedDateTime.now());
		}
		return e;
	}

	private boolean equalStartDatesAndChannelLimits(VolumeDiscountEntity e, VolumeDiscount t) {
		return t.getStartDate().isEqual(e.getStartDate())
				&& channelService.areEqual(e.getChannelLimit(), t.getChannelLimit());
	}
}
