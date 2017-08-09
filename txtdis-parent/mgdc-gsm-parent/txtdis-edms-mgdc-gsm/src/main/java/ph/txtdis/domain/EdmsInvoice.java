package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.Keyed;

@Data
@Entity
@Table(name = "tr_si_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsInvoice
	extends EdmsAbstractReferencedBookedPaidTransported
	implements Keyed<Long>,
	Serializable {

	private static final long serialVersionUID = 6337207545928852608L;

	@Column(name = "drRefNo")
	private String deliveryNo;

	@Column(name = "rsrPosted")
	private String postedRemittanceVariance;
}
