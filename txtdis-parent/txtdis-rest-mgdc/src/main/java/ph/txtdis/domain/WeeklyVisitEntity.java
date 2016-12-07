package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "weekly_visit", //
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "week_no" }))
public class WeeklyVisitEntity extends AbstractEntityId<Long> implements Comparable<WeeklyVisitEntity> {

	private static final long serialVersionUID = -6036484815394531712L;

	@Column(name = "week_no", nullable = false)
	private Integer weekNo;

	private Boolean sun, mon, tue, wed, thu, fri, sat;

	@Override
	public int compareTo(WeeklyVisitEntity wv) {
		return getWeekNo().compareTo(wv.getWeekNo());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (fri ? 1231 : 1237);
		result = prime * result + (mon ? 1231 : 1237);
		result = prime * result + (sat ? 1231 : 1237);
		result = prime * result + (sun ? 1231 : 1237);
		result = prime * result + (thu ? 1231 : 1237);
		result = prime * result + (tue ? 1231 : 1237);
		result = prime * result + (wed ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		WeeklyVisitEntity wv = (WeeklyVisitEntity) obj;
		if (this.getFri() != wv.getFri())
			return false;
		if (this.getMon() != wv.getMon())
			return false;
		if (this.getSat() != wv.getSat())
			return false;
		if (this.getSun() != wv.getSun())
			return false;
		if (this.getThu() != wv.getThu())
			return false;
		if (this.getTue() != wv.getTue())
			return false;
		if (this.getWed() != wv.getWed())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "weekNo=" + weekNo + ", sun=" + sun + ", mon=" + mon + ", tue=" + tue + ", wed=" + wed + ", thu=" + thu
				+ ", fri=" + fri + ", sat=" + sat;
	}
}