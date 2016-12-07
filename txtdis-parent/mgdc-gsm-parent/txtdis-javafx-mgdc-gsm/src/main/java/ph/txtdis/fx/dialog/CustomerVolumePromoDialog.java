package ph.txtdis.fx.dialog;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerVolumePromo;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.service.ItemService;
import ph.txtdis.service.ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("customerVolumePromoDialog")
public class CustomerVolumePromoDialog extends AbstractFieldDialog<CustomerVolumePromo> {

	@Autowired
	private ItemService itemService;

	@Autowired
	private ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService service;

	@Autowired
	private LabeledCombo<UomType> uomCombo;

	@Autowired
	private LabeledDatePicker startDatePicker;

	@Autowired
	private QuantitiesInCasesAndBottlesDialog freeQuantitiesInCasesAndBottlesDialog,
			targetQuantitiesInCasesAndBottlesDialog;

	@Autowired
	private ItemInputtedDialog itemInputtedDialog;

	private CustomerVolumePromo volumePromo;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(itemNodes());
		l.add(uomCombo());
		l.addAll(targetQuantitiesInCasesAndBottlesDialog());
		l.addAll(freeQuantitiesInCasesAndBottlesDialog());
		l.add(startDatePicker());
		return l;
	}

	private List<InputNode<?>> itemNodes() {
		List<InputNode<?>> l = itemInputtedDialog.addNodes(this);
		itemInputtedDialog.setItemIdFieldOnAction(e -> setItemUponVerification());
		return l;
	}

	private void setItemUponVerification() {
		try {
			service.setItem(itemInputtedDialog.validateItemExists());
		} catch (Exception e) {
			resetNodesOnError(e);
		}
	}

	private InputNode<?> uomCombo() {
		return uomCombo.name("UOM").items(itemService.listSellingUoms()).build();
	}

	private List<InputNode<?>> targetQuantitiesInCasesAndBottlesDialog() {
		return targetQuantitiesInCasesAndBottlesDialog.addNodes(service, "Target");
	}

	private List<InputNode<?>> freeQuantitiesInCasesAndBottlesDialog() {
		return freeQuantitiesInCasesAndBottlesDialog.addNodes(service, "Free");
	}

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.setOnAction(value -> validateStartDate());
		return startDatePicker;
	}

	private void validateStartDate() {
		if (startDatePicker.getValue() != null)
			try {
				createVolumePromoUponValidation();//
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	@Override
	protected CustomerVolumePromo createEntity() {
		return volumePromo;
	}

	@Override
	protected String headerText() {
		return "Add New Volume Promo";
	}

	private void createVolumePromoUponValidation() throws Exception {
		volumePromo = service.createVolumePromoUponValidation( //
				targetQuantitiesInCasesAndBottlesDialog.totalQtyInPieces(), //
				targetQuantitiesInCasesAndBottlesDialog.totalQtyInPieces(), //
				startDatePicker.getValue());
	}
}