package ph.txtdis.fx.dialog;

import java.util.List;

public interface InputtedDialog<T> {

	List<T> getAddedItems();

	void showAndWait();
}
