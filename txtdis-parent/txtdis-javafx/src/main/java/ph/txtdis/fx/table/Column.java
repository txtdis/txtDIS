package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CHECKBOX;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import ph.txtdis.app.LaunchableApp;
import ph.txtdis.dto.Keyed;
import ph.txtdis.type.Type;
import ph.txtdis.util.TypeStyle;

@Scope("prototype")
@Component("column")
public class Column<S extends Keyed<?>, T>
	extends TableColumn<S, T>
	implements AppTableColumn {

	@Autowired
	private TabularCell<S, T> cell;

	private LaunchableApp app;

	private String data;

	private Type type;

	private boolean isEditable, isHeaderVertical;

	private int width;

	@Override
	public boolean isHeaderVertical() {
		return isHeaderVertical;
	}

	public Column<S, T> build(String name, String field) {
		setStyle(" -fx-opacity: 1; ");
		setText(name);
		setEditable(isEditable);
		makeHeaderWrappable(name);
		setId(field);
		setCellValueFactory(new PropertyValueFactory<>(field));
		setColumnWidth(width());
		setCellFactory(c -> cell(field));
		if (data != null)
			setUserData(data);
		return this;
	}

	private void makeHeaderWrappable(String name) {
		Label label = new Label(name);
		label.setStyle(" -fx-padding: 8px; ");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);
		setGraphic(labelStack(label));
	}

	private void setColumnWidth(int width) {
		setMinWidth(width);
		setPrefWidth(width);
		setMaxWidth(width);
	}

	private int width() {
		return width != 0 ? width : TypeStyle.width(type);
	}

	private TableCell<S, T> cell(String field) {
		if (type == CHECKBOX)
			return cell.get(field);
		return cell.get(app, type);
	}

	private StackPane labelStack(Label label) {
		StackPane stack = new StackPane();
		stack.getChildren().add(label);
		stack.prefWidthProperty().bind(widthProperty().subtract(5));
		label.prefWidthProperty().bind(stack.prefWidthProperty());
		return stack;
	}

	public Column<S, T> launches(LaunchableApp app) {
		this.app = app;
		return this;
	}

	public Column<S, T> addLaunchData(String data) {
		this.data = data;
		return this;
	}

	public Column<S, T> ofType(Type type) {
		this.type = type;
		this.isEditable = type == CHECKBOX;
		return this;
	}

	public Column<S, T> verticalHeader() {
		this.isHeaderVertical = true;
		return this;
	}

	public Column<S, T> width(int width) {
		this.width = width;
		return this;
	}
}
