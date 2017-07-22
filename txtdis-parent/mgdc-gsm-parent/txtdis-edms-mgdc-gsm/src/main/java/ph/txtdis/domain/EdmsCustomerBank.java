package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "master_customerlist_bank")
@EqualsAndHashCode(callSuper = true)
public class EdmsCustomerBank //
		extends EdmsAbstractId //
		implements Serializable {

	private static final long serialVersionUID = -7925283571731892086L;

	@Column(name = "xCode")
	private String customerCode;

	@Column(name = "bank")
	private String bankName = "";

	@Column(name = "accNo")
	private String accountNo = "";

	@Column(name = "accName")
	private String accountName = "";
}
