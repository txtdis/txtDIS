package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "customer", //
		indexes = { @Index(columnList = "mobile") })
public class CustomerImpl extends AbstractEntityId<Long> {

	private static final long serialVersionUID = 7959110197628528230L;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(nullable = false, unique = true)
	private String mobile;

	@ManyToOne
	@JoinColumn(name = "inventory_template_sent_log_id")
	private SmsLog inventoryTemplateSentLog;

	public CustomerImpl(String name, String mobile) {
		this.name = name;
		this.mobile = mobile;
	}
}
