package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.beans.factory.annotation.Autowired;
import ph.txtdis.dto.Billable;
import ph.txtdis.exception.FailedPrintingException;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.printer.ReturnedMaterialPrinter;
import ph.txtdis.mgdc.gsm.repository.RmaRepository;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static ph.txtdis.util.UserUtils.username;

public abstract class AbstractRmaService<AR extends RmaRepository> //
	extends AbstractSpunSavedBillableService //
	implements BillingDataService,
	RmaService,
	ReceivableService {

	@Autowired
	protected AR rmaRepository;

	@Autowired
	private ReturnedMaterialPrinter printer;

	@Override
	public Billable findOpenRmaByCustomerId(Long id) throws Exception {
		BillableEntity e = openRmaByCustomerId(id);
		return throwNotClosedExceptionIfExists(e.getCustomer() + "'s RMA No. " + e.getBookingId(), e, id);
	}

	private BillableEntity openRmaByCustomerId(Long id) {
		List<BillableEntity> l = rmaRepository.findByNumIdNullAndRmaNotNullAndCustomerId(id);
		return notCancelledOnly(l);
	}

	private BillableEntity notCancelledOnly(List<BillableEntity> l) {
		try {
			return l.stream().filter(b -> b.getIsValid() == null || b.getIsValid()).findFirst().get();
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Billable print(Long id) throws FailedPrintingException {
		BillableEntity e = findEntityByPrimaryKey(id);
		e = print(e);
		return toModel(e);
	}

	private BillableEntity print(BillableEntity b) throws FailedPrintingException {
		try {
			return printRma(b);
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedPrintingException(e.getMessage());
		}
	}

	@Override
	public Billable toModel(BillableEntity e) {
		Billable b = super.toModel(e);
		return b == null ? null : setPrintingData(b, e);
	}

	private BillableEntity printRma(BillableEntity b) throws Exception {
		if (!b.getOrderDate().isBefore(LocalDate.now()))
			printer.print(b);
		b.setReceivingModifiedBy(username());
		b.setReceivingModifiedOn(ZonedDateTime.now());
		return repository.save(b);
	}

	private Billable setPrintingData(Billable b, BillableEntity e) {
		b.setReceivingModifiedBy(null);
		b.setReceivingModifiedOn(null);
		b.setPrintedBy(e.getReceivingModifiedBy());
		b.setPrintedOn(e.getReceivingModifiedOn());
		return b;
	}

	@Override
	protected BillableEntity update(BillableEntity e, Billable b) {
		e = super.update(e, b);
		e = setOrderNoAndBillingData(e, b);
		return setReceivingData(e, b, repository);
	}
}