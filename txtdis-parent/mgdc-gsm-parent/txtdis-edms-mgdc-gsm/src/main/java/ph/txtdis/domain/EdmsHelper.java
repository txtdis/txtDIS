package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_helper")
@EqualsAndHashCode(callSuper = true)
public class EdmsHelper //
		extends EdmsAbstractContactInfoMaster //
		implements Serializable {

	private static final long serialVersionUID = 4552669971695960534L;
}
