package ph.txtdis.service;

public interface ReportService<T> //
	extends HeaderNameService,
	IconAndModuleNamedAndTypeMappedService,
	ResettableService,
	Spreadsheet<T>,
	StartEndService,
	SpunService {
}
