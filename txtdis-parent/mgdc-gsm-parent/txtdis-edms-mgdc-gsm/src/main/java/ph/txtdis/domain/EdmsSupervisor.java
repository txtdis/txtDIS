package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_dss")
@EqualsAndHashCode(callSuper = true)
public class EdmsSupervisor //
	extends EdmsAbstractMaster //
	implements Serializable {

	private static final long serialVersionUID = 348251657442213794L;
}
