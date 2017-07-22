package ph.txtdis.dyvek.fx.dialog;

import ph.txtdis.app.StartableApp;
import ph.txtdis.dyvek.model.BillableDetail;

public interface AssignmentDialog
		extends StartableApp {

	BillableDetail getDetail();

	AssignmentDialog setDetail(BillableDetail detail);
}