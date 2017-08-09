package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_territory")
@EqualsAndHashCode(callSuper = true)
public class EdmsTerritory
	extends EdmsAbstractMaster
	implements Serializable {

	private static final long serialVersionUID = 1429834151589755522L;
}
