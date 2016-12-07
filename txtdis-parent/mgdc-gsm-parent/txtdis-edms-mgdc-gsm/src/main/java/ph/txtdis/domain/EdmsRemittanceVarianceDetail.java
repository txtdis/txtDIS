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
@Table(name = "tr_rsr_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsRemittanceVarianceDetail extends EdmsAbstractReferencedItemCodeIdDetail implements Serializable {

	private static final long serialVersionUID = -641028816339275537L;

	@Column(name = "itemName")
	private String itemName;

	@Column(name = "xVariance")
	private BigDecimal varianceValue;

	@Column(name = "stat")
	private String status;

	@Column(name = "logpReg")
	private BigDecimal pickedQty;

	@Column(name = "logpPromo")
	private BigDecimal pickedPromoQty;

	@Column(name = "drSiSales")
	private BigDecimal billedQty;

	@Column(name = "drSiSalesAmount")
	private BigDecimal billedValue;

	@Column(name = "drSiEmpties")
	private BigDecimal emptyBottleQty;

	@Column(name = "drSiEmptiesAmount")
	private BigDecimal emptyBottleValue;

	@Column(name = "cmReturns")
	private BigDecimal returnedQty;

	@Column(name = "ilrFull")
	private BigDecimal receivedQty;

	@Column(name = "ilrReturns")
	private BigDecimal returnsReceivedQty;

	@Column(name = "ilrEmpties")
	private BigDecimal emptiesReceivedQty;

	@Column(name = "ilrPromo")
	private BigDecimal promoReceivedQty;
}
