package ph.txtdis.mgdc.gsm.fx.tab;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.tab.AbstractTab;
import ph.txtdis.mgdc.gsm.fx.table.CreditTable;
import ph.txtdis.mgdc.gsm.service.CreditGivenCustomerService;

import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.PHONE;
import static ph.txtdis.type.Type.TEXT;

@Scope("prototype")
@Component("creditTab")
public class CreditTab
	extends AbstractTab {

	@Autowired
	private CreditGivenCustomerService service;

	@Autowired
	private LabelFactory label;

	@Autowired
	private AppFieldImpl<String> contactNameField, contactSurnameField, titleField, mobileField;

	@Autowired
	private CreditTable creditTable;

	public CreditTab() {
		super("Credit Details");
	}

	@Override
	public void clear() {
		creditTable.removeListener();
	}

	@Override
	public void refresh() {
		contactNameField.setText(service.getContactName());
		contactSurnameField.setText(service.getContactSurname());
		titleField.setText(service.getContactTitle());
		mobileField.setValue(service.getMobile());
		creditTable.items(service.getCreditDetails());
	}

	@Override
	public void save() {
		service.setContactName(contactNameField.getText());
		service.setContactSurname(contactSurnameField.getText());
		service.setContactTitle(titleField.getText());
		service.setCreditDetails(creditTable.getItems());
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		gridPane.getChildren().clear();
		gridPane.add(label.group("Credit Contact"), 0, 0, 3, 1);
		gridPane.add(label.field("Given Name"), 0, 1);
		gridPane.add(contactNameField.build(TEXT), 1, 1);
		gridPane.add(label.field("Surname"), 2, 1);
		gridPane.add(contactSurnameField.build(TEXT), 3, 1);
		gridPane.add(label.field("Designation"), 0, 2);
		gridPane.add(titleField.build(TEXT), 1, 2);
		gridPane.add(label.field("Mobile No."), 2, 2);
		gridPane.add(mobileField.build(PHONE), 3, 2);
		return asList(gridPane, pane.centeredHorizontal(tablePane()));
	}

	private VBox tablePane() {
		return pane.vertical(label.group("Approved Credit History"), creditTable.build());
	}

	@Override
	protected void setBindings() {
		contactSurnameField.disableIf(contactNameField.isEmpty());
		titleField.disableIf(contactSurnameField.isEmpty());
		mobileField.disableIf(titleField.isEmpty());
		creditTable.disableIf(mobileField.isEmpty());
	}

	@Override
	protected void setListeners() {
		mobileField.onAction(e -> validateMobileNo());
	}

	private void validateMobileNo() {
		String ph = mobileField.getValue();
		if (!ph.isEmpty())
			try {
				service.validatePhoneNo(ph);
			} catch (Exception e) {
				mobileField.clear();
				handleError(mobileField, e);
			}
	}
}
