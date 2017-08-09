package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "tr_logp_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsLoadOrderDetail
	extends EdmsAbstractVolumeDiscountTotalTransactionCodeItemNameCostPriceUomQtyReferenceItemCodeIdDetail
	implements Serializable {

	private static final long serialVersionUID = -5481002788965164842L;
}
