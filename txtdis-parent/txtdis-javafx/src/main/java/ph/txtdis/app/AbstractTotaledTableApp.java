package ph.txtdis.app;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppBoxPaneFactory;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.TotaledTableService;

public abstract class AbstractTotaledTableApp<T> implements TotaledTableApp<T> {

	private static Logger logger = getLogger(AbstractTotaledTableApp.class);

	@Autowired
	private LabelFactory label;

	@Autowired
	protected AppBoxPaneFactory box;

	@Autowired
	protected List<AppField<BigDecimal>> totalDisplays;

	private AppTable<T> table;

	private Label subhead;

	@Override
	public VBox addNoSubHeadTablePane(AppTable<T> t) {
		subheadPane();
		VBox v = box.forVerticals(t.build(), totalPane());
		return addTablePane(t, v);
	}

	@Override
	public VBox addTablePane(AppTable<T> t) {
		VBox v = box.forVerticals(subheadPane(), t.build(), totalPane());
		return addTablePane(t, v);
	}

	private VBox addTablePane(AppTable<T> t, VBox v) {
		table = t;
		HBox h = box.forHorizontalPane(v);
		return box.forVerticals(h);
	}

	@Override
	public HBox addTotalDisplays(int count) {
		totalDisplays = new ArrayList<>();
		for (int i = 0; i < count; i++)
			totalDisplays.add(new AppField<BigDecimal>().readOnly().build(CURRENCY));
		return box.forTableTotals(totalDisplays);
	}

	@Override
	public void refresh(TotaledTableService<T> s) {
		refreshSubheader(s);
		refreshTotals(s);
	}

	private void refreshSubheader(TotaledTableService<T> s) {
		subhead.setText(s.getSubhead());
		table.setId(s.getSubhead());
	}

	private void refreshTotals(TotaledTableService<T> s) {
		logger.info("\n    TableItems = " + table.getItems());
		List<BigDecimal> list = s.getTotals(table.getItems());
		logger.info("\n    TotalList = " + list);
		if (list == null)
			return;
		for (int i = 0; i < list.size(); i++)
			totalDisplays.get(i).setValue(list.get(i));
	}

	private HBox subheadPane() {
		subhead = label.subheader("");
		return box.forSubheader(subhead);
	}

	private Node totalPane() {
		return box.forTableTotals(totalDisplays);
	}
}
