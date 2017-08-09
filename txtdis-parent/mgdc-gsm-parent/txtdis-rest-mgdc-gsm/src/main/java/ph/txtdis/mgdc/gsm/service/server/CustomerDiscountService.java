package ph.txtdis.mgdc.gsm.service.server;

import ph.txtdis.mgdc.gsm.domain.BillableEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.gsm.domain.CustomerEntity;
import ph.txtdis.mgdc.gsm.dto.Customer;
import ph.txtdis.mgdc.gsm.dto.CustomerDiscount;
import ph.txtdis.mgdc.gsm.repository.CustomerDiscountRepository;
import ph.txtdis.service.DecisionDataUpdate;

import java.math.BigDecimal;
import java.util.List;

public interface CustomerDiscountService
	extends DecisionDataUpdate<CustomerDiscountEntity, CustomerDiscountRepository> {

	void cancelDiscountsOfOutletsWithAverageMonthlySalesBelowRequiredQty(BigDecimal noOfmonths,
	                                                                     BigDecimal requiredQty,
	                                                                     List<BillableEntity> billings);

	List<CustomerDiscountEntity> getNewAndOldCustomerDiscounts(CustomerEntity e, Customer c);

	boolean hasDecisionOnNewCustomerDiscountsBeenMade(CustomerEntity e, Customer c);

	List<CustomerDiscountEntity> toEntities(List<CustomerDiscount> l, CustomerEntity e);

	List<CustomerDiscount> toModels(List<CustomerDiscountEntity> l);

	List<CustomerDiscountEntity> updateCustomerDiscountDecisions(CustomerEntity e, Customer c);

	void updateDecisionData(String[] s);
}