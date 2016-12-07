package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_class")
@EqualsAndHashCode(callSuper = true)
public class EdmsItemClass extends EdmsAbstractMaster implements Serializable {

	private static final long serialVersionUID = 3226566849844623177L;
}
