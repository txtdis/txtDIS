package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Bom extends CreationTracked<Long> {

	private static final long serialVersionUID = -86213987350272414L;

	@ManyToOne(optional = false)
	private Item part;

	@Column(nullable = false, precision = 8, scale = 4)
	private BigDecimal qty;
}
