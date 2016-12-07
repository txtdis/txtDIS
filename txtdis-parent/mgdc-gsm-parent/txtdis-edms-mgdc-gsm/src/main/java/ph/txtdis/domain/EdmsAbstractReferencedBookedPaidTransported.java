package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractReferencedBookedPaidTransported extends EdmsAbstractBookedPaidTransported {

	@Column(name = "soRefNo")
	private String bookingNo;

	@Column(name = "dueDate")
	private LocalDate dueDate;
}
