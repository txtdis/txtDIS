package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.domain.AbstractEntityId;
import ph.txtdis.type.ScriptType;

@Data
@Entity
@Table(name = "script")
@EqualsAndHashCode(callSuper = true)
public class ScriptEntity extends AbstractEntityId<Long> {

	private static final long serialVersionUID = -2797924638644401871L;

	private ScriptType type;

	private String script;

	private boolean sent;
}
