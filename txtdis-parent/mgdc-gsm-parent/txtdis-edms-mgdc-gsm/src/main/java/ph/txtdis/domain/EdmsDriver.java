package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_driver")
@EqualsAndHashCode(callSuper = true)
public class EdmsDriver
	extends EdmsAbstractContactInfoMaster
	implements Serializable {

	private static final long serialVersionUID = -1552963291096470053L;
}
