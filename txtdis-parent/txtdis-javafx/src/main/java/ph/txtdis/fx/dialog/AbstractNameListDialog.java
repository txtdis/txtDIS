package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static ph.txtdis.type.Type.TEXT;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Keyed;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.info.Information;
import ph.txtdis.service.SavedByName;
import ph.txtdis.service.UniqueNamedService;

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
			messageDialog().show(e).addParent(this).start();
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
