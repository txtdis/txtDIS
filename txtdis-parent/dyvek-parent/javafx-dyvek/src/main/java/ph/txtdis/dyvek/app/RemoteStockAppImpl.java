package ph.txtdis.dyvek.app;

import javafx.scene.Node;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.app.AbstractApp;
import ph.txtdis.dyvek.service.RemoteStockService;

import java.util.List;

@Scope("prototype")
@Component("remoteStockApp")
public class RemoteStockAppImpl
	extends AbstractApp<RemoteStockService>
	implements RemoteStockApp {

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
