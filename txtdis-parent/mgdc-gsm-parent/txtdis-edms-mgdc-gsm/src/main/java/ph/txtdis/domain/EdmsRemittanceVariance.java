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
@Table(name = "tr_rsr_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsRemittanceVariance extends EdmsAbstractDatedRemarkedReferencedStatusCreatedId implements Serializable {

	private static final long serialVersionUID = -2518161128745571598L;

	@Column(name = "modiBy")
	private String modifiedBy;

	@Column(name = "dateModi")
	private String modifiedOn;

	@Column(name = "salesman")
	private String sellerCode;

	@Column(name = "driver")
	private String driverCode;

	@Column(name = "helper")
	private String helperCode;

	@Column(name = "cashDay")
	private BigDecimal cashPaymentValue;

	@Column(name = "cashCollection")
	private BigDecimal cashCollectionValue;

	@Column(name = "cashTotal")
	private BigDecimal totalCashValue;

	@Column(name = "dcDay")
	private BigDecimal datedChequePaymentValue;

	@Column(name = "dcCollection")
	private BigDecimal datedChequeCollectionValue;

	@Column(name = "dcTotal")
	private BigDecimal totalDatedChequeValue;

	@Column(name = "pdcDay")
	private BigDecimal postDatedChequePaymentValue;

	@Column(name = "pdcCollection")
	private BigDecimal postDatedChequeCollectionValue;

	@Column(name = "pdcTotal")
	private BigDecimal totalPostDatedChequeValue;

	@Column(name = "cmDay")
	private BigDecimal creditMemoPaymentValue;

	@Column(name = "cmCollection")
	private BigDecimal creditMemoCollectionValue;

	@Column(name = "cmTotal")
	private BigDecimal totalCreditMemoValue;

	@Column(name = "cnDay")
	private BigDecimal creditNotePaymentValue;

	@Column(name = "cnCollection")
	private BigDecimal creditNoteCollectionValue;

	@Column(name = "cnTotal")
	private BigDecimal totalCreditNoteValue;

	@Column(name = "stDay")
	private BigDecimal subTotalPaymentValue;

	@Column(name = "stCollection")
	private BigDecimal subTotalCollectionValue;

	@Column(name = "stTotal")
	private BigDecimal subTotalValue;

	@Column(name = "creditSales")
	private BigDecimal creditValue;

	@Column(name = "total")
	private BigDecimal totalValue;

	@Column(name = "grossSales")
	private BigDecimal grossValue;

	@Column(name = "discount")
	private BigDecimal discount;

	@Column(name = "empties")
	private BigDecimal emptyValue;

	@Column(name = "cm")
	private BigDecimal creditMemoValue;

	@Column(name = "cn")
	private BigDecimal creditNoteValue;

	@Column(name = "lessGS")
	private BigDecimal discountValue;

	@Column(name = "netSales")
	private BigDecimal netValue;

	@Column(name = "netRemittance")
	private BigDecimal netRemittanceValue;

	@Column(name = "actualRemittance")
	private BigDecimal actualRemittanceValue;

	@Column(name = "overShort")
	private BigDecimal varianceValue;

	@Column(name = "osStat")
	private String status;
}
