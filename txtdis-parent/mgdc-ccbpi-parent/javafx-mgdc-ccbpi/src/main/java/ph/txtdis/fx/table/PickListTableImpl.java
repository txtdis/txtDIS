package ph.txtdis.fx.table;

import static ph.txtdis.type.Type.TEXT;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component("pickListTable")
public class PickListTableImpl extends AbstractPickListTable {

	@Override
	@SuppressWarnings("unchecked")
	protected void addColumns() {
		getColumns().setAll(//
				location.launches(app).ofType(TEXT).width(180).build("Order No.", "location"), //
				name.launches(app).ofType(TEXT).width(180).build("Name", "customer"), //
				route.launches(app).ofType(TEXT).width(60).build("Route", "route"));
	}
}
