package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.BillingType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Channel extends Named<Long> {

	private static final long serialVersionUID = -8012814058038917889L;

	private BillingType type;

	@Column(name = "is_visited")
	private boolean visited;
}
