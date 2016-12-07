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
@Table(name = "tr_dr_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsDelivery extends EdmsAbstractReferencedBookedPaidTransported implements Keyed<Long>, Serializable {

	private static final long serialVersionUID = -433291482882125071L;

	@Column(name = "siRefNo")
	private String billingNo;
}
