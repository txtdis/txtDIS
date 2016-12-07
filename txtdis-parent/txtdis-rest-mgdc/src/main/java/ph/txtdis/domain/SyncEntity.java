package ph.txtdis.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.txtdis.dto.Keyed;
import ph.txtdis.type.SyncType;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sync")
public class SyncEntity implements Serializable, Keyed<SyncType> {

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
}
