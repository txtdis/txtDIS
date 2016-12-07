package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "sys_autonum_tr")
public class EdmsAutoNumber {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "machineNo")
	private String updateNo;

	@Column(name = "logp")
	private String pickListNo;

	@Column(name = "so")
	private String salesOrderNo;

	@Column(name = "dr")
	private String deliveryNo;

	@Column(name = "si")
	private String invoiceNo;

	@Column(name = "ilr")
	private String receivingNo;

	@Column(name = "cm")
	private String creditMemoNo;

	@Column(name = "rsr")
	private String remittanceVarianceNo;

	@Column(name = "dsmr")
	private String inventoryVarianceNo;

	@Column(name = "customerPayment")
	private String remittanceNo;

	@Column(name = "customerNotes")
	private String customerNoteNo;

	@Column(name = "sti")
	private String transferReceiptNo;

	@Column(name = "sto")
	private String transferNo;

	@Column(name = "gsmisi")
	private String purchaseReceiptNo;

	@Column(name = "wtw")
	private String warehouseToWarehouseNo;

	@Column(name = "tp")
	private String tpNo;

	@Column(name = "payRef")
	private String paymentReferenceNo;
}
