package ph.txtdis.mgdc.gsm.fx.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.mgdc.gsm.dto.Item;

import java.util.List;

public interface ItemInputtedDialog {

	List<InputNode<?>> addNodes(AbstractInputDialog inputDialog);

	Long getId();

	InputNode<String> getItemInput();

	void nextFocus();

	void refresh();

	void setName(String name);

	void updateUponVerification(Item t);

	Item validateItemExists() throws Exception;

	void setItemIdFieldOnAction(EventHandler<ActionEvent> e);
}