package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "invoice_booklet",
		uniqueConstraints = @UniqueConstraint(columnNames = { "prefix", "start_id", "end_id", "suffix" }) ,
		indexes = { @Index(columnList = "prefix, start_id, end_id, suffix") })
public class InvoiceBooklet extends CreationTracked<Long> {

	private static final long serialVersionUID = 6045289585003677813L;

	private String prefix, suffix;

	@Column(name = "start_id")
	private long startId;

	@Column(name = "end_id")
	private long endId;

	@Column(name = "issued_to")
	private String issuedTo;

	@PrePersist
	public void nullIfEmpty() {
		prefix = prefix.isEmpty() ? null : prefix;
		suffix = suffix.isEmpty() ? null : suffix;
	}
}
