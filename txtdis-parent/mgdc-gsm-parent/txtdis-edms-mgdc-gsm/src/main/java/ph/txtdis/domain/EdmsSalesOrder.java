package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_so_header")
@EqualsAndHashCode(callSuper = true)
public class EdmsSalesOrder //
	extends EdmsAbstractBookedPaidTransported //
	implements Serializable {

	private static final long serialVersionUID = -7879043349941384153L;

	@Column(name = "modiBy")
	private String modifiedBy = "";

	@Column(name = "dateModi")
	private String modifiedOn = "";
}
