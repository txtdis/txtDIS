package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractTransported //
		extends EdmsAbstractNotedWarehousedDatedRemarkedReferencedStatusCreatedId {

	@Column(name = "salesman")
	private String sellerCode;

	@Column(name = "truckCode")
	private String truckCode;

	@Column(name = "truckPlateNo")
	private String plateNo;

	@Column(name = "driver")
	private String driverCode;

	@Column(name = "helper")
	private String helperCode;
}
