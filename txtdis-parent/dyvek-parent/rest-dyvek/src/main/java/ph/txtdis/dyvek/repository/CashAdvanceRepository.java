package ph.txtdis.dyvek.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.dyvek.domain.CashAdvanceEntity;
import ph.txtdis.type.PartnerType;

@Repository("cashAdvanceRepository")
public interface CashAdvanceRepository //
		extends CrudRepository<CashAdvanceEntity, Long> {

	CashAdvanceEntity findByBankNameAndCheckId( // 
			@Param("bank") String b, //
			@Param("checkId") Long c);

	List<CashAdvanceEntity> findByOrderByBalanceValueDescCheckDateDesc();

	List<CashAdvanceEntity> findByCustomerTypeAndCustomerNameAndBalanceValueGreaterThanOrderByCheckDateDesc( //
			@Param("type") PartnerType t, //
			@Param("customer") String c, //
			@Param("zeroBalance") BigDecimal b);
}
