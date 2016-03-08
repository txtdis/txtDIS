package ph.txtdis.domain;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.type.ScriptType;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Script extends AbstractId<Long> {

	private static final long serialVersionUID = -2797924638644401871L;

	private ScriptType type;

	private String script;

	private boolean sent;
}
