package ph.txtdis.app;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import ph.txtdis.dto.SalesforceAccount;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.service.SalesforceUploadService;

@Scope("prototype")
@Component("customerApp")
public class CustomerAppImpl extends AbstractTabbedCustomerApp {

	@Autowired
	private SalesforceUploadApp salesforce;

	@Autowired
	private AppButton uploadButton;

	@Override
	public void refresh() {
		super.refresh();
		salesforce.refresh();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<AppButton> addButtons() {
		List<AppButton> l = new ArrayList<>(super.addButtons());
		l.add(uploadButton = salesforce.service((SalesforceUploadService<SalesforceAccount>) service).stage(this)
				.addUploadButton());
		return l;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(tabPane(), trackedPane(), modificationAndUploadPane());
	}

	private HBox modificationAndUploadPane() {
		List<Node> l = new ArrayList<>(lastModificationNodes());
		l.addAll(salesforce.addNodes());
		return box.forHorizontalPane(l);
	}

	@Override
	protected void setBindings() {
		super.setBindings();
		uploadButton.disableIf(isOffSite);
	}
}
