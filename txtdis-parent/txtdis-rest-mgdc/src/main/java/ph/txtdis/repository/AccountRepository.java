package ph.txtdis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.AccountEntity;

@Repository("accountRepository")
public interface AccountRepository extends CrudRepository<AccountEntity, Long> {
}
