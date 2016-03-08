package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
@EntityListeners(AuditingEntityListener.class)
public abstract class CreationTracked<PK> extends AbstractId<PK> {

	private static final long serialVersionUID = -5753015257974898395L;

	@LastModifiedBy
	@Column(name = "created_by", nullable = false)
	protected String createdBy;

	@LastModifiedDate
	@Column(name = "created_on", nullable = false)
	protected ZonedDateTime createdOn;
}