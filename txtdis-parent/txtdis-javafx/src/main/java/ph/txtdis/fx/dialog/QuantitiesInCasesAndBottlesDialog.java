package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static org.apache.log4j.Logger.getLogger;

import java.math.BigDecimal;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.QtyPerUomService;
import ph.txtdis.type.Type;
import ph.txtdis.type.UomType;

@Scope("prototype")
@Component("quantitiesInCasesAndBottlesDialog")
public class QuantitiesInCasesAndBottlesDialog {

	private static Logger logger = getLogger(QuantitiesInCasesAndBottlesDialog.class);

	@Autowired
	private LabeledField<Integer> caseField;

	@Autowired
	private LabeledField<Integer> pieceField;

	private QtyPerUomService service;

	private String prefix;

	public List<InputNode<?>> addNodes(QtyPerUomService service, String prefix) {
		this.service = service;
		this.prefix = prefix + " ";
		return asList(caseField(), pieceField());
	}

	private LabeledField<Integer> caseField() {
		return caseField.name(prefix + "Cases").build(Type.INTEGER);
	}

	private LabeledField<Integer> pieceField() {
		return pieceField.name(prefix + "Bottles").build(Type.INTEGER);
	}

	public void setQtyFieldOnAction(EventHandler<ActionEvent> e) {
		pieceField.setOnAction(e);
	}

	public BigDecimal totalQtyInPieces() {
		BigDecimal unitQtyOfCases = qtyInCases().multiply(qtyPerCase());
		BigDecimal totalUnitQty = unitQtyOfCases.add(qtyInPieces());
		logger.info(
				"\n    UnitQtyOfCases" + prefix + "= " + unitQtyOfCases + ", TotalUnitQty" + prefix + "= " + totalUnitQty);
		return totalUnitQty;
	}

	private BigDecimal qtyInCases() {
		Integer qtyInCases = caseField.getValue();
		logger.info("\n    QtyInCases" + prefix + "= " + qtyInCases);
		return new BigDecimal(qtyInCases);
	}

	private BigDecimal qtyPerCase() {
		BigDecimal qtyPerCase = service.getQtyPerUom(UomType.CS);
		logger.info("\n    QtyPerCases" + prefix + "= " + qtyPerCase);
		return qtyPerCase;
	}

	private BigDecimal qtyInPieces() {
		Integer qtyInPieces = pieceField.getValue();
		logger.info("\n    QtyInPieces" + prefix + "= " + qtyInPieces);
		return new BigDecimal(qtyInPieces);
	}
}
