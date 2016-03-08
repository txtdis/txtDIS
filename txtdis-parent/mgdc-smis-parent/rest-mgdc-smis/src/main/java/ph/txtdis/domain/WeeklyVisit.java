package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "weekly_visit", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "week_no" }) )
public class WeeklyVisit extends AbstractId<Long> implements Comparable<WeeklyVisit> {

	private static final long serialVersionUID = -6036484815394531712L;

	@Column(name = "week_no", nullable = false)
	private Integer weekNo;

	private Boolean sun, mon, tue, wed, thu, fri, sat;

	@Override
	public int compareTo(WeeklyVisit wv) {
		return getWeekNo().compareTo(wv.getWeekNo());
	}
}
