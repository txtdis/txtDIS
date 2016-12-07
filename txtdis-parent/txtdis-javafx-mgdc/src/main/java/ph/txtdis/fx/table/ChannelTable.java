package ph.txtdis.fx.table;

import static java.util.Arrays.asList;
import static ph.txtdis.type.Type.BOOLEAN;
import static ph.txtdis.type.Type.ENUM;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ph.txtdis.dto.Channel;
import ph.txtdis.fx.dialog.ChannelDialog;
import ph.txtdis.type.BillingType;

@Scope("prototype")
@Component("channelTable")
public class ChannelTable extends AbstractNameListTable<Channel, ChannelDialog> {

	@Autowired
	private Column<Channel, BillingType> type;

	@Autowired
	private Column<Channel, Boolean> visited;

	@Override
	protected void addColumns() {
		super.addColumns();
		getColumns().addAll(2, //
				asList(type.ofType(ENUM).build("Billing Type", "billingType"), //
						visited.ofType(BOOLEAN).width(70).build("Visited", "visited")));
	}
}
