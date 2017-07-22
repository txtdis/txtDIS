package ph.txtdis.domain;

import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toLocalDate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import ph.txtdis.dto.Keyed;
import ph.txtdis.type.SyncType;

@Data
@Entity
@Table(name = "sync")
public class SyncEntity //
		implements Serializable, Keyed<SyncType> {

	private static final long serialVersionUID = 4934707118618469477L;

	@Id
	private SyncType type;

	@Column(name = "last_sync")
	private Date lastSync;

	@Override
	public SyncType getId() {
		return type;
	}

	@Override
	public void setId(SyncType id) {
		type = id;
	}

	@Override
	public String toString() {
		return type + " synced " + toDateDisplay(toLocalDate(lastSync));
	}
}
