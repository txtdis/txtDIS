package ph.txtdis.domain;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(indexes = { //
		@Index(columnList = "code"), //
		@Index(columnList = "name") })
public class Customer extends Named<Long> {

	private static final long serialVersionUID = 6853333430416794250L;
}
