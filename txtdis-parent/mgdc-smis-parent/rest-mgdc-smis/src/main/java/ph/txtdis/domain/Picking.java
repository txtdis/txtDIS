package ph.txtdis.domain;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.printer.Printed;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "pick_date"), //
		@Index(columnList = "printed_on") })
public class Picking extends CreationTracked<Long> implements Printed {

	private static final long serialVersionUID = -3835242947594550479L;

	@ManyToOne
	private Truck truck;

	@ManyToOne
	@JoinColumn(name = "driver")
	private User driver;

	@ManyToOne
	@JoinColumn(name = "lead_helper")
	private User leadHelper;

	@ManyToOne
	@JoinColumn(name = "asst_helper")
	private User asstHelper;

	@Column(name = "pick_date", nullable = false)
	private LocalDate pickDate;

	private String remarks;

	@OneToMany
	@JoinColumn(name = "picking_id")
	private List<Billing> billings;

	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "printed_on")
	private ZonedDateTime printedOn;
}
