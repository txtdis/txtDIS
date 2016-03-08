package ph.txtdis.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.InvoiceBooklet;

@Repository("invoiceBookletRepository")
public interface InvoiceBookletRepository extends CrudRepository<InvoiceBooklet, Long> {

	List<InvoiceBooklet> findByOrderByPrefixAscStartIdAscSuffixAsc();

	InvoiceBooklet findByPrefixAndSuffixAndStartIdLessThanEqualAndEndIdGreaterThanEqual(//
			@Param("prefix") String p, //
			@Param("suffix") String s, //
			@Param("firstId") Long f, //
			@Param("lastId") Long l);//
}
