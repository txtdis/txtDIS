package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

public interface Totaled<T> {

	List<BigDecimal> getTotals(List<T> l);
}
