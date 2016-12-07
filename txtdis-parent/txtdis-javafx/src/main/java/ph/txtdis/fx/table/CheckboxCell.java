package ph.txtdis.fx.table;

import org.apache.commons.lang3.StringUtils;

import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxTableCell;
import ph.txtdis.util.ReflectionUtils;

public class CheckboxCell<T extends Object> extends CheckBoxTableCell<T, Boolean> {

	private final CheckBox checkBox;
	private final String field;

	public CheckboxCell(String field) {
		this.checkBox = new CheckBox();
		this.field = field;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void updateItem(Boolean checked, boolean empty) {
		super.updateItem(checked, empty);
		if (checked != null && getTableRow() != null) {
			checkBox.setSelected(checked);
			checkBox.selectedProperty().addListener((check, oldValue, newValue) -> {
				Object item = getTableRow().getItem();
				setBoolean((T) item, newValue);
			});
			setGraphic(checkBox);
			setStyle("-fx-alignment: CENTER;");
		}
	}

	protected void setBoolean(T item, Boolean newValue) {
		ReflectionUtils.invokeOneParameterMethod(item, "set" + StringUtils.capitalize(field), newValue, boolean.class);
	}
}
