package ph.txtdis.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.dto.StockTakeDetail;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTake extends AbstractCreationTracked<Long> {

	private String warehouse, checker, taker, remarks;

	private LocalDate countDate;

	private List<StockTakeDetail> details;
}
