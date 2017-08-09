package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.dto.Billable;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;

import java.util.List;

public interface InvoiceService //
	extends ExTruckBillingService,
	ImportedBillingService,
	InvoiceInvalidationService {

	List<Billable> findAllBilledButUnpicked();

	List<BillableEntity> findAllById(List<Long> ids);

	List<Billable> findAllInvalidInvoices(List<Long> invoiceIds);

	List<Billable> findAllOutletBillings();

	Billable saveToEdms(Billable t) throws Exception;

	BillableEntity toEntity(Billable b);

	Billable toModel(BillableEntity e);

	void updateItemReturnPayment(String[] s);
}
