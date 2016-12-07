package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_bank")
@EqualsAndHashCode(callSuper = true)
public class EdmsBank extends EdmsAbstractMaster implements Serializable {

	private static final long serialVersionUID = 7374560525512425754L;
}
