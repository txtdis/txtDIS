package ph.txtdis.mgdc.ccbpi.service;

import ph.txtdis.dto.Bom;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.mgdc.ccbpi.dto.Channel;
import ph.txtdis.mgdc.ccbpi.dto.Item;
import ph.txtdis.mgdc.service.ItemService;
import ph.txtdis.mgdc.service.ValidatedUomService;
import ph.txtdis.service.*;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.PriceType;
import ph.txtdis.type.UomType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BommedDiscountedPricedValidatedItemService //
	extends DateValidated,
	DecisionNeededService,
	SavableAsExcelService<Item>,
	ItemBasedService<Bom>,
	MasterService<Item>,
	AppendableDetailService,
	EmptiesService,
	ItemService,
	LatestApproved,
	ListedAndResettableService<Item>,
	ValidatedUomService,
	VendorIdBasedService {

	Bom createBom(BigDecimal qty);

	Price createPricingUponValidation( //
	                                   PricingType type, //
	                                   BigDecimal price, //
	                                   Channel channel, //
	                                   LocalDate startDate//
	) throws Exception;

	Item findByVendorNo( //
	                     String id) throws Exception;

	BigDecimal getCurrentPriceValue( //
	                                 Long id, //
	                                 LocalDate orderDate, //
	                                 PriceType type);

	PricingType getDealerPricingType();

	LocalDate getEndOfLife();

	void setEndOfLife( //
	                   LocalDate d);

	List<Price> getPriceList();

	void setPriceList( //
	                   List<Price> l);

	PricingType getPurchasePricingType();

	BigDecimal getQtyPerUom( //
	                         Item item, //
	                         UomType uom);

	BigDecimal getRegularPriceValue( //
	                                 Long itemId, //
	                                 PriceType type);

	ItemType getType();

	void setType( //
	              ItemType t);

	boolean hasReportUom();

	boolean hasSoldUom();

	@Override
	default boolean isNew() {
		return MasterService.super.isNew();
	}

	boolean isNotDiscounted();

	void setNotDiscounted( //
	                       boolean b);

	boolean isNotDiscounted( //
	                         String itemVendorNo);

	List<Bom> listBoms();

	List<UomType> listBuyingUoms( //
	                              Item i) throws Exception;

	List<String> listNames();

	List<PricingType> listPricingTypes();

	List<UomType> listSellingUoms();

	List<UomType> listSellingUoms( //
	                               Item i) throws Exception;

	List<ItemType> listTypes();

	boolean noChangesNeedingApproval( //
	                                  String tab);

	void setBoms(List<Bom> l);

	void setFamily(ItemFamily f);

	void setPartUponValidation( //
	                            Long id) throws Exception;
}