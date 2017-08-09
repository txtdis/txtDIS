package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_purchase_h")
@EqualsAndHashCode(callSuper = true)
public class EdmsPurchaseReceipt //
	extends EdmsAbstractModifiedNotedWarehousedDatedRemarkedReferencedStatusCreatedId //
	implements Serializable {

	private static final long serialVersionUID = -1148761289889352509L;
}
