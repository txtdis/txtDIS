package ph.txtdis.app;

import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.Item;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.SearchDialog;
import ph.txtdis.service.ItemService;

public abstract class AbstractItemApp extends AbstractIdApp<ItemService, Item, Long, Long> implements ItemApp {

	@Autowired
	private AppButton searchButton;

	@Autowired
	private AppField<String> lastModifiedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> lastModifiedOnDisplay;

	@Autowired
	private SearchDialog searchDialog;

	@Autowired
	private ItemListApp itemListApp;

	@Autowired
	protected LabelFactory label;

	protected BooleanProperty isOffSite;

	public void listSearchResults() throws Exception {
		itemListApp.addParent(this).start();
		Item c = itemListApp.getSelection();
		if (c != null)
			service.open(c.getId());
	}

	@Override
	public void refresh() {
		refreshLastModificationNodes();
		super.refresh();
	}

	private List<Node> lastModificationNodes() {
		return Arrays.asList(//
				label.name("Last Modified by"), lastModifiedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), lastModifiedOnDisplay.readOnly().build(TIMESTAMP));
	}

	private void openSearchDialog() {
		searchDialog.criteria("name").start();
		String name = searchDialog.getText();
		if (name != null)
			search(name);
	}

	private void refreshLastModificationNodes() {
		lastModifiedByDisplay.setValue(service.getLastModifiedBy());
		lastModifiedOnDisplay.setValue(service.getLastModifiedOn());
	}

	private void search(String name) {
		try {
			service.search(name);
			listSearchResults();
			refresh();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(searchButton.icon("search").tooltip("Search...").build());
		return l;
	}

	@Override
	protected void setBindings() {
		isOffSite = new SimpleBooleanProperty(service.isOffSite());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		searchButton.setOnAction(e -> openSearchDialog());
	}

	@Override
	protected HBox trackedPane() {
		List<Node> l = new ArrayList<>(super.trackedPane().getChildren());
		l.addAll(lastModificationNodes());
		return box.forHorizontalPane(l);
	}
}
