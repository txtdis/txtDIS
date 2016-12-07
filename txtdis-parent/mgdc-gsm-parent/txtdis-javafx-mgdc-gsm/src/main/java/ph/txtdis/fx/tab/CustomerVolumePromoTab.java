package ph.txtdis.fx.tab;

import static java.util.Arrays.asList;
import static javafx.geometry.Pos.CENTER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.table.CustomerVolumePromoTable;
import ph.txtdis.service.ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService;

@Scope("prototype")
@Component("customerVolumePromoTab")
public class CustomerVolumePromoTab extends AbstractTab {

	@Autowired
	private ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService volumePromoService;

	@Autowired
	private CustomerVolumePromoTable volumePromoTable;

	public CustomerVolumePromoTab() {
		super("Volume Promo");
	}

	@Override
	public void refresh() {
		volumePromoTable.items(volumePromoService.getVolumePromos());
	}

	@Override
	public void save() {
		volumePromoService.setVolumePromos(volumePromoTable.getItems());
	}

	@Override
	protected VBox mainVerticalPane() {
		VBox vbox = super.mainVerticalPane();
		vbox.setAlignment(CENTER);
		return vbox;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(box.forHorizontalPane(volumePromoTable.build()));
	}

	@Override
	protected void setBindings() {
	}

	@Override
	protected void setListeners() {
	}
}
