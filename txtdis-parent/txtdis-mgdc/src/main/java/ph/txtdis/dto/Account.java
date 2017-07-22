package ph.txtdis.dto;

import java.time.LocalDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Account //
		extends AbstractCreationTracked<Long> {

	private String seller, sellerFullName;

	private LocalDate startDate;
}
