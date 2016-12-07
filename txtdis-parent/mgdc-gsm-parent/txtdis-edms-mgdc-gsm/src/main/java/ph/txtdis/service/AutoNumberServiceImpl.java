package ph.txtdis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsAutoNumber;
import ph.txtdis.domain.EdmsMasterAutoNumber;
import ph.txtdis.repository.EdmsAutoNumberRepository;
import ph.txtdis.repository.EdmsMasterAutoNumberRepository;
import ph.txtdis.util.Code;

@Service("autoNumberService")
public class AutoNumberServiceImpl implements AutoNumberService {

	private static final String LATEST_UPDATE = "01";

	@Autowired
	private EdmsAutoNumberRepository edmsAutoNumberRepository;

	@Autowired
	private EdmsMasterAutoNumberRepository edmsMasterAutoNumberRepository;

	private String getAutoNoName(String prefix) {
		if (prefix.equals(Code.CUSTOMER_PREFIX))
			return Code.CUSTOMER_AUTO_NO;
		return prefix;
	}

	@Override
	public void saveAutoNo(String prefix, String autoNo) {
		if (isMaster(prefix))
			saveMasterAutoNo(prefix, autoNo);
		else
			saveTransactionAutoNo(prefix, autoNo);
	}

	private boolean isMaster(String prefix) {
		return prefix.equals(Code.CUSTOMER_PREFIX) || prefix.equals(Code.ITEM_AUTO_NO);
	}

	private void saveMasterAutoNo(String prefix, String autoNo) {
		EdmsMasterAutoNumber e = edmsMasterAutoNumberRepository.findByName(getAutoNoName(prefix));
		e.setLastNo(autoNo);
		edmsMasterAutoNumberRepository.save(e);
	}

	private void saveTransactionAutoNo(String prefix, String autoNo) {
		EdmsAutoNumber e = edmsAutoNumberRepository.findByUpdateNo(LATEST_UPDATE);
		if (prefix.equals(Code.PICKING_PREFIX))
			e.setPickListNo(autoNo);
		else if (prefix.equals(Code.BOOKING_PREFIX))
			e.setSalesOrderNo(autoNo);
		else if (prefix.equals(Code.DELIVERY_PREFIX))
			e.setDeliveryNo(autoNo);
		else if (prefix.equals(Code.INVOICE_PREFIX))
			e.setInvoiceNo(autoNo);
		else if (prefix.equals(Code.RECEIVING_PREFIX))
			e.setReceivingNo(autoNo);
		else if (prefix.equals(Code.CREDIT_MEMO_PREFIX))
			e.setCreditMemoNo(autoNo);
		else if (prefix.equals(Code.REMITTANCE_VARIANCE_PREFIX))
			e.setRemittanceVarianceNo(autoNo);
		else if (prefix.equals(Code.INVENTORY_VARIANCE_PREFIX))
			e.setInventoryVarianceNo(autoNo);
		else if (prefix.equals(Code.REMITTANCE_PREFIX))
			e.setRemittanceNo(autoNo);
		else if (prefix.equals(Code.TRANSFER_ORDER_PREFIX))
			e.setTransferNo(autoNo);
		else if (prefix.equals(Code.TRANSFER_RECEIPT_PREFIX))
			e.setTransferReceiptNo(autoNo);
		else if (prefix.equals(Code.PAYMENT_REFERENCE_PREFIX))
			e.setPaymentReferenceNo(autoNo);
		else if (prefix.equals(Code.PURCHASE_RECEIPT_PREFIX))
			e.setPurchaseReceiptNo(autoNo);
		edmsAutoNumberRepository.save(e);
	}
}