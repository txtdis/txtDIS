package ph.txtdis.domain;

import static javax.persistence.CascadeType.ALL;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.CascadeType;
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
@Table(name = "picking", //
		indexes = { //
				@Index(columnList = "pick_date"), //
				@Index(columnList = "printed_on"), //
				@Index(columnList = "id, received_on") })
public class PickListEntity extends AbstractCreatedEntity<Long> implements Printed {

	private static final long serialVersionUID = -3835242947594550479L;

	@ManyToOne
	private TruckEntity truck;

	@ManyToOne
	@JoinColumn(name = "driver")
	private UserEntity driver;

	@ManyToOne
	@JoinColumn(name = "lead_helper")
	private UserEntity leadHelper;

	@ManyToOne
	@JoinColumn(name = "asst_helper")
	private UserEntity asstHelper;

	@Column(name = "pick_date", nullable = false)
	private LocalDate pickDate;

	private String remarks;

	@OneToMany(cascade = ALL)
	@JoinColumn(name = "picking_id")
	private List<PickListDetailEntity> details;

	@OneToMany(mappedBy = "picking", cascade = CascadeType.ALL)
	private List<BillableEntity> billings;

	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "printed_on")
	private ZonedDateTime printedOn;

	@Column(name = "received_by")
	private String receivedBy;

	@Column(name = "received_on")
	private ZonedDateTime receivedOn;
}
