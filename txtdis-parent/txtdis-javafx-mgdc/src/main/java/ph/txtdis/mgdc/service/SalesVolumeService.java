package ph.txtdis.mgdc.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesVolume;
import ph.txtdis.mgdc.type.SalesVolumeReportType;
import ph.txtdis.service.SavableAsExcelService;
import ph.txtdis.service.SpunService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

public interface SalesVolumeService //
		extends SavableAsExcelService<SalesVolume>, SpunService, TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService, VerifiedSalesOrderService {

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