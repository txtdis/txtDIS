package ph.txtdis.mgdc.gsm.domain;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractStockTakeVarianceEntity;

@Data
@Entity
@Table(name = "stock_take_variance")
@EqualsAndHashCode(callSuper = true)
public class StockTakeVarianceEntity //
		extends AbstractStockTakeVarianceEntity {

	private static final long serialVersionUID = 9092668909841849439L;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@Override
	public String toString() {
		return getCountDate() + ": " //
				+ getItem().getName() + ", " //
				+ getQuality() //
				+ ", start= " + getStartQty() //
				+ ", in= " + getInQty() //
				+ ", out= " + getOutQty() // 
				+ ", actual= " + getActualQty() //
				+ ", final= " + getFinalQty();
	}
}
