package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractBookedPaidTransported //
		extends EdmsAbstractPaidTransported {

	@Column(name = "deliveryDate")
	private LocalDate deliveryDate;

	@Column(name = "paymentTerms")
	private String paymentTermCode;
}
