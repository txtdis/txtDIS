package ph.txtdis.mgdc.gsm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Bom;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.mgdc.gsm.dto.Item;
import ph.txtdis.mgdc.service.ItemService;
import ph.txtdis.mgdc.service.ValidatedUomService;
import ph.txtdis.service.DateValidated;
import ph.txtdis.service.DecisionNeededService;
import ph.txtdis.service.MasterService;
import ph.txtdis.service.SavableAsExcelService;
import ph.txtdis.type.ItemType;
import ph.txtdis.type.UomType;

public interface BommedDiscountedPricedValidatedItemService //
		extends DateValidated, DecisionNeededService, SavableAsExcelService<Item>, ItemBasedService<Bom>, ItemService, MasterService<Item>,
		ValidatedUomService {

	Bom createBom(BigDecimal qty);

	Price createPricingUponValidation( //
			PricingType type, //
			BigDecimal price, //
			LocalDate startDate//
	) throws Exception;

	Item findByVendorNo(String id) throws Exception;

	LocalDate getEndOfLife();

	List<Price> getPriceList();

	BigDecimal getQtyPerUom( //
			Item item, //
			UomType uom);

	ItemType getType();

	boolean hasReportUom();

	boolean hasSoldUom();

	@Override
	default boolean isNew() {
		return MasterService.super.isNew();
	}

	boolean isNotDiscounted();

	List<Bom> listBoms();

	List<UomType> listBuyingUoms(Item item) throws Exception;

	List<String> listNames();

	List<ItemFamily> listParents();

	List<PricingType> listPricingTypes();

	List<UomType> listSellingUoms();

	List<UomType> listSellingUoms(Item item) throws Exception;

	List<ItemType> listTypes();

	boolean noChangesNeedingApproval(String tab);

	void setBoms(List<Bom> l);

	void setEndOfLife(LocalDate d);

	void setFamily(ItemFamily f);

	void setNotDiscounted(boolean b);

	void setPartUponValidation(Long id) throws Exception;

	void setPriceList(List<Price> items);

	void setType(ItemType t);
}