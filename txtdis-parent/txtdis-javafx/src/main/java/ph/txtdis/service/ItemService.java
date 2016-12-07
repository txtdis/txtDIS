package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Bom;
import ph.txtdis.dto.Channel;
import ph.txtdis.dto.Item;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.dto.QtyPerUom;
import ph.txtdis.dto.VolumeDiscount;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.UomType;
import ph.txtdis.type.VolumeDiscountType;

public interface ItemService extends ByNameSearchable<Item>, DateValidated, DecisionNeeded, Excel<Item>, Reset,
		ItemBasedService<Bom>, Serviced<Long>, ServiceDeactivated<Long> {

	Bom createBom(BigDecimal qty);

	VolumeDiscount createDiscountUponValidation(VolumeDiscountType type, UomType uom, Integer cutoff,
			BigDecimal discount, Channel channel, LocalDate startDate) throws Exception;

	Price createPricingUponValidation(PricingType type, BigDecimal price, Channel channel, LocalDate startDate)
			throws Exception;

	QtyPerUom createQtyPerUom(UomType uom, BigDecimal qty, Boolean isPurchased, Boolean isSold, Boolean isReported);

	Item findByName(String name) throws Exception;

	Item findByVendorId(Long id) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException;

	String getDescription();

	LocalDate getEndOfLife();

	String getLastModifiedBy();

	ZonedDateTime getLastModifiedOn();

	VolumeDiscount getLatestVolumeDiscount(Channel channel, LocalDate date);

	String getName();

	List<Price> getPriceList();

	BigDecimal getQtyPerUom(Item item, UomType uom);

	ItemType getType();

	String getVendorId();

	List<VolumeDiscount> getVolumeDiscounts();

	boolean hasPurchaseUom();

	boolean hasReportUom();

	boolean hasSoldUom();

	boolean isNotDiscounted();

	boolean isOffSite();

	boolean isPurchased();

	List<Bom> listBoms();

	List<UomType> listBuyingUoms(Item item) throws Exception;

	List<ItemFamily> listParents();

	List<PricingType> listPricingTypes();

	List<QtyPerUom> listQtyPerUom();

	List<UomType> listSellingUoms();

	List<UomType> listSellingUoms(Item item) throws Exception;

	List<ItemType> listTypes();

	List<UomType> listUoms();

	boolean noChangesNeedingApproval(String tab);

	void setBoms(List<Bom> l);

	void setDescription(String text);

	void setEndOfLife(LocalDate d);

	void setFamily(ItemFamily f);

	void setName(String text);

	void setNameIfUnique(String text) throws Exception;

	void setNotDiscounted(boolean b);

	void setPartUponValidation(Long id) throws Exception;

	void setPriceList(List<Price> items);

	void setQtyPerUomList(List<QtyPerUom> list);

	void setType(ItemType t);

	void setVendorId(String id);

	void setVolumeDiscounts(List<VolumeDiscount> items);

	void validatePurchasedUom(Boolean b) throws Exception;

	void validateReportedUom(Boolean value) throws Exception;
}