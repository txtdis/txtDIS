package ph.txtdis.domain;

import javax.persistence.Entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Bank extends Named<Long> {

	private static final long serialVersionUID = 8106427600120401310L;
}
