package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_dsmr_d")
@EqualsAndHashCode(callSuper = true)
public class EdmsInventoryVarianceDetail extends EdmsAbstractReferencedItemCodeIdDetail implements Serializable {

	private static final long serialVersionUID = -7812225856101060992L;

	@Column(name = "itemName")
	private String itemName;

	@Column(name = "begBal")
	private BigDecimal beginningQty;

	@Column(name = "pGsmi")
	private BigDecimal purchaseReceiptQty;

	@Column(name = "ilr")
	private BigDecimal loadReceiptQty;

	@Column(name = "stockIn")
	private BigDecimal transferReceiptQty;

	@Column(name = "stockFrom")
	private BigDecimal safekeepReceiptQty;

	@Column(name = "totalReceipt")
	private BigDecimal totalReceiptQty;

	@Column(name = "logp")
	private BigDecimal pickedQty;

	@Column(name = "stockOut")
	private BigDecimal transferredQty;

	@Column(name = "stockTo")
	private BigDecimal safekeptQty;

	@Column(name = "totalIssuance")
	private BigDecimal totalIssuedQty;

	@Column(name = "endBal")
	private BigDecimal endingQty;

	@Column(name = "caseCount")
	private BigDecimal caseQty;

	@Column(name = "bottleCount")
	private BigDecimal bottleQty;

	@Column(name = "physicalCount")
	private BigDecimal stockTakeQty;

	@Column(name = "xVariance")
	private BigDecimal varianceQty;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "grossInvoice")
	private BigDecimal billedQty;

	@Column(name = "logpLessIlr")
	private BigDecimal soldQty;

	@Column(name = "giVariance")
	private BigDecimal billingVarianceQty;

	@Column(name = "empties")
	private BigDecimal emptyQty;

	@Column(name = "cm")
	private BigDecimal returnedQty;

	@Column(name = "netVariance")
	private BigDecimal netVarianceQty;
}
