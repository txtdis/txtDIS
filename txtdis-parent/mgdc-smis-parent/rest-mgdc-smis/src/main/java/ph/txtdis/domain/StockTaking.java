package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "stock_take")
@EqualsAndHashCode(callSuper = true)
public class StockTaking extends CreationTracked<Long> {

	private static final long serialVersionUID = 7630222117410458884L;

	@ManyToOne(optional = false)
	private Warehouse warehouse;

	@ManyToOne(optional = false)
	private User taker;

	@ManyToOne(optional = false)
	private User checker;

	@Column(name = "stock_take_date", nullable = false)
	private LocalDate stockTakeDate;

	@OneToMany(mappedBy = "stockTaking", cascade = ALL)
	private List<StockTakingDetail> details;
}
