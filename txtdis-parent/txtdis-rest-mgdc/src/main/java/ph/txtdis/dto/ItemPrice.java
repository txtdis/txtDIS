package ph.txtdis.dto;

import java.math.BigDecimal;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPrice {

    private long id;

    private String name;

    private String description;

    private BigDecimal value;

    public ItemPrice(long id, String name, String description, BigDecimal none, BigDecimal one) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.value = one == null ? none : none;
    }

    public BigDecimal getValue() {
        return value == null ? BigDecimal.ZERO : value;
    }
}
