package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_outlet_type")
@EqualsAndHashCode(callSuper = true)
public class EdmsOutletType //
	extends EdmsAbstractMaster //
	implements Serializable {

	private static final long serialVersionUID = 3327211522987285785L;
}
