package ph.txtdis.mgdc.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.BOOLEAN;
import static ph.txtdis.type.Type.DATE;
import static ph.txtdis.type.Type.ENUM;
import static ph.txtdis.type.Type.QUANTITY;
import static ph.txtdis.type.Type.TEXT;
import static ph.txtdis.type.Type.TWOPLACE;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.scene.control.TableColumn;
import ph.txtdis.dto.SalesVolume;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.type.UomType;

public abstract class AbstractSalesVolumeTable //
		extends AbstractTable<SalesVolume> {

	@Autowired
	private Column<SalesVolume, BigDecimal> vol, qty;

	@Autowired
	private Column<SalesVolume, Boolean> active;

	@Autowired
	protected Column<SalesVolume, LocalDate> orderDate, customerStartDate;

	@Autowired
	protected Column<SalesVolume, String> billingNo, seller, delivery, channel, category, prodLine, item, customer, street, barangay, city;

	@Autowired
	private Column<SalesVolume, UomType> uom;

	@Override
	protected List<TableColumn<SalesVolume, ?>> addColumns() {
		return asList( //
				billingNo.ofType(TEXT).width(120).build("S/I(D/R) No.", "billingNo"), //
				orderDate.ofType(DATE).build("Date", "orderDate"), //
				seller.ofType(TEXT).width(100).build("Seller", "seller"), //
				delivery.ofType(TEXT).width(100).build("Delivery", "delivery"), //
				channel.ofType(TEXT).width(240).build("Channel", "channel"), //
				customer.ofType(TEXT).width(300).build("Customer", "customer"), //
				active.ofType(BOOLEAN).build("Active", "active"), //
				customerStartDate.ofType(DATE).build("Start", "customerStartDate"), //
				street.ofType(TEXT).width(420).build("Street", "street"), //
				barangay.ofType(TEXT).width(240).build("Barangay", "barangay"), //
				city.ofType(TEXT).width(180).build("City", "city"), //
				category.ofType(TEXT).width(120).build("Category", "category"), //
				prodLine.ofType(TEXT).width(180).build("Product Line", "productLine"), //
				item.ofType(TEXT).width(420).build("Item", "item"), //
				vol.ofType(TWOPLACE).build("Volume", "vol"), //
				uom.ofType(ENUM).width(60).build("UOM", "uom"), //
				qty.ofType(QUANTITY).width(90).build("Qty(PC)", "qty") //
		);
	}
}
