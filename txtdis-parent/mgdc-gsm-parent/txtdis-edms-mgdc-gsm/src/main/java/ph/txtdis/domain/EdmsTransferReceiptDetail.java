package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_stock_in_d")
@EqualsAndHashCode(callSuper = true)
public class EdmsTransferReceiptDetail
	extends EdmsAbstractItemNameUomQtyReferenceItemCodeIdDetail
	implements Serializable {

	private static final long serialVersionUID = -9031098945148001917L;
}
