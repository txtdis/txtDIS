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
@EqualsAndHashCode(callSuper = true)
@Table(name = "tr_dsmr_h")
public class EdmsInventoryVariance
	extends EdmsAbstractWarehousedDatedRemarkedReferencedStatusCreatedId
	implements Serializable {

	private static final long serialVersionUID = -8228232219863703852L;

	@Column(name = "totEndBal")
	private BigDecimal totalEndQty;

	@Column(name = "totPhyCount")
	private BigDecimal totalStockTakeQty;

	@Column(name = "totVariance")
	private BigDecimal totalVarianceQty;

	@Column(name = "modiBy")
	private String modifiedBy;

	@Column(name = "dateModi")
	private String modifiedOn;
}
