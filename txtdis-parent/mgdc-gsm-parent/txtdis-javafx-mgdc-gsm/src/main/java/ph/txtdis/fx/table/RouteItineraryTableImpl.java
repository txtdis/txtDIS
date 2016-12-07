package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Customer;
import ph.txtdis.service.StockTakeService;

@Scope("prototype")
@Component("routeItineraryTable")
public class RouteItineraryTableImpl extends AbstractTableView<Customer> implements RouteItineraryTable {

	private static final String NULL = "deactivatedBy";

	@Autowired
	private Column<Customer, Long> id, credit, check;

	@Autowired
	private Column<Customer, String> name, address, discount, invoice, amount, signature;

	@Autowired
	private StockTakeService stockTakeService;

	@Override
	protected void addColumns() {
		List<Column<Customer, ?>> columns = new ArrayList<>(Arrays.asList(//
				id.ofType(ID).width(30).build("ID", "id"), //
				name.ofType(TEXT).width(240).build("Outlet", "name"), //
				address.ofType(TEXT).width(500).build("Address", "address"), //
				credit.ofType(QUANTITY).width(24).verticalHeader().build("Credit", "creditTerm"), //
				discount.ofType(TEXT).width(24).verticalHeader().build("Discount", "discountType"), //
				check.ofType(QUANTITY).width(24).verticalHeader().build("Cheque", "gracePeriod") //
		));
		columns.addAll(itemOnStockColumns());
		columns.addAll(Arrays.asList(//
				invoice.ofType(TEXT).width(90).build("S/I No.", NULL), //
				amount.ofType(TEXT).width(90).build("Amount", NULL), //
				signature.ofType(TEXT).width(90).build("Signature", NULL) //
		));
		getColumns().setAll(columns);
	}

	private List<Column<Customer, String>> itemOnStockColumns() {
		List<String> itemsOnStock = itemsOnStock();
		List<Column<Customer, String>> columns = new ArrayList<>();
		for (int i = 0; i < 30; i++)
			columns.add(new Column<Customer, String>().ofType(TEXT).width(12).verticalHeader()
					.build(itemName(itemsOnStock, i), NULL));
		return columns;
	}

	private List<String> itemsOnStock() {
		try {
			return stockTakeService.listItemsOnStock();
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}

	private String itemName(List<String> itemsOnStock, int i) {
		return i >= itemsOnStock.size() ? "" : itemsOnStock.get(i);
	}
}
