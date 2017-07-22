package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.EdmsCustomerBank;

@Repository("edmsCustomerBankRepository")
public interface EdmsCustomerBankRepository //
		extends CrudRepository<EdmsCustomerBank, Long> {
}
