package ph.txtdis.mgdc.gsm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ph.txtdis.dto.AbstractCreationTracked;
import ph.txtdis.type.QualityType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class StockTakeAdjustment //
	extends AbstractCreationTracked<Long> {

	private LocalDate stockTakeDate;

	private Item item;

	private QualityType quality;

	private BigDecimal qty;

	private String justification;
}
