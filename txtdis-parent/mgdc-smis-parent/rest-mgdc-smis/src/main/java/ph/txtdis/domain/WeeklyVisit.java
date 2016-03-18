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
		uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "week_no" }) )
public class WeeklyVisit extends AbstractId<Long> implements Comparable<WeeklyVisit> {

	private static final long serialVersionUID = -6036484815394531712L;

	@Column(name = "week_no", nullable = false)
	private Integer weekNo;

	private boolean sun, mon, tue, wed, thu, fri, sat;

	@Override
	public int compareTo(WeeklyVisit wv) {
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
		WeeklyVisit wv = (WeeklyVisit) obj;
		if (this.isFri() != wv.isFri())
			return false;
		if (this.isMon() != wv.isMon())
			return false;
		if (this.isSat() != wv.isSat())
			return false;
		if (this.isSun() != wv.isSun())
			return false;
		if (this.isThu() != wv.isThu())
			return false;
		if (this.isTue() != wv.isTue())
			return false;
		if (this.isWed()  != wv.isWed())
			return false;
		return true;
	}
}