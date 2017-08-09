package ph.txtdis.mgdc.gsm.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.type.PartnerType;

import java.util.List;

@Repository("purchaseReceiptRepository")
public interface NoPurchaseOrderReceiptRepository //
	extends PurchaseReceiptRepository {

	List<BillableEntity> findByCustomerTypeOrderByIdAsc( //
	                                                     @Param("vendorType") PartnerType t);

	BillableEntity findByNumIdAndRmaNullAndCustomerType( //
	                                                     @Param("shipmentId") Long shipmentId, //
	                                                     @Param("vendorType") PartnerType t);
}
