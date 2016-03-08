package ph.txtdis.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public class SalesforceAccount implements SalesforceEntity {

	private String distributor, //
			accountName, //
			accountNumber, //
			channel, //
			province, //
			city, //
			barangay, //
			area, //
			cluster, //
			taxClassification, //
			visitFrequency;
	
	private int paymentTerms;

	private ZonedDateTime uploadedOn;

	@Override
	public String getIdNo() {
		return accountNumber;
	}
}
