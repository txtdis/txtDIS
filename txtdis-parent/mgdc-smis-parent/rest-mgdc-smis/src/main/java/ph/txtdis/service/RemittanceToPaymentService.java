package ph.txtdis.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.stereotype.Service;

import ph.txtdis.domain.Billing;
import ph.txtdis.domain.Customer;
import ph.txtdis.domain.Remittance;
import ph.txtdis.domain.RemittanceDetail;
import ph.txtdis.dto.Payment;
import ph.txtdis.dto.PaymentDetail;

@Service("remittanceToPaymentService")
public class RemittanceToPaymentService {

	public Payment toForHistoryPayment(Remittance r) {
		Payment p = new Payment();
		p.setId(r.getId());
		p.setValue(r.getValue());
		p.setPaymentDate(r.getPaymentDate());
		p.setDepositedOn(r.getDepositedOn());
		p.setIsValid(r.getIsValid());
		return p;
	}

	public Payment toIdOnlyPayment(Remittance r) {
		if (r == null)
			return null;
		Payment p = new Payment();
		p.setId(r.getId());
		return p;
	}

	public List<Payment> toPayment(List<Remittance> r) {
		return r == null ? null : convert(r);
	}

	public Payment toPayment(Remittance r) {
		return r == null ? null : convert(r);
	}

	private Billing billing(RemittanceDetail r) {
		return r.getBilling();
	}

	private List<Payment> convert(List<Remittance> l) {
		return l.stream().map(r -> convert(r)).collect(toList());
	}

	private Payment convert(Remittance r) {
		Payment p = new Payment();
		p.setId(r.getId());
		p.setValue(r.getValue());
		p.setDetails(details(r));
		p.setPaymentDate(r.getPaymentDate());
		p.setRemarks(r.getRemarks());
		p.setCollector(r.getCollector());
		p.setCreatedBy(r.getCreatedBy());
		p.setCreatedOn(r.getCreatedOn());
		if (r.getIsValid() != null)
			setAuditData(r, p);
		if (r.getDepositedOn() != null)
			setDepositData(r, p);
		if (r.getReceivedOn() != null)
			setTransferData(r, p);
		if (r.getCheckId() != null)
			setCheckData(r, p);
		return p;
	}

	private Customer depositorBank(Remittance r) {
		Customer c = new Customer();
		c.setName(r.getDepositorBank().getName());
		return c;
	}

	private PaymentDetail detail(RemittanceDetail r) {
		PaymentDetail p = new PaymentDetail();
		p.setId(billing(r).getId());
		p.setOrderNo(billing(r).getOrderNo());
		p.setCustomerName(billing(r).getCustomer().getName());
		p.setDueDate(billing(r).getDueDate());
		p.setTotalDueValue(billing(r).getTotalValue());
		p.setPaymentValue(r.getPaymentValue());
		return p;
	}

	private List<PaymentDetail> details(Remittance r) {
		return r.getDetails().stream().map(d -> detail(d)).collect(toList());
	}

	private Customer draweeBank(Remittance r) {
		Customer c = new Customer();
		c.setId(r.getDraweeBank().getId());
		c.setName(r.getDraweeBank().getName());
		return c;
	}

	private void setAuditData(Remittance r, Payment p) {
		p.setIsValid(r.getIsValid());
		p.setDecidedBy(r.getDecidedBy());
		p.setDecidedOn(r.getDecidedOn());
	}

	private void setCheckData(Remittance r, Payment p) {
		p.setCheckId(r.getCheckId());
		p.setDraweeBank(draweeBank(r).getName());
	}

	private void setDepositData(Remittance r, Payment p) {
		p.setDepositorBank(depositorBank(r).getName());
		p.setDepositedOn(r.getDepositedOn());
		p.setDepositor(r.getDepositor());
		p.setDepositorOn(r.getDepositorOn());
	}

	private void setTransferData(Remittance r, Payment p) {
		p.setReceivedBy(r.getReceivedBy());
		p.setReceivedOn(r.getReceivedOn());
	}
}
