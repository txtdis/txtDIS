package ph.txtdis.fx.dialog;

import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import ph.txtdis.dto.Item;
import ph.txtdis.fx.control.InputNode;

public interface ItemInputtedDialog {

	List<InputNode<?>> addNodes(AbstractInputDialog inputDialog);

	Long getId();

	InputNode<String> getItemInput();

	void nextFocus();

	void refresh();

	void setItemIdFieldOnAction(EventHandler<ActionEvent> e);

	void setName(String name);

	void updateUponVerification(Item t);

	Item validateItemExists() throws Exception;

}