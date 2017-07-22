package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class EdmsAbstractRemittanceVarianceBasisModifiedNotedTransported extends EdmsAbstractTransported {

	@Column(name = "modiBy")
	private String modifiedBy = "";

	@Column(name = "dateModi")
	private String modifiedOn = "";

	@Column(name = "rsrPosted")
	private String postedRemittanceVariance;
}
