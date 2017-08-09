package ph.txtdis.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "style")
@EqualsAndHashCode(callSuper = true)
public class StyleEntity //
	extends AbstractKeyedEntity<Long> {

	private static final long serialVersionUID = -4479575580426911105L;

	@Column(nullable = false)
	private String base;

	@Column(nullable = false)
	private String font;

	@Override
	public String toString() {
		return "color= " + getBase() + " & font= " + getFont();
	}
}
