package ph.txtdis.domain;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import lombok.Data;
import ph.txtdis.dto.Keyed;

@Data
@MappedSuperclass
public abstract class EdmsAbstractId //
	implements Keyed<Long> {

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
}
