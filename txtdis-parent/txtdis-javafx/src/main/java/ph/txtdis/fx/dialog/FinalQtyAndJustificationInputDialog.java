package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.TextAreaDisplay;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.type.Type;

@Scope("prototype")
@Component("finalQtyAndJustificationInputDialog")
public class FinalQtyAndJustificationInputDialog extends AbstractInputDialog {

	private static Logger logger = getLogger(FinalQtyAndJustificationInputDialog.class);

	@Autowired
	private AppGridPane grid;

	@Autowired
	private AppButton actionButton;

	@Autowired
	private AppField<Integer> caseField, pieceField;

	@Autowired
	private TextAreaDisplay justificationArea;

	private StockTakeVariance variance;

	public FinalQtyAndJustificationInputDialog varianceToEdit(StockTakeVariance v) {
		variance = v;
		logger.info("\n    InitialVariance: " + v);
		return this;
	}

	@Override
	public void refresh() {
		caseField.setValue(null);
		pieceField.setValue(null);
		justificationArea.setValue(null);
		super.refresh();
	}

	@Override
	public void setFocus() {
		caseField.requestFocus();
	}

	@Override
	protected String headerText() {
		return "Input Final Qty";
	}

	public StockTakeVariance getVariance() {
		logger.info("\n    EditedVariance: " + variance);
		return variance;
	}

	private void setFinalQtyAndJustification() {
		variance.setFinalQty(unitQtyOfCases().add(qtyInPieces()));
		variance.setJustification(justificationArea.getValue());
		logger.info("\n    VarianceOnClose: " + variance);
		refresh();
		close();
	}

	private BigDecimal unitQtyOfCases() {
		return qtyInCases().multiply(qtyPerCase());
	}

	private BigDecimal qtyInCases() {
		return new BigDecimal(caseField.getValue());
	}

	private BigDecimal qtyPerCase() {
		return new BigDecimal(variance.getQtyPerCase());
	}

	private BigDecimal qtyInPieces() {
		return new BigDecimal(pieceField.getValue());
	}

	private Button actionButton() {
		actionButton.large("Input").build();
		actionButton.setOnAction(event -> setFinalQtyAndJustification());
		actionButton.disableIf(caseField.isEmpty() //
				.or(pieceField.isEmpty()) //
				.or(justificationArea.isEmpty()));
		return actionButton;
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { actionButton(), closeButton() };
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
		justificationArea.editable();
		return asList(header(), grid, buttonBox());
	}

	@Override
	protected void setOnFiredCloseButton() {
		refresh();
		super.setOnFiredCloseButton();
	}
}
