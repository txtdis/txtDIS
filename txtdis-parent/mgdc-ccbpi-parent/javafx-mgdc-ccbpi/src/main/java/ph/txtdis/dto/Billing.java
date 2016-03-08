package ph.txtdis.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Billing extends EntityCreationTracked<Long> implements Keyed<Long> {

	private BigDecimal unpaidValue, totalQty, totalValue;

	private Customer customer;

	private List<ItemList> details;

	private LocalDate orderDate;

	private Long receivingId;

	private String collector, remarks, printedBy, receivedBy;

	private ZonedDateTime printedOn, receivedOn;
}
