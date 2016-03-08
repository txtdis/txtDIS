package ph.txtdis.dto;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ph.txtdis.domain.Item;

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
