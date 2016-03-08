package ph.txtdis.dto;

import java.time.ZonedDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SalesforceSalesInfo implements SalesforceEntity {

	private String account, //
			actualDate, //
			invoiceNumber, //
			dsp;
	
	private List<SalesforceSalesInfoProduct> products;
	
	private ZonedDateTime uploadedOn;

	@Override
	public String getIdNo() {
		return invoiceNumber;
	}
}
