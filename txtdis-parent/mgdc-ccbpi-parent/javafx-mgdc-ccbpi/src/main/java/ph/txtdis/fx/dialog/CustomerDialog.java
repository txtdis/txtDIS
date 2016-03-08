package ph.txtdis.fx.dialog;

import static javafx.beans.binding.Bindings.notEqual;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.util.NumberUtils.toLong;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableLongValue;
import ph.txtdis.dto.Customer;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.CustomerService;

@Scope("prototype")
@Component("customerDialog")
public class CustomerDialog extends NameListDialog<Customer, CustomerService> {

	@Autowired
	private LabeledField<Long> idField;

	private ObservableLongValue id;

	public CustomerDialog addNameForCode(String idText) {
		long id = toLong(idText);
		this.id = new SimpleLongProperty(id);
		return this;
	}

	@Override
	public void refresh() {
		super.refresh();
		if (id != null)
			try {
				setForNameOnlyInput();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private LabeledField<Long> idField() {
		idField.name("Code").build(ID);
		idField.setOnAction(e -> verifyIdIsUnique(idField.getValue()));
		idField.disableIf(notEqual(0L, id));
		return idField;
	}

	private void setForNameOnlyInput() {
		idField.setValue(id.get());
	}

	private void verifyIdIsUnique(Long id) {
		if (this.id == null && id != 0)
			try {
				service.duplicateExists(id);
			} catch (Exception e) {
				dialog.show(e).addParent(this).start();
				refresh();
			}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>();
		l.add(idField());
		l.addAll(super.addNodes());
		return l;
	}

	@Override
	protected Customer createEntity() {
		try {
			return service.save(idField.getValue(), nameField.getValue());
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Customer";
	}
}