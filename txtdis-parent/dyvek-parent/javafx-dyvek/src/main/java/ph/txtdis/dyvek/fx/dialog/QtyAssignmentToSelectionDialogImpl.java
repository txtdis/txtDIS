package ph.txtdis.dyvek.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dyvek.fx.dialog.AssignmentDialog;
import ph.txtdis.dyvek.model.BillableDetail;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.fx.pane.AppGridPane;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.QUANTITY;

@Scope("prototype")
@Component("qtyAssignmentToSelectionDialog")
public class QtyAssignmentToSelectionDialogImpl
	extends AbstractInputDialog
	implements AssignmentDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppFieldImpl<BigDecimal> qtyField;

	private BillableDetail detail;

	@Override
	public AssignmentDialog setDetail(BillableDetail detail) {
		this.detail = detail;
		return this;
	}

	@Override
	public BillableDetail getDetail() {
		return detail;
	}

	@Override
	public void goToDefaultFocus() {
		qtyField.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(assignmentButton(), closeButton());
	}

	private AppButton assignmentButton() {
		AppButton openButton = button.large("Assign").build();
		openButton.onAction(event -> assignQtyIfValid(qtyField.getValue()));
		openButton.disableIf(qtyField.isEmpty());
		return openButton;
	}

	private void assignQtyIfValid(BigDecimal value) {
		try {
			if (detail.getQty().compareTo(value) < 0)
				throw new InvalidException("Quantity exceeds balance");
			assignQty(value);
		} catch (InvalidException e) {
			resetNodesOnError(e);
		}
	}

	private void assignQty(BigDecimal value) {
		detail.setAssignedQty(value);
		qtyField.setValue(null);
		close();
	}

	@Override
	protected String headerText() {
		return "Assign Quantity";
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(qtyField.build(QUANTITY), 0, 0);
		grid.add(label.help("kg"), 1, 0);
		return asList(header(), grid, buttonBox());
	}

	@Override
	protected void nullData() {
		super.nullData();
		detail = null;
	}
}
