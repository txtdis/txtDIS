package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_stock_out_d")
@EqualsAndHashCode(callSuper = true)
public class EdmsTransferOrderDetail
	extends EdmsAbstractItemNameUomQtyReferenceItemCodeIdDetail
	implements Serializable {

	private static final long serialVersionUID = 6273175158132128251L;
}
