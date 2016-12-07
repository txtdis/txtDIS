package ph.txtdis.app;

import ph.txtdis.dto.Typed;
import ph.txtdis.type.BillableType;

public interface MultiTyped extends Typed {

	Startable type(BillableType type);
}
