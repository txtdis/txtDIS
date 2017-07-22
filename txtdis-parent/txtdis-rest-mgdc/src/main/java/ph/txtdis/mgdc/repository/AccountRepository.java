package ph.txtdis.mgdc.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.domain.AccountEntity;

@Repository("accountRepository")
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
}
