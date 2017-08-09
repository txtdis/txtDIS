package ph.txtdis.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ph.txtdis.mgdc.gsm.dto.Item;

import java.math.BigDecimal;

@Getter
@Component
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class VolumeSummary {

	private Item item;

	private BigDecimal qty = BigDecimal.ZERO;

	public long getItemId() {
		return item == null ? 0 : item.getId();
	}

	public BigDecimal getVol() {
		return getQty();
	}
}
