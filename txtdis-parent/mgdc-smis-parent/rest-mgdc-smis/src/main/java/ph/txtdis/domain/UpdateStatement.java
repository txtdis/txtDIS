package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "update_statement")
public class UpdateStatement extends AbstractId<Long> {

	private static final long serialVersionUID = -6809401018654446849L;

	@Column(name = "table_name", nullable = false)
	private String tableName;

	@Column(name = "column_name", nullable = false)
	private String columnName;

	@Column(name = "column_datum", nullable = false)
	private String columnDatum;

	@Column(nullable = false)
	private String user_name;

	@Column(name = "time_stamp", nullable = false)
	private ZonedDateTime timestamp;

	@Column(name = "table_id", nullable = false)
	private String tableId;

	@Column(name = "id_no", nullable = false)
	private Long idNo;
}
