package ph.txtdis.domain;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_cm_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsCreditMemo extends EdmsAbstractPaidTransported implements Serializable {

	private static final long serialVersionUID = 586806393911673808L;

	@Column(name = "cmDate")
	private LocalDate billedDate;

	@Column(name = "modiBy")
	private String modifiedBy;

	@Column(name = "dateModi")
	private String modifiedOn;

	@Column(name = "rsrPosted")
	private String postedRemittanceVariance;
}
