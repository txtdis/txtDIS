package ph.txtdis.mgdc.gsm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractNamedCreatedKeyedEntity;
import ph.txtdis.dto.Named;
import ph.txtdis.type.BillingType;

@Data
@Entity
@Table(name = "channel", //
		indexes = { @Index(columnList = "is_visited") })
@EqualsAndHashCode(callSuper = true)
public class ChannelEntity //
		extends AbstractNamedCreatedKeyedEntity<Long> //
		implements Named {

	private static final long serialVersionUID = -8012814058038917889L;

	@Column(name = "type")
	private BillingType billingType;

	@Column(name = "is_visited")
	private boolean visited;

	@Override
	public String toString() {
		return super.toString();
	}
}
