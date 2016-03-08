package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.QualityType;
import ph.txtdis.type.UomType;

@Data
@Entity
@Table(name = "stock_take_detail")
@EqualsAndHashCode(callSuper = true)
public class StockTakingDetail extends AbstractId<Long> {

	private static final long serialVersionUID = 4692138441515885681L;

	@JoinColumn(name = "stock_take_id")
	@ManyToOne(optional = false, cascade = ALL)
	private StockTaking stockTaking;

	@ManyToOne(optional = false)
	private Item item;

	@Column(nullable = false)
	private UomType uom;

	@Column(nullable = false, precision = 10, scale = 4)
	private BigDecimal qty;

	@Column(nullable = false)
	private QualityType quality;
}
