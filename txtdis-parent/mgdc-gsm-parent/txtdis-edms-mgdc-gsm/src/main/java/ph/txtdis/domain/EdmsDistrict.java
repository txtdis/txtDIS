package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_district")
@EqualsAndHashCode(callSuper = true)
public class EdmsDistrict
	extends EdmsAbstractMaster
	implements Serializable {

	private static final long serialVersionUID = 7071134876775769024L;
}
