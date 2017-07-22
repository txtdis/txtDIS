package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import javafx.scene.control.Button;
import ph.txtdis.fx.control.AppButtonImpl;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.pane.AppGridPane;

public abstract class AbstractFieldDialog<T> //
		extends AbstractInputDialog //
		implements InputtedDialog<T> {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	protected AppButtonImpl addButton;

	protected T entity;

	protected List<InputNode<?>> inputNodes;

	public AbstractFieldDialog() {
		super();
		inputNodes = null;
	}

	@Override
	protected Button[] buttons() {
		return new Button[] { addButton(), closeButton() };
	}

	protected Button addButton() {
		addButton.large("Add").build();
		addButton.onAction(event -> addItem());
		addButton.disableIf(getAddButtonDisableBindings());
		return addButton;
	}

	protected void addItem() {
		entity = createEntity();
		refresh();
		close();
	}

	protected abstract T createEntity();

	protected BooleanBinding getAddButtonDisableBindings() {
		BooleanBinding binding = inputNodes.get(0).isEmpty();
		for (int i = 1; i < inputNodes.size(); i++)
			binding = binding.or(inputNodes.get(i).isEmpty());
		return binding;
	}

	@Override
	public List<T> getAddedItems() {
		return entity == null ? null : asList(entity);
	}

	@Override
	protected String headerText() {
		return "Add New " + super.headerText();
	}

	@Override
	protected List<Node> nodes() {
		return asList(header(), grid(), buttonBox());
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		populateGrid();
		return grid;
	}

	private void populateGrid() {
		inputNodes = addNodes();
		if (inputNodes != null)
			putNodes();
	}

	protected abstract List<InputNode<?>> addNodes();

	private void putNodes() {
		for (int row = 0; row < inputNodes.size(); row++)
			putLabeledControls(row);
	}

	private void putLabeledControls(int row) {
		List<Node> nodes = inputNodes.get(row).getNodes();
		for (int column = 0; column < nodes.size(); column++)
			grid.add(nodes.get(column), column, row);
	}

	@Override
	protected void nullData() {
		super.nullData();
		entity = null;
	}

	@Override
	public void refresh() {
		inputNodes.forEach(inputNode -> inputNode.reset());
		super.refresh();
	}

	protected void resetNodesOnError(Throwable e) {
		e.printStackTrace();
		dialog.show((Exception) e).addParent(this).start();
		refresh();
	}

	@Override
	public void goToDefaultFocus() {
		inputNodes.get(0).requestFocus();
	}
}
