package ph.txtdis.fx.dialog;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.TEXT;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import ph.txtdis.fx.control.AppButton;
import ph.txtdis.fx.control.AppFieldImpl;
import ph.txtdis.fx.pane.AppGridPane;

@Scope("prototype")
@Component("searchDialog")
public class SearchDialog
	extends AbstractInputDialog {

	@Autowired
	protected AppGridPane grid;

	@Autowired
	private AppFieldImpl<String> textField;

	private String searchText, criteria;

	public SearchDialog criteria(String criteria) {
		this.criteria = criteria;
		return this;
	}

	public String getText() {
		return searchText;
	}

	@Override
	public void goToDefaultFocus() {
		textField.requestFocus();
	}

	@Override
	protected List<AppButton> buttons() {
		return asList(findButton(), closeButton());
	}

	private AppButton findButton() {
		AppButton findButton = button.large("Find").build();
		findButton.onAction(event -> setEnteredText());
		return findButton;
	}

	private void setEnteredText() {
		searchText = textField.getText();
		textField.clear();
		close();
	}

	@Override
	protected String headerText() {
		return "Search";
	}

	@Override
	protected List<Node> nodes() {
		return asList(header(), grid(), buttonBox());
	}

	private AppGridPane grid() {
		grid.getChildren().clear();
		grid.add(help(), 0, 0);
		grid.add(textField.build(TEXT), 0, 1);
		return grid;
	}

	private Label help() {
		return new Label("Enter partial or full " + criteria + " to find a match; blank to list all");
	}

	@Override
	protected void nullData() {
		super.nullData();
		searchText = null;
	}
}
