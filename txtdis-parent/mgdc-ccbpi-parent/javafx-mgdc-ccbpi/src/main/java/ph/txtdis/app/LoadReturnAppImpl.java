package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ID;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.dto.PickList;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.pane.AppGridPane;
import ph.txtdis.fx.table.LoadReturnTable;
import ph.txtdis.service.LoadReturnService;

@Lazy
@Component("loadReturnApp")
public class LoadReturnAppImpl extends AbstractIdApp<LoadReturnService, PickList, Long, Long> implements LoadReturnApp {

	@Autowired
	private AppField<LocalDate> pickDateDisplay;

	@Autowired
	private AppField<Long> pickListIdInput;

	@Autowired
	private LoadReturnTable table;

	@Autowired
	private AppGridPane gridPane;

	@Autowired
	private LabelFactory label;

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(gridPane(), box.forHorizontalPane(table.build()), trackedPane());
	}

	private AppGridPane gridPane() {
		gridPane.getChildren().clear();
		gridPane.add(label.field("Date"), 0, 0);
		gridPane.add(pickDateDisplay.build(DATE), 1, 0);
		gridPane.add(label.field("Pick List No."), 2, 0);
		gridPane.add(pickListIdInput.build(ID), 3, 0);
		return gridPane;
	}

	@Override
	public void refresh() {
		super.refresh();
		pickDateDisplay.setValue(service.getPickDate());
		table.items(service.getDetails());
	}

	@Override
	protected void setBindings() {
		saveButton.disableIf(table.isEmpty()//
				.or(table.disabledProperty())//
				.or(isPosted()));
		table.disableIf(pickListIdInput.isEmpty());
	}

	@Override
	public void setFocus() {
		pickListIdInput.requestFocus();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		pickListIdInput.setOnAction(e -> updateUponPickListIdValidation());
	}

	private void updateUponPickListIdValidation() {
		if (service.isNew())
			try {
				service.updateUponPickListIdValidation(pickListIdInput.getValue());
				refresh();
				table.requestFocus();
			} catch (Exception e) {
				service.reset();
				refresh();
				clearControlAfterShowingErrorDialog(e, pickListIdInput);
			}
	}
}