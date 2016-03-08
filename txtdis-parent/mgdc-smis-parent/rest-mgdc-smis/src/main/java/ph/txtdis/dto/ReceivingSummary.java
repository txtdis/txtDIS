package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.Item;
import ph.txtdis.type.QualityType;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Subselect("select d.id, r.order_date, d.item_id, d.quality_id, sum(d.qty * qpu.qty) qty from receiving r "
		+ "join receiving_detail d on r.id = d.receiving_id "
		+ "join qty_per_uom qpu on d.item_id = qpu.item_id and qpu.uom = d.uom "
		+ "group by d.id, r.order_date, d.item_id, d.quality_id ")
@Synchronize({ "receiving", "receiving_detail", "item", "quality", "qty_per_uom" })
public class ReceivingSummary {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Column(nullable = false)
	private LocalDate orderDate;

	@ManyToOne(optional = false, cascade = CascadeType.ALL)
	private Item item;

	private QualityType quality;

	@Column(nullable = false, precision = 10, scale = 4)
	private BigDecimal qty;

	public ReceivingSummary(Item item, QualityType quality, BigDecimal qty) {
		this.item = item;
		this.quality = quality;
		this.qty = qty;
	}
}
