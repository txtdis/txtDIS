package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_si_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsInvoiceDetail extends EdmsAbstractBillingDetail implements EdmsDetailBased, Serializable {

	private static final long serialVersionUID = 7256171435796668566L;
}
