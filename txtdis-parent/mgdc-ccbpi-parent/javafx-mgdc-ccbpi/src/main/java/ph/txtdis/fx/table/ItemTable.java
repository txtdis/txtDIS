package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TIMESTAMP;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Item;
import ph.txtdis.fx.dialog.ItemDialog;

@Lazy
@Component("itemTable")
public class ItemTable extends NameListTable<Item, ItemDialog> {

	@Autowired
	private Column<Item, BigDecimal> price;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				id.ofType(ID).build("Code", "code"), //
				name.ofType(TEXT).width(320).build("Name", "name"), //
				price.ofType(CURRENCY).build("Price", "priceValue"), //
				createdBy.ofType(TEXT).width(160).build("Last Modified by", "createdBy"), //
				createdOn.ofType(TIMESTAMP).build("Last Modified on", "createdOn"));
	}
}
