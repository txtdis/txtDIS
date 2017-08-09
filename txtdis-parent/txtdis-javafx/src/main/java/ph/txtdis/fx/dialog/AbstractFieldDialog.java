package ph.txtdis.fx.dialog;

import javafx.beans.binding.BooleanBinding;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.pane.AppGridPane;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public abstract class AbstractFieldDialog<T> //
	extends AbstractInputDialog //
	implements InputtedDialog<T> {

	@Autowired
	protected AppGridPane grid;

	protected T entity;

	private List<InputNode<?>> inputNodes;

	public AbstractFieldDialog() {
		super();
		inputNodes = null;
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(addButton(), closeButton());
	}

	protected AppButton addButton() {
		AppButton addButton = button.large("Add").build();
		addButton.onAction(event -> addItem());
		addButton.disableIf(getAddButtonDisableBindings());
		return addButton;
	}

	protected void addItem() {
		entity = createEntity();
		refresh();
		close();
	}

	protected BooleanBinding getAddButtonDisableBindings() {
		BooleanBinding binding = inputNodes.get(0).isEmpty();
		for (int i = 1; i < inputNodes.size(); i++)
			binding = binding.or(inputNodes.get(i).isEmpty());
		return binding;
	}

	protected abstract T createEntity();

	@Override
	public void refresh() {
		inputNodes.forEach(InputNode::reset);
		super.refresh();
	}

	@Override
	public List<T> getAddedItems() {
		return entity == null ? null : singletonList(entity);
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

	protected void resetNodesOnError(Throwable e) {
		e.printStackTrace();
		messageDialog().show((Exception) e).addParent(this).start();
		refresh();
	}

	@Override
	public void goToDefaultFocus() {
		inputNodes.get(0).requestFocus();
	}
}
