package ph.txtdis.repository;

import org.springframework.stereotype.Repository;

import ph.txtdis.domain.Bank;

@Repository("bankRepository")
public interface BankRepository extends NameListRepository<Bank> {
}
