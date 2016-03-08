package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "sms_log", uniqueConstraints = @UniqueConstraint(columnNames = { "customer_id", "time_stamp" }) )
public class SmsLog extends AbstractId<Long> {

	private static final long serialVersionUID = -8780823487171491421L;
	
	@Column(name = "time_stamp", nullable = false)
	private ZonedDateTime timeStamp;

	@ManyToOne(optional = false)
	private Customer customer;

	@Column(nullable = false)
	private String message;
}
