package ph.txtdis.dyvek.fx.dialog;

import ph.txtdis.app.App;
import ph.txtdis.dyvek.model.BillableDetail;

public interface AssignmentDialog
	extends App {

	BillableDetail getDetail();

	AssignmentDialog setDetail(BillableDetail detail);
}