package ph.txtdis.mgdc.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractCreatedKeyedEntity;
import ph.txtdis.domain.TruckEntity;
import ph.txtdis.domain.UserEntity;
import ph.txtdis.mgdc.printer.Printed;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractPickListEntity //
	extends AbstractCreatedKeyedEntity<Long> //
	implements Printed {

	private static final long serialVersionUID = -439128315706234672L;

	@ManyToOne
	private TruckEntity truck;

	@ManyToOne
	@JoinColumn(name = "driver")
	private UserEntity driver;

	@ManyToOne
	@JoinColumn(name = "lead_helper")
	private UserEntity assistant;

	@ManyToOne
	@JoinColumn(name = "asst_helper")
	private UserEntity leadAssistant;

	@Column(name = "pick_date", nullable = false)
	private LocalDate pickDate;

	private String remarks;

	@Column(name = "printed_by")
	private String printedBy;

	@Column(name = "printed_on")
	private ZonedDateTime printedOn;
}
