package ph.txtdis.domain;

import static java.math.BigDecimal.ZERO;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "master_customerlist_ar")
public class EdmsAgingReceivable //
		implements Serializable {

	private static final long serialVersionUID = -3969533960824154885L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "custNo")
	private String customerCode;

	@Column(name = "fldCurrent")
	private BigDecimal currentValue = ZERO;

	@Column(name = "fldPastDue")
	private BigDecimal pastDueValue = ZERO;

	@Column(name = "fldAsOf")
	private String asOfDate;
}
