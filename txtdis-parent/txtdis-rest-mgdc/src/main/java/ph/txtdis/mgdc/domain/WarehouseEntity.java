package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.AbstractNamedCreatedKeyedEntity;
import ph.txtdis.dto.Named;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "warehouse", //
	indexes = {@Index(columnList = "name")})
public class WarehouseEntity //
	extends AbstractNamedCreatedKeyedEntity<Long> //
	implements Named {

	private static final long serialVersionUID = -902162181604587549L;

	@ManyToOne
	private ItemFamilyEntity family;

	@Override
	public String toString() {
		return getName();
	}
}
