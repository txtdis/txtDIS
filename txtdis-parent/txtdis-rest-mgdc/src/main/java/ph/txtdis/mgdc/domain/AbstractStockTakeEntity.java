package ph.txtdis.mgdc.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.domain.UserEntity;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public class AbstractStockTakeEntity //
		extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -2404308873876734935L;

	@ManyToOne(optional = false)
	private WarehouseEntity warehouse;

	@ManyToOne(optional = false)
	private UserEntity taker;

	@ManyToOne(optional = false)
	private UserEntity checker;

	@Column(name = "stock_take_date", nullable = false)
	private LocalDate stockTakeDate;
}
