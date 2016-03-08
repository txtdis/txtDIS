package ph.txtdis.app;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.stage.Stage;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppField;
import ph.txtdis.fx.control.LabelFactory;
import ph.txtdis.fx.dialog.MessageDialog;
import ph.txtdis.service.SalesforceUploadable;

@Scope("prototype")
@Component("SalesforceUploadApp")
public class SalesforceUploadApp {

	@Autowired
	private AppButton uploadButton;

	@Autowired
	private AppField<String> uploadedByDisplay;

	@Autowired
	private AppField<ZonedDateTime> uploadedOnDisplay;

	@Autowired
	private LabelFactory label;

	@Autowired
	private MessageDialog dialog;
	
	private SalesforceUploadable service;
	
	private Stage stage;
	
	public SalesforceUploadApp service(SalesforceUploadable service) {
		this.service = service;
		return this;
	}
	
	public SalesforceUploadApp stage(Stage stage) {
		this.stage = stage;
		return this;
	}

	public List<Node> addNodes() {
		return asList(//
				label.name("Uploaded by"), uploadedByDisplay.readOnly().width(120).build(TEXT), //
				label.name("on"), uploadedOnDisplay.readOnly().build(TIMESTAMP));
	}

	public AppButton addUploadButton() {
		uploadButton.icon("toSalesforce").tooltip("Upload to Salesforce...").build();
		uploadButton.setOnAction(e -> upload());
		return uploadButton;
	}

	private void upload() {
		try {
			service.upload();
		} catch (Exception e) {
			e.printStackTrace();
			dialog.show(e).addParent(stage).start();
		}
	}

	public BooleanBinding uploaded() {
		return uploadedOnDisplay.isNotEmpty();
	}

	public void refresh() {
		if (uploadedByDisplay == null)
			return;
		uploadedByDisplay.setValue(service.getUploadedBy());
		uploadedOnDisplay.setValue(service.getUploadedOn());
	}
}
