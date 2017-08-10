package ph.txtdis.app;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.StockTakeVariance;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.dialog.AuditDialog;
import ph.txtdis.fx.dialog.OpenByDateDialog;
import ph.txtdis.fx.table.StockTakeVarianceTable;
import ph.txtdis.info.Information;
import ph.txtdis.service.StockTakeVarianceService;
import ph.txtdis.util.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static javafx.collections.FXCollections.observableArrayList;
import static ph.txtdis.util.DateTimeUtils.getServerDate;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

public abstract class AbstractStockTakeVarianceApp //
	extends AbstractReportApp<StockTakeVarianceTable, StockTakeVarianceService, StockTakeVariance> //
	implements StockTakeVarianceApp {

	@Autowired
	private AppButton decisionButton, saveButton;

	@Autowired
	private AuditDialog decisionDialog;

	@Autowired
	private OpenByDateDialog openDialog;

	private BooleanProperty canBeApproved = new SimpleBooleanProperty(false);

	private BooleanProperty canBeRejected = new SimpleBooleanProperty(false);

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.addAll(Arrays.asList( //
			saveButton.icon("save").tooltip("Save...").build(), //
			decisionButton.icon("decision").tooltip("Decide...").build()));
		return b;
	}

	@Override
	protected void displayOpenByDateDialog() {
		openDialog.header("List Physical Count Variances");
		openDialog.prompt("Enter date of the desired stock take");
		openDialog.addParent(this).start();
	}

	@Override
	protected void setDates() {
		service.setEndDate(openDialog.getDate());
	}

	@Override
	protected void setBindings() {
		decisionButton.disableIf(saveButton.disabledProperty().not());
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		saveButton.onAction(e -> save());
		decisionButton.onAction(e -> decide());
	}

	private void save() {
		try {
			service.saveUponValidation(table.getItems());
		} catch (Information i) {
			showInfoDialog(i);
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}

	private void decide() {
		canBeApproved.set(service.canApprove());
		canBeRejected.set(service.canReject());
		decisionDialog//
			.disableApprovalButtonIf(canBeApproved.not())//
			.disableRejectionButtonIf(canBeRejected.not())//
			.addParent(this).start();
		if (decisionDialog.isValid() != null)
			updateTableOnDecision();
	}

	private void updateTableOnDecision() {
		ObservableList<StockTakeVariance> l = observableArrayList(table.getItems());
		table.items(l.stream().map(this::validate).collect(toList()));
	}

	private StockTakeVariance validate(StockTakeVariance v) {
		if (v.getIsValid() == null) {
			v.setIsValid(decisionDialog.isValid());
			String prefix = "";
			String justification = v.getJustification();
			if (justification != null && !justification.isEmpty())
				prefix = justification + "\n";
			v.setJustification(prefix + decision());
		}
		return v;
	}

	private String decision() {
		String prefix = "";
		if (!decisionDialog.isValid())
			prefix = "DIS";
		return "[" + prefix + "APPROVED: " + service.getUsername() + " - " + toDateDisplay(getServerDate()) + "] "
			+ TextUtils.blankIfNull(decisionDialog.getFindings());
	}
}
