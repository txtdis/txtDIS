
package ph.txtdis.domain;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class ModificationTracked<PK> extends Named<PK> {

	private static final long serialVersionUID = 6409589135828964023L;

	@Column(name = "deactivated_by")
	protected String deactivatedBy;

	@Column(name = "deactivated_on")
	protected ZonedDateTime deactivatedOn;

	@LastModifiedBy
	@Column(name = "last_modified_by")
	protected String lastModifiedBy;

	@LastModifiedDate
	@Column(name = "last_modified_on")
	protected ZonedDateTime lastModifiedOn;
}