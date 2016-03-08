package ph.txtdis.repository;

import java.time.ZonedDateTime;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.domain.StockTaking;

@Repository("stockTakeRepository")
public interface StockTakeRepository extends SpunRepository<StockTaking, Long> {

	StockTaking findFirstByCreatedOnBetweenOrderByIdAsc(@Param("startOfDay") ZonedDateTime s,
			@Param("endOfDay") ZonedDateTime e);
}
