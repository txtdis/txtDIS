package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "stock_take")
@EqualsAndHashCode(callSuper = true)
public class StockTakeEntity extends AbstractCreatedEntity<Long> {

	private static final long serialVersionUID = 7630222117410458884L;

	@ManyToOne(optional = false)
	private WarehouseEntity warehouse;

	@ManyToOne(optional = false)
	private UserEntity taker;

	@ManyToOne(optional = false)
	private UserEntity checker;

	@Column(name = "stock_take_date", nullable = false)
	private LocalDate stockTakeDate;

	@OrderBy("item ASC")
	@OneToMany(mappedBy = "stockTaking", cascade = ALL)
	private List<StockTakeDetailEntity> details;
}
