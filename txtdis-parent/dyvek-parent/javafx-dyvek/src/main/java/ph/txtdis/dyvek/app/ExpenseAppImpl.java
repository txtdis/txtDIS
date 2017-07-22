package ph.txtdis.dyvek.app;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import ph.txtdis.app.AbstractApp;
import ph.txtdis.dyvek.service.ExpenseService;

@Scope("prototype")
@Component("expenseApp")
public class ExpenseAppImpl //
		extends AbstractApp<ExpenseService>//
		implements ExpenseApp {

	@Override
	public void goToDefaultFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	protected String getFontIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getTitleText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getHeaderText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		// TODO Auto-generated method stub
		return null;
	}
}
