package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "master_customerlist_payment")
public class EdmsCreditDetail extends EdmsAbstractId implements Serializable {

	private static final long serialVersionUID = -3969533960824154885L;

	@Column(name = "custNo")
	private String customerCode;

	@Column(name = "payment")
	private String payment;

	@Column(name = "terms")
	private String paymentTermCode;

	@Column(name = "creditLimit")
	private BigDecimal creditLimit;

	@Column(name = "disc")
	private Byte discountGiven;

	@Column(name = "discPHP")
	private BigDecimal discountValue;

	@Column(name = "discPer")
	private BigDecimal percentDiscount;

	@Column(name = "discCash")
	private BigDecimal cashDiscount;

	@Column(name = "discBase")
	private BigDecimal baseDiscount;

	@Column(name = "discOthers")
	private BigDecimal otherDiscount;

	@Column(name = "chk")
	private Byte chequeAllowed;
}
