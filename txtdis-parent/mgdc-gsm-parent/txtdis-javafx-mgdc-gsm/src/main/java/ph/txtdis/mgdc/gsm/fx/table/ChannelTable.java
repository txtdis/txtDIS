package ph.txtdis.mgdc.gsm.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.BOOLEAN;
import static ph.txtdis.type.Type.ENUM;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.scene.control.TableColumn;
import ph.txtdis.fx.table.AbstractNameListTable;
import ph.txtdis.fx.table.Column;
import ph.txtdis.mgdc.gsm.dto.Channel;
import ph.txtdis.mgdc.gsm.fx.dialog.ChannelDialog;
import ph.txtdis.type.BillingType;

@Scope("prototype")
@Component("channelTable")
public class ChannelTable extends AbstractNameListTable<Channel, ChannelDialog> {

	@Autowired
	private Column<Channel, BillingType> type;

	@Autowired
	private Column<Channel, Boolean> visited;

	@Override
	protected List<TableColumn<Channel, ?>> addColumns() {
		List<TableColumn<Channel, ?>> l = new ArrayList<>(super.addColumns());
		l.addAll(2, asList(type.ofType(ENUM).build("Billing Type", "billingType"), //
				visited.ofType(BOOLEAN).width(70).build("Visited", "visited")));
		return l;
	}
}
