package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TWOPLACE;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.SalesVolume;
import ph.txtdis.type.UomType;

public abstract class SalesVolumeAbstractTable extends AppTable<SalesVolume> {

	@Autowired
	protected Column<SalesVolume, LocalDate> orderDate;

	@Autowired
	protected Column<SalesVolume, String> seller, channel, customer, category, prodLine, item;

	@Autowired
	private Column<SalesVolume, UomType> uom;

	@Autowired
	private Column<SalesVolume, BigDecimal> vol, qty;

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				orderDate.ofType(DATE).build("Date", "orderDate"), //
				seller.ofType(TEXT).width(100).build("Seller", "seller"), //
				channel.ofType(TEXT).width(180).build("Channel", "channel"), //
				customer.ofType(TEXT).width(300).build("Customer", "customer"), //
				category.ofType(TEXT).width(120).build("Category", "category"), //
				prodLine.ofType(TEXT).width(120).build("Product Line", "productLine"), //
				item.ofType(TEXT).width(420).build("Item", "item"), //
				vol.ofType(TWOPLACE).build("Volume", "vol"), //
				uom.ofType(ENUM).width(60).build("UOM", "uom"), //
				qty.ofType(QUANTITY).width(90).build("Qty(PC)", "qty") //
		);
	}
}
