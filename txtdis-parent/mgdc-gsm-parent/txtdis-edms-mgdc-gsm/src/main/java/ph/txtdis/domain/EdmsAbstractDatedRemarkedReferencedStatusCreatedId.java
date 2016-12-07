package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractDatedRemarkedReferencedStatusCreatedId extends EdmsAbstractReferencedStatusCreatedId {

	@Column(name = "dateTrans")
	private LocalDate orderDate;

	@Column(name = "remarks")
	private String remarks;
}
