package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.BillingType;

@Data
@EqualsAndHashCode(callSuper = true)
public class Channel extends AbstractCreationTracked<Long> implements Named {

	private BillingType billingType;

	private String name;

	private boolean visited;

	@Override
	public String toString() {
		return name;
	}
}
