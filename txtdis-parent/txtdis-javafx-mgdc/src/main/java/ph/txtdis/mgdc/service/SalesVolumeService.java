package ph.txtdis.mgdc.service;

import ph.txtdis.dto.SalesVolume;
import ph.txtdis.mgdc.type.SalesVolumeReportType;
import ph.txtdis.service.SavableAsExcelService;
import ph.txtdis.service.SpunService;
import ph.txtdis.service.TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService;

import java.time.LocalDate;
import java.util.List;

public interface SalesVolumeService //
	extends SavableAsExcelService<SalesVolume>,
	SpunService,
	TitleAndHeaderAndIconAndModuleNamedAndTypeMappedService,
	VerifiedSalesOrderService {

	List<SalesVolume> dataDump();

	LocalDate getEndDate();

	void setEndDate(LocalDate d);

	LocalDate getStartDate();

	void setStartDate(LocalDate d);

	String getSubhead();

	String getTitleText();

	void setType(String t);

	void setType(SalesVolumeReportType t);

	List<SalesVolume> filterByCustomer(long id) throws Exception;
}