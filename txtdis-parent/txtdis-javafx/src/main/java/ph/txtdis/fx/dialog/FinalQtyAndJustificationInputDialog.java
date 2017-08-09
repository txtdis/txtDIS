package ph.txtdis.fx.dialog;

import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.TextAreaDisplay;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.type.Type;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("finalQtyAndJustificationInputDialog")
public class FinalQtyAndJustificationInputDialog
	extends AbstractInputDialog {

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppFieldImpl<Integer> caseField, pieceField;

	@Autowired
	private TextAreaDisplay justificationArea;

	private StockTakeVariance variance;

	public FinalQtyAndJustificationInputDialog varianceToEdit(StockTakeVariance v) {
		variance = v;
		return this;
	}

	@Override
	public void goToDefaultFocus() {
		caseField.requestFocus();
	}

	@Override
	protected String headerText() {
		return "Input Final Qty";
	}

	public StockTakeVariance getVariance() {
		return variance;
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(actionButton(), closeButton());
	}

	private AppButton actionButton() {
		AppButton actionButton = button.large("Input").build();
		actionButton.onAction(event -> setFinalQtyAndJustification());
		actionButton.disableIf(caseField.isEmpty()
			.or(pieceField.isEmpty())
			.or(justificationArea.isEmpty()));
		return actionButton;
	}

	private void setFinalQtyAndJustification() {
		variance.setFinalQty(unitQtyOfCases().add(qtyInPieces()));
		variance.setJustification(justificationArea.getValue());
		refresh();
		close();
	}

	private BigDecimal unitQtyOfCases() {
		return qtyInCases().multiply(qtyPerCase());
	}

	private BigDecimal qtyInPieces() {
		return new BigDecimal(pieceField.getValue());
	}

	@Override
	public void refresh() {
		caseField.setValue(null);
		pieceField.setValue(null);
		justificationArea.setValue(null);
		super.refresh();
	}

	private BigDecimal qtyInCases() {
		return new BigDecimal(caseField.getValue());
	}

	private BigDecimal qtyPerCase() {
		return new BigDecimal(variance.getQtyPerCase());
	}

	@Override
	protected List<Node> nodes() {
		grid.getChildren().clear();
		grid.add(label.field("Case"), 0, 0);
		grid.add(caseField.build(Type.INTEGER), 1, 0);
		grid.add(label.field("Bottle"), 2, 0);
		grid.add(pieceField.build(Type.INTEGER), 3, 0);
		grid.add(label.field("Justification"), 0, 1);
		grid.add(justificationArea.width(360).build(), 0, 2, 4, 1);
		justificationArea.makeEditable();
		return asList(header(), grid, buttonBox());
	}

	@Override
	protected void nullData() {
		super.nullData();
	}
}
