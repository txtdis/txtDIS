package ph.txtdis.mgdc.ccbpi.fx.dialog;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.dialog.AbstractInputDialog;
import ph.txtdis.mgdc.ccbpi.dto.Item;

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