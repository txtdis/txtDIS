package ph.txtdis.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "sys_eod")
public class EdmsEndOfDay {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "system_date")
	private LocalDate edmsDate;

	@Column(name = "process_by")
	private String createdBy;

	@Column(name = "computer_date")
	private LocalDate creationDate;

	@Column(name = "time_trans")
	private String creationTime;
}
