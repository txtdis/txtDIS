package ph.txtdis.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTake //
	extends AbstractCreationTracked<Long> {

	private String warehouse, checker, taker, remarks;

	private LocalDate countDate;

	private List<StockTakeDetail> details;
}
