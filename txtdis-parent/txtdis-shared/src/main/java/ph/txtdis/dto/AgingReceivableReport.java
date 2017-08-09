package ph.txtdis.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class AgingReceivableReport {

	private List<AgingReceivable> receivables;

	private List<BigDecimal> totals;

	private ZonedDateTime timestamp;
}
