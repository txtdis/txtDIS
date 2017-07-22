package ph.txtdis.domain;

import static javax.persistence.GenerationType.IDENTITY;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import ph.txtdis.dto.Keyed;

@Data
@MappedSuperclass
public abstract class AbstractKeyedEntity<PK> //
		implements Keyed<PK>, Serializable {

	private static final long serialVersionUID = 6503580333612358526L;

	@Id
	@GeneratedValue(strategy = IDENTITY)
	private PK id;
}