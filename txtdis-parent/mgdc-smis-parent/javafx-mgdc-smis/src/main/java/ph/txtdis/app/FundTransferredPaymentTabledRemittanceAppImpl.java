package ph.txtdis.app;

import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.layout.HBox;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.pane.AppGridPane;

@Scope("prototype")
@Component("remittanceApp")
public class FundTransferredPaymentTabledRemittanceAppImpl extends AbstractPaymentTabledRemittanceApp {

	@Autowired
	private AppButton transferButton;

	@Autowired
	private AppField<String> receivedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> receivedOnDisplay;

	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		b.add(4, transferButton.icon("transfer").tooltip("Fund transfer\receipt").build());
		return b;
	}

	@Override
	protected AppGridPane addGridPane() {
		super.addGridPane();
		fundTransferNodes();
		depositAndRemarksNodesStartingAtLineNo(3);
		return gridPane;
	}

	private void fundTransferNodes() {
		gridPane.add(label.field("Fund Transfer"), 0, 2);
		gridPane.add(transferBox(), 1, 2);
	}

	private HBox transferBox() {
		return box.forGridGroup(//
				label.field("Received by"), //
				receivedByDisplay.readOnly().width(120).build(TEXT), //
				label.field("on"), //
				receivedOnDisplay.readOnly().build(TIMESTAMP));
	}

	@Override
	public void refresh() {
		super.refresh();
		receivedByDisplay.setValue(service.getReceivedBy());
		receivedOnDisplay.setValue(service.getReceivedOn());
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		transferButton.disableIf(notPosted()//
				.or(transferred())//
				.or(deposited())//
				.or(audited())//
				.or(userAllowedToReceiveFundTransfer.not()));
	}

	private BooleanBinding transferred() {
		return receivedOnDisplay.isNotEmpty();
	}

	@Override
	protected void setListeners() {
		super.setListeners();
		transferButton.setOnAction(e -> setFundTransferData());
	}

	private void setFundTransferData() {
		service.setFundTransferData();
		save();
	}
}
