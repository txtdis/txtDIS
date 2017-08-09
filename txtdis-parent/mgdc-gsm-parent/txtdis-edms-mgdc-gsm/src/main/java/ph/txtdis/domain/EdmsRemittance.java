package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_payment")
@EqualsAndHashCode(callSuper = true)
public class EdmsRemittance
	extends EdmsAbstractId
	implements Serializable {

	private static final long serialVersionUID = -6829171934110917887L;

	@Column(name = "autoNo")
	private String autoNo;

	@Column(name = "xCode")
	private String billingNo;

	@Column(name = "refCode")
	private String referenceNo;

	@Column(name = "custCode")
	private String customerCode;

	@Column(name = "salesCode")
	private String sellerCode;

	@Column(name = "payment")
	private String paymentMode;

	@Column(name = "cash")
	private BigDecimal cashValue;

	@Column(name = "credit")
	private BigDecimal creditValue;

	@Column(name = "chkNo")
	private String chequeNo;

	@Column(name = "sysDate")
	private LocalDate systemDate;

	@Column(name = "date")
	private LocalDate paymentDate;

	@Column(name = "dueDate")
	private LocalDate maturityDate;

	@Column(name = "accName")
	private String bankAccountName;

	@Column(name = "bank")
	private String bankName;

	@Column(name = "accNo")
	private String bankAccountNo;

	@Column(name = "cleared")
	private String cleared;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "void")
	private short validity;

	@Column(name = "source")
	private String billingType;

	@Column(name = "createdBy")
	private String createdBy;

	@Column(name = "dateCreate")
	private String createdOn;

	@Column(name = "dateModi")
	private String modifiedOn;

	@Column(name = "rsrPosted")
	private String postedRemittanceVariance;
}
