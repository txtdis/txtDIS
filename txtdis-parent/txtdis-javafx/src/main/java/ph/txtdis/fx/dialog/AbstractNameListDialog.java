package ph.txtdis.fx.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.info.Information;
import ph.txtdis.service.SavedByName;
import ph.txtdis.service.UniqueNamedService;

import java.util.List;

import static java.util.Collections.singletonList;
import static ph.txtdis.type.Type.TEXT;

public abstract class AbstractNameListDialog<T extends Keyed<Long>, S extends UniqueNamedService<T>> //
	extends AbstractFieldDialog<T> {

	@Autowired
	protected LabeledField<String> nameField;

	@Autowired
	protected S service;

	@Override
	protected List<InputNode<?>> addNodes() {
		nameField.name("Name").build(TEXT);
		nameField.onAction(e -> verifyNameIsUnique());
		return singletonList(nameField);
	}

	private void verifyNameIsUnique() {
		try {
			findDuplicate(nameField.getValue());
		} catch (Exception e) {
			showErrorDialog(e);
			refresh();
		}
	}

	private void findDuplicate(String name) throws Exception {
		if (!name.isEmpty())
			service.confirmUniqueness(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected T createEntity() {
		try {
			return ((SavedByName<T>) service).save(nameField.getValue());
		} catch (Exception | Information e) {
			resetNodesOnError(e);
			return null;
		}
	}
}
