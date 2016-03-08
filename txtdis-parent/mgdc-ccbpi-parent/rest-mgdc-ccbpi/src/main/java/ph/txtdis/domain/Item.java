package ph.txtdis.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "code"), //
		@Index(columnList = "name") })
public class Item extends Named<Long> {

	private static final long serialVersionUID = -3012020260825126952L;

	@Column(name = "bottle_per_case")
	private int bottlePerCase;

	@Column(name = "price")
	private BigDecimal priceValue;
}
