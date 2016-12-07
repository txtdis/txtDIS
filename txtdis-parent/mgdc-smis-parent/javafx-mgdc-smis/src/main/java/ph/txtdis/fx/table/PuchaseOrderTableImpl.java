package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.INTEGER;
import static ph.txtdis.type.Type.QUANTITY;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.fx.dialog.PurchasingDialog;
import ph.txtdis.service.BillableService;

@Component("puchaseOrderTable")
public class PuchaseOrderTableImpl extends AbstractBillableTable<BillableService, PurchasingDialog>
		implements PurchaseOrderTable {

	@Autowired
	private Column<BillableDetail, BigDecimal> qtyInDecimals;

	@Autowired
	private Column<BillableDetail, Integer> onPurchaseDaysLevel, onReceiptDaysLevel;

	@Override
	public void initializeColumns() {
		super.initializeColumns();
		onPurchaseDaysLevel.ofType(INTEGER).width(120).build("On-Purchase Days Lavel", "onPurchaseDaysLevel");
		onReceiptDaysLevel.ofType(INTEGER).width(120).build("On-Receipt Days Lavel", "onReceiptDaysLevel");
		qtyInDecimals.ofType(QUANTITY).build("Quantity", qty());
	}

	@Override
	protected TableColumn<BillableDetail, ?> qtyColumn() {
		return qtyInDecimals;
	}
}
