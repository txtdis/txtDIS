package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class AgingReceivableReport {

	private List<AgingReceivable> receivables;

	private List<BigDecimal> totals;

	private ZonedDateTime timestamp;
}
