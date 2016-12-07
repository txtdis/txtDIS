package ph.txtdis.dto;

public interface Keyed<PK> {

	PK getId();

	void setId(PK id);
}
