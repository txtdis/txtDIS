package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { @Index(columnList = "name") })
public class Warehouse extends Named<Long> {

	private static final long serialVersionUID = -902162181604587549L;

	@ManyToOne
	private ItemFamily family;
}
