package ph.txtdis.mgdc.ccbpi.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;

@Data
@Entity
@Table(name = "bom")
@EqualsAndHashCode(callSuper = true)
public class BomEntity //
	extends AbstractCreatedKeyedEntity<Long> {

	private static final long serialVersionUID = -86213987350272414L;

	@ManyToOne(optional = false, cascade = ALL)
	private ItemEntity item;

	@ManyToOne(optional = false)
	private ItemEntity part;

	@Column(nullable = false, precision = 8, scale = 4)
	private BigDecimal qty;

	@Override
	public String toString() {
		return item + "w/ " + qty + " " + part + "\n";
	}
}
