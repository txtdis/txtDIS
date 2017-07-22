package ph.txtdis.mgdc.ccbpi.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Price;
import ph.txtdis.dto.PricingType;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledDatePicker;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.mgdc.ccbpi.service.BommedDiscountedPricedValidatedItemService;
import ph.txtdis.util.DateTimeUtils;

@Scope("prototype")
@Component("pricingDialog")
public class PricingDialogImpl //
		extends AbstractFieldDialog<Price> //
		implements PricingDialog {

	@Autowired
	private LabeledDatePicker endDatePicker, startDatePicker;

	@Autowired
	private LabeledField<BigDecimal> dealerPriceAfterEndField, dealerPriceAtStartField, purchasePriceAfterEndField, purchasePriceAtStartField;

	@Autowired
	private BommedDiscountedPricedValidatedItemService itemService;

	private Price dealerPriceAfterEnd, dealerPriceAtStart, purchasePriceAfterEnd, purchasePriceAtStart;

	@Override
	protected List<InputNode<?>> addNodes() {
		return asList(startDatePicker(), //
				purchasePriceOnStartField(), //
				dealerPriceOnStartField(), //
				endDatePicker(), //
				purchasePriceAfterEndField(), //
				dealerPriceAfterEndField()); //
	}

	private LabeledDatePicker startDatePicker() {
		return startDatePicker.name("Start Date");
	}

	private LabeledField<BigDecimal> purchasePriceOnStartField() {
		purchasePriceAtStartField.name("Purchase Price at Start").build(CURRENCY);
		purchasePriceAtStartField.disableIf(startDatePicker.isEmpty());
		purchasePriceAtStartField.onAction(e -> createPurchasePriceAtStartUponValidation());
		return purchasePriceAtStartField;
	}

	private void createPurchasePriceAtStartUponValidation() {
		purchasePriceAtStart = createPricingUponValidation( //
				itemService.getPurchasePricingType(), //
				startDatePicker.getValue(), //
				purchasePriceAtStartField.getValue());
	}

	private Price createPricingUponValidation(PricingType type, LocalDate date, BigDecimal value) {
		try {
			return itemService.createPricingUponValidation(type, value, null, date);
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	private LabeledField<BigDecimal> dealerPriceOnStartField() {
		dealerPriceAtStartField.name("Dealer Price at Start").build(CURRENCY);
		dealerPriceAtStartField.disableIf(startDatePicker.isEmpty());
		dealerPriceAtStartField.onAction(e -> createDealerPriceAtStartUponValidation());
		return dealerPriceAtStartField;
	}

	private void createDealerPriceAtStartUponValidation() {
		dealerPriceAtStart = createPricingUponValidation( //
				itemService.getDealerPricingType(), //
				startDatePicker.getValue(), //
				dealerPriceAtStartField.getValue());
	}

	private LabeledDatePicker endDatePicker() {
		endDatePicker.name("End Date");
		endDatePicker.disableIf(startDatePicker.isEmpty());
		endDatePicker.onAction(e -> validateEndDate());
		return endDatePicker;
	}

	private void validateEndDate() {
		if (endDate() != null)
			try {
				DateTimeUtils.validateEndDate(startDatePicker.getValue(), endDate());
			} catch (Exception e) {
				resetNodesOnError(e);
			}
	}

	private LocalDate endDate() {
		return endDatePicker.getValue();
	}

	private LabeledField<BigDecimal> purchasePriceAfterEndField() {
		purchasePriceAfterEndField.name("Purchase Price after End").build(CURRENCY);
		purchasePriceAfterEndField.disableIf(endDatePicker.isEmpty());
		purchasePriceAfterEndField.onAction(e -> createPurchasePriceAfterEndUponValidation());
		return purchasePriceAfterEndField;
	}

	private void createPurchasePriceAfterEndUponValidation() {
		purchasePriceAfterEnd = createPricingUponValidation( //
				itemService.getPurchasePricingType(), //
				dateAfterEnd(), //
				purchasePriceAfterEndField.getValue());
	}

	private LocalDate dateAfterEnd() {
		return endDate() == null ? null : endDate().plusDays(1L);
	}

	private LabeledField<BigDecimal> dealerPriceAfterEndField() {
		dealerPriceAfterEndField.name("Dealer Price after End").build(CURRENCY);
		dealerPriceAfterEndField.disableIf(endDatePicker.isEmpty());
		dealerPriceAfterEndField.onAction(e -> createDealerPriceAfterEndUponValidation());
		return dealerPriceAfterEndField;
	}

	private void createDealerPriceAfterEndUponValidation() {
		dealerPriceAfterEnd = createPricingUponValidation( //
				itemService.getDealerPricingType(), //
				dateAfterEnd(), //
				dealerPriceAfterEndField.getValue());
	}

	@Override
	public List<Price> getAddedItems() {
		return Arrays.asList(dealerPriceAtStart, purchasePriceAtStart, dealerPriceAfterEnd, purchasePriceAfterEnd);
	}

	@Override
	protected Price createEntity() {
		return null;
	}

	@Override
	protected String headerText() {
		return "Add New Pricing";
	}
}