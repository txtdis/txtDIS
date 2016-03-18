package ph.txtdis.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyVisit extends AbstractId<Long> {

	private int weekNo;

	private boolean sun, mon, tue, wed, thu, fri, sat;

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
		WeeklyVisit other = (WeeklyVisit) obj;
		if (this.isFri() != other.isFri())
			return false;
		if (this.isMon() != other.isMon())
			return false;
		if (this.isSat() != other.isSat())
			return false;
		if (this.isSun() != other.isSun())
			return false;
		if (this.isThu() != other.isThu())
			return false;
		if (this.isTue() != other.isTue())
			return false;
		if (this.isWed()  != other.isWed())
			return false;
		return true;
	}
}