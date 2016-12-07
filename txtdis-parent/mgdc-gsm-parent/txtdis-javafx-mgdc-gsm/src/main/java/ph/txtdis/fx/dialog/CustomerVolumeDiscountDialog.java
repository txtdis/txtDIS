package ph.txtdis.fx.dialog;

import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerVolumeDiscount;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ItemService;
import ph.txtdis.service.ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("customerVolumeDiscountDialog")
public class CustomerVolumeDiscountDialog extends AbstractFieldDialog<CustomerVolumeDiscount> {

	@Autowired
	private ItemService itemService;

	@Autowired
	private ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService service;

	@Autowired
	private LabeledCombo<UomType> uomCombo;

	@Autowired
	private LabeledDatePicker startDatePicker;

	@Autowired
	private QuantitiesInCasesAndBottlesDialog targetQuantitiesInCasesAndBottlesDialog;

	@Autowired
	private ItemInputtedDialog itemInputtedDialog;

	@Autowired
	protected LabeledField<BigDecimal> discountField;

	private CustomerVolumeDiscount volumeDiscount;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(itemNodes());
		l.add(uomCombo());
		l.addAll(targetQuantitiesInCasesAndBottlesDialog());
		l.add(discountField.name("Discount\nValue").build(CURRENCY));
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

	private LabeledDatePicker startDatePicker() {
		startDatePicker.name("Start Date");
		startDatePicker.setOnAction(value -> validateStartDate());
		return startDatePicker;
	}

	private void validateStartDate() {
		if (startDatePicker.getValue() != null)
			try {
				createVolumeDiscountUponValidation();//
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	@Override
	protected CustomerVolumeDiscount createEntity() {
		return volumeDiscount;
	}

	@Override
	protected String headerText() {
		return "Add New Volume Discount";
	}

	private void createVolumeDiscountUponValidation() throws Exception {
		volumeDiscount = service.createVolumeDiscountUponValidation( //
				targetQuantitiesInCasesAndBottlesDialog.totalQtyInPieces(), //
				discountField.getValue(), //
				startDatePicker.getValue());
	}
}