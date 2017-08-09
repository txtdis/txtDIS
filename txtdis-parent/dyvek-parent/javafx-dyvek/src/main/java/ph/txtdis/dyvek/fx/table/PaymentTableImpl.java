package ph.txtdis.dyvek.fx.table;

import javafx.scene.control.TableColumn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ph.txtdis.dto.RemittanceDetail;
import ph.txtdis.fx.table.AbstractPaymentTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.fx.table.PaymentTable;
import ph.txtdis.type.Type;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

@Scope("prototype")
@Component("paymentTable")
public class PaymentTableImpl //
	extends AbstractPaymentTable //
	implements PaymentTable {

	@Autowired
	private Column<RemittanceDetail, Boolean> selected;

	@Override
	protected List<TableColumn<RemittanceDetail, ?>> addColumns() {
		Column<RemittanceDetail, Boolean> c = selected.ofType(Type.CHECKBOX).build("", "selected");
		List<TableColumn<RemittanceDetail, ?>> l = new ArrayList<>(asList(c));
		l.addAll(super.addColumns());
		return l;
	}
}
