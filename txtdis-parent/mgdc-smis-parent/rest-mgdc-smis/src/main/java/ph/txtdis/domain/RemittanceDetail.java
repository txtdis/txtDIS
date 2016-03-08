package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "remittance_detail")
@EqualsAndHashCode(callSuper = true)
public class RemittanceDetail extends AbstractId<Long> {

	private static final long serialVersionUID = -968954060788678059L;

	@ManyToOne(optional = false, cascade = ALL)
	private Remittance remittance;

	@ManyToOne(optional = false, cascade = ALL)
	private Billing billing;

	@Column(name = "payment", precision = 8, scale = 2)
	private BigDecimal paymentValue;
}
