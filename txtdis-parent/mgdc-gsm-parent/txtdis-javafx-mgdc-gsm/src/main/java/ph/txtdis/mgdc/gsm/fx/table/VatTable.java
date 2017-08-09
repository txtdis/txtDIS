package ph.txtdis.mgdc.gsm.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.fx.table.AbstractTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.app.MultiTypeBillingApp;
import ph.txtdis.mgdc.gsm.dto.Vat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.*;

@Scope("prototype")
@Component("vatTable")
public class VatTable
	extends AbstractTable<Vat> {

	@Autowired
	private MultiTypeBillingApp app;

	@Autowired
	private Column<Vat, BigDecimal> value, vat, vatable;

	@Autowired
	private Column<Vat, LocalDate> orderDate;

	@Autowired
	private Column<Vat, Long> idNo;

	@Autowired
	private Column<Vat, String> prefix, suffix, customer;

	@Override
	protected List<TableColumn<Vat, ?>> addColumns() {
		return asList( //
			prefix.launches(app).ofType(CODE).width(60).build("Code", "prefix"), //
			idNo.launches(app).ofType(ID).build("No.", "nbrId"), //
			suffix.launches(app).ofType(ALPHA).width(65).build("Series", "suffix"), //
			customer.launches(app).ofType(TEXT).build("Customer", "customer"), //
			orderDate.launches(app).ofType(DATE).build("Date", "orderDate"), //
			value.launches(app).ofType(CURRENCY).build("Value", "value"), //
			vatable.launches(app).ofType(CURRENCY).build("Vatable", "vatableValue"), //
			vat.launches(app).ofType(CURRENCY).build("Vat", "vatValue"));
	}
}
