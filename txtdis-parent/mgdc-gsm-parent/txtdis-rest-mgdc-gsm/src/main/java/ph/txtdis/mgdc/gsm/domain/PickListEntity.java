package ph.txtdis.mgdc.gsm.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.mgdc.domain.AbstractPickListEntity;
import ph.txtdis.mgdc.printer.Printed;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "picking", //
	indexes = { //
		@Index(columnList = "pick_date, asst_helper"), //
		@Index(columnList = "pick_date, truck_id"), //
		@Index(columnList = "printed_on")})
public class PickListEntity //
	extends AbstractPickListEntity //
	implements DetailedEntity,
	Printed {

	private static final long serialVersionUID = -3835242947594550479L;

	@OneToMany(mappedBy = "picking", cascade = ALL)
	private List<BillableEntity> billings;

	@OneToMany(mappedBy = "picking", cascade = ALL)
	private List<PickListDetailEntity> details;

	@Override
	public String toString() {
		return "P/L No. " + getId() + ": " + getTruck() + " on " + toDateDisplay(getPickDate()) + "\n";
	}
}
