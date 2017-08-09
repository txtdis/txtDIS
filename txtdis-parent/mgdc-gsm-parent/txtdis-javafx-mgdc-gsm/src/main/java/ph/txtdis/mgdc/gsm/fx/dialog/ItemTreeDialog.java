package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.ItemTree;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledCombo;
import ph.txtdis.fx.dialog.AbstractFieldDialog;
import ph.txtdis.info.Information;
import ph.txtdis.mgdc.gsm.service.ItemTreeService;

import java.util.Arrays;
import java.util.List;

@Scope("prototype")
@Component("itemTreeDialog")
public class ItemTreeDialog
	extends AbstractFieldDialog<ItemTree> {

	@Autowired
	private LabeledCombo<ItemFamily> familyCombo;

	@Autowired
	private LabeledCombo<ItemFamily> parentCombo;

	@Autowired
	private ItemTreeService service;

	@Override
	protected List<InputNode<?>> addNodes() {
		setComboItems();
		setComboOnAction();
		return Arrays.asList(familyCombo, parentCombo);
	}

	private void setComboItems() {
		setComboItems(familyCombo.name("Family"));
		setComboItems(parentCombo.name("Parent"));
	}

	private void setComboOnAction() {
		familyCombo.onAction(event -> parentCombo.items(getParents()));
		parentCombo.onAction(event -> verifyTreeIsUnique(familyCombo.getValue(), parentCombo.getValue()));
	}

	private void setComboItems(LabeledCombo<ItemFamily> combo) {
		try {
			combo.items(service.listFamilies()).build();
		} catch (Exception e) {
			e.printStackTrace();
			resetNodesOnError(e);
		}
	}

	private ObservableList<ItemFamily> getParents() {
		try {
			return FXCollections.observableArrayList(service.listParents(familyCombo.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
			resetNodesOnError(e);
			return null;
		}
	}

	private void verifyTreeIsUnique(ItemFamily family, ItemFamily parent) {
		try {
			if (family != null && parent != null)
				service.isDuplicated(family, parent);
		} catch (Exception e) {
			e.printStackTrace();
			resetNodesOnError(e);
		}
	}

	@Override
	protected ItemTree createEntity() {
		try {
			return service.save(familyCombo.getValue(), parentCombo.getValue());
		} catch (Exception | Information e) {
			e.printStackTrace();
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Item Tree";
	}
}
