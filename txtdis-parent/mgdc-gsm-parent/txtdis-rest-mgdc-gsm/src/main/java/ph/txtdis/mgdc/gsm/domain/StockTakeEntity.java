package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractStockTakeEntity;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Data
@Entity
@Table(name = "stock_take")
@EqualsAndHashCode(callSuper = true)
public class StockTakeEntity //
	extends AbstractStockTakeEntity {

	private static final long serialVersionUID = 7630222117410458884L;

	@OrderBy("item ASC")
	@OneToMany(mappedBy = "stockTaking", cascade = ALL)
	private List<StockTakeDetailEntity> details;
}
