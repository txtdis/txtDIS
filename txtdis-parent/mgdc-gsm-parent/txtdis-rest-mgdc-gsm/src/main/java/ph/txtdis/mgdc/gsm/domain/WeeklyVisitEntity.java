package ph.txtdis.mgdc.gsm.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractKeyedEntity;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "weekly_visit", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "week_no" }))
public class WeeklyVisitEntity //
		extends AbstractKeyedEntity<Long> //
		implements Comparable<WeeklyVisitEntity> {

	private static final long serialVersionUID = -6036484815394531712L;

	@Column(name = "week_no", nullable = false)
	private Integer weekNo;

	private Boolean sun, mon, tue, wed, thu, fri, sat;

	@Override
	public int compareTo(WeeklyVisitEntity wv) {
		return getWeekNo().compareTo(wv.getWeekNo());
	}

	@Override
	public String toString() {
		return "wk=" + weekNo + ", su=" + sun + ", m=" + mon + ", t=" + tue + ", w=" + wed + ", th=" + thu + ", f=" + fri + ", sa=" + sat;
	}
}