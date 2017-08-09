package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_area")
@EqualsAndHashCode(callSuper = true)
public class EdmsArea
	extends EdmsAbstractMaster
	implements Serializable {

	private static final long serialVersionUID = -7996895739631530347L;
}
