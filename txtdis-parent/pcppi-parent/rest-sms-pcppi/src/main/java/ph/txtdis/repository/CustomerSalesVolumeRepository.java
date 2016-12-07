package ph.txtdis.repository;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.CustomerImpl;
import ph.txtdis.domain.CustomerSalesVolume;
import ph.txtdis.domain.Item;

@Repository("customerSalesVolumeRepository")
public interface CustomerSalesVolumeRepository extends SpunRepository<CustomerSalesVolume, Long> {

	CustomerSalesVolume findByCustomerAndItem(@Param("customer") CustomerImpl c, @Param("item") Item i);
}
