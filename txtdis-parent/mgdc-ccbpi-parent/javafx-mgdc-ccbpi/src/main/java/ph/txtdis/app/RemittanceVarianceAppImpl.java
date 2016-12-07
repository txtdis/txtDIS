package ph.txtdis.app;

import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import ph.txtdis.dto.SalesItemVariance;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.table.RemittanceVarianceTable;
import ph.txtdis.service.RemittanceVarianceService;

@Scope("prototype")
@Component("remittanceVarianceApp")
public class RemittanceVarianceAppImpl
		extends AbstractTotaledReportApp<RemittanceVarianceTable, RemittanceVarianceService, SalesItemVariance>
		implements RemittanceVarianceApp {

	@Autowired
	private AppField<BigDecimal> actualRemittanceDisplay, varianceRemittanceDisplay;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		List<Node> nodes = super.mainVerticalPaneNodes();
		VBox vbox = box.forVerticals(nodes.get(0));
		return actualAndRemittanceVarianceNodes(vbox);
	}

	@Override
	protected int noOfTotalDisplays() {
		return 1;
	}

	@Override
	public void refresh() {
		try {
			super.refresh();
			actualRemittanceDisplay.setValue(service.getActualValue());
			varianceRemittanceDisplay.setValue(service.getVarianceValue());
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(this).start();
		}
	}

	private List<Node> actualAndRemittanceVarianceNodes(VBox vbox) {
		List<Node> nodes = new ArrayList<>(vbox.getChildren());
		nodes.addAll(actualAndRemittanceVariance());
		return nodes;
	}

	private List<Node> actualAndRemittanceVariance() {
		return Arrays.asList( //
				box.forTableTotals(actualRemittanceDisplay()), //
				box.forTableTotals(varianceRemittanceDisplay()));
	}

	private List<Node> actualRemittanceDisplay() {
		return Arrays.asList(actualRemittanceDisplay.readOnly().build(CURRENCY));
	}

	private List<Node> varianceRemittanceDisplay() {
		return Arrays.asList(varianceRemittanceDisplay.readOnly().build(CURRENCY));
	}
}
