package ph.txtdis.app;

import static ph.txtdis.type.Type.CURRENCY;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppBoxPaneFactory;
import ph.txtdis.fx.table.AppTable;
import ph.txtdis.service.SubheadedTotaledService;
import ph.txtdis.service.TotaledService;

public abstract class AbstractTotaledTableApp<T> implements TotaledTableApp<T> {

	@Autowired
	private LabelFactory label;

	@Autowired
	protected AppBoxPaneFactory box;

	@Autowired
	protected List<AppFieldImpl<BigDecimal>> totalDisplays;

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

	private HBox subheadPane() {
		subhead = label.subheader("");
		return box.forSubheader(subhead);
	}

	private Node totalPane() {
		return box.forTableTotals(totalDisplays);
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
			totalDisplays.add(new AppFieldImpl<BigDecimal>().readOnly().build(CURRENCY));
		return box.forTableTotals(totalDisplays);
	}

	@Override
	public void refresh(TotaledService<T> s) {
		if (s instanceof SubheadedTotaledService)
			refreshSubheader((SubheadedTotaledService<T>) s);
		refreshTotals(s);
	}

	private void refreshSubheader(SubheadedTotaledService<T> s) {
		subhead.setText(s.getSubhead());
		table.setId(s.getSubhead());
	}

	private void refreshTotals(TotaledService<T> service) {
		List<BigDecimal> list = service.getTotals(table.getItems());
		if (list != null && totalDisplays != null)
			for (int i = 0; i < list.size() && i < totalDisplays.size(); i++)
				totalDisplays.get(i).setValue(list.get(i));
	}
}
