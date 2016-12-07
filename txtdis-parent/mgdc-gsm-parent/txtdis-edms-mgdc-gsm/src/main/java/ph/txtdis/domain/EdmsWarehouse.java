package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "ref_warehouse")
@EqualsAndHashCode(callSuper = true)
public class EdmsWarehouse extends EdmsAbstractMaster implements Serializable {

	private static final long serialVersionUID = -7777879110049518667L;

	@Column(name = "whLocation")
	private String location;

	@Column(name = "active")
	private boolean isActive;

	@Column(name = "mainWh")
	private boolean isMain;
}
