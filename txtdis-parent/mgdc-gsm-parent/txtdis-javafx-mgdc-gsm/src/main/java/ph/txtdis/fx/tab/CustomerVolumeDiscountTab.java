package ph.txtdis.fx.tab;

import static java.util.Arrays.asList;
import static javafx.geometry.Pos.CENTER;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.Node;
import javafx.scene.layout.VBox;
import ph.txtdis.fx.table.CustomerVolumeDiscountTable;
import ph.txtdis.service.ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService;

@Scope("prototype")
@Component("customerVolumeDiscountTab")
public class CustomerVolumeDiscountTab extends AbstractTab {

	@Autowired
	private ValueBasedCustomerDiscountWithVolumePromoAndDiscountCustomerService volumeDiscountService;

	@Autowired
	private CustomerVolumeDiscountTable volumeDiscountTable;

	public CustomerVolumeDiscountTab() {
		super("Volume Discount");
	}

	@Override
	public void refresh() {
		volumeDiscountTable.items(volumeDiscountService.getVolumeDiscounts());
	}

	@Override
	public void save() {
		volumeDiscountService.setVolumeDiscounts(volumeDiscountTable.getItems());
	}

	@Override
	protected VBox mainVerticalPane() {
		VBox vbox = super.mainVerticalPane();
		vbox.setAlignment(CENTER);
		return vbox;
	}

	@Override
	protected List<Node> mainVerticalPaneNodes() {
		return asList(box.forHorizontalPane(volumeDiscountTable.build()));
	}

	@Override
	protected void setBindings() {
	}

	@Override
	protected void setListeners() {
	}
}
