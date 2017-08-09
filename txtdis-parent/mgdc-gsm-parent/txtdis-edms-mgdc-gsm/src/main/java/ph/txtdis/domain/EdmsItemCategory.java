package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_category")
@EqualsAndHashCode(callSuper = true)
public class EdmsItemCategory
	extends EdmsAbstractMaster
	implements Serializable {

	private static final long serialVersionUID = 961493564811905729L;
}
