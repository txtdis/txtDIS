package ph.txtdis.mgdc.gsm.domain;

import static javax.persistence.CascadeType.ALL;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractStockTakeDetailEntity;

@Data
@Entity
@Table(name = "stock_take_detail")
@EqualsAndHashCode(callSuper = true)
public class StockTakeDetailEntity //
		extends AbstractStockTakeDetailEntity {

	private static final long serialVersionUID = 4692138441515885681L;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@JoinColumn(name = "stock_take_id")
	@ManyToOne(optional = false, cascade = ALL)
	private StockTakeEntity stockTaking;
}
