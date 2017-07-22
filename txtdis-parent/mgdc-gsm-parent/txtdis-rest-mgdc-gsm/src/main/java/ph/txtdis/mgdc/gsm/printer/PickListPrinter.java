package ph.txtdis.mgdc.gsm.printer;

import ph.txtdis.mgdc.gsm.domain.PickListEntity;

public interface PickListPrinter {

	void print(PickListEntity p) throws Exception;
}
