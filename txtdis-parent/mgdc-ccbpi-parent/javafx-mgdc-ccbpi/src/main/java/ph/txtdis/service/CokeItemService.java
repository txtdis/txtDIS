package ph.txtdis.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CokeItemService extends EmptiesItemService, LatestApproved, VendorIdBasedService {

	BigDecimal getLatestPrice(Long id, LocalDate orderDate);
}
