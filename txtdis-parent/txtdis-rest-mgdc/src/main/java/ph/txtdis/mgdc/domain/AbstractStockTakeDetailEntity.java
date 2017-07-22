package ph.txtdis.mgdc.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractStockTakeDetailEntity //
		extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = 49763029717562192L;

	@Column(nullable = false)
	private UomType uom;

	@Column(nullable = false, precision = 10, scale = 4)
	private BigDecimal qty;

	@Column(nullable = false)
	private QualityType quality;
}
