package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.FRACTION;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.Fraction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.ItemList;
import ph.txtdis.fx.dialog.BillingDialog;
import ph.txtdis.fx.dialog.FieldDialog;
import ph.txtdis.fx.dialog.ReceivingDialog;
import ph.txtdis.service.BillingService;

@Scope("prototype")
@Component("itemListTable")
public class ItemListTable extends AppTable<ItemList> {

	@Autowired
	private AppendContextMenu<ItemList> append;

	@Autowired
	private DeleteContextMenu<ItemList> subtract;

	@Autowired
	private Column<ItemList, Long> id;

	@Autowired
	private Column<ItemList, Fraction> quantity;

	@Autowired
	private Column<ItemList, String> name;

	@Autowired
	private Column<ItemList, BigDecimal> price, subtotal;

	@Autowired
	private BillingDialog billingDialog;

	@Autowired
	private ReceivingDialog receivingDialog;

	@Autowired
	private BillingService service;

	public FieldDialog<ItemList> dialog() {
		return service.isASalesOrder() ? billingDialog : receivingDialog;
	}

	public void initializeColumnsAndDialogs() {
		id.ofType(ID).build("Code", "itemCode");
		name.ofType(TEXT).width(320).build("Name", "itemName");
		price.ofType(CURRENCY).build("Price", "priceValue");
		quantity.ofType(FRACTION).build("Quantity", qty());
		subtotal.ofType(CURRENCY).build("Subtotal", "subtotalValue");
	}

	public String qty() {
		if (service.isASalesOrder())
			return "initialFraction";
		return service.isASalesReturn() ? "returnedFraction" : "netFraction";
	}

	private List<TableColumn<ItemList, ?>> columns() {
		List<TableColumn<ItemList, ?>> l = new ArrayList<>(asList(id, name));
		if (service.isASalesReturn())
			l.add(quantity);
		else
			l.addAll(asList(price, quantity, subtotal));
		return l;
	}

	@Override
	protected void addColumns() {
		initializeColumnsAndDialogs();
		getColumns().setAll(columns());
	}

	@Override
	protected void addProperties() {
		append.addMenu(this, dialog(), service);
		subtract.addMenu(this);
	}
}
