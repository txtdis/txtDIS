package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "sms_id"), //
		@Index(columnList = "vendor_id") })
public class Item extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -3012020260825126952L;

	@Column(name = "sms_id", nullable = false, unique = true, length = 6)
	private String smsId;

	@Column(name = "vendor_id", nullable = false, unique = true, length = 10)
	private String vendorId;

	@Column(nullable = false, unique = true)
	private String name;

	@JoinColumn(name = "item_id")
	@OneToMany(fetch = EAGER, cascade = ALL)
	@OrderBy("start_date DESC")
	private List<Price> priceList;

	public Item(String smsId, String vendorId, String name) {
		this.smsId = smsId;
		this.vendorId = vendorId;
		this.name = name;
	}
}
