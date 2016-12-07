package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesVolume;
import ph.txtdis.type.SalesVolumeReportType;

public interface SalesVolumeService extends BilledAllPickedSalesOrder, Moduled, Excel<SalesVolume>, Spun {

	List<SalesVolume> dataDump();

	LocalDate getEndDate();

	LocalDate getStartDate();

	String getSubhead();

	String getTitleText();

	void setType(String t);

	void setType(SalesVolumeReportType t);

	void setEndDate(LocalDate d);

	void setStartDate(LocalDate d);

	List<SalesVolume> filterByCustomer(long id) throws Exception;
}