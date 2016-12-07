package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.CustomerDiscount;
import ph.txtdis.dto.Item;
import ph.txtdis.exception.DateInThePastException;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.service.ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService;

@Scope("prototype")
@Component("customerDiscountDialog")
public class CustomerDiscountDialogImpl extends AbstractCustomerDiscountDialog {

	@Autowired
	private ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService valueBasedDiscountedCustomerService;

	@Autowired
	private ItemInputtedDialog itemInputtedDialog;

	private Item item;

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>(itemNodes());
		l.addAll(asList(discountField("Discount Value"), startDatePicker()));
		return l;
	}

	private List<InputNode<?>> itemNodes() {
		List<InputNode<?>> l = itemInputtedDialog.addNodes(this);
		itemInputtedDialog.setItemIdFieldOnAction(e -> setItemUponVerification());
		return l;
	}

	private void setItemUponVerification() {
		try {
			item = itemInputtedDialog.validateItemExists();
		} catch (Exception e) {
			item = null;
			resetNodesOnError(e);
		}
	}

	@Override
	protected CustomerDiscount validateThenCreateDiscount() throws DateInThePastException, DuplicateException {
		return valueBasedDiscountedCustomerService.createDiscountUponValidation(//
				item, //
				discountField.getValue(), //
				startDatePicker.getValue());
	}
}