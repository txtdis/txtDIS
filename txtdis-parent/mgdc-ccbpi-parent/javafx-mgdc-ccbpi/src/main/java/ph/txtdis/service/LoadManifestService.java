package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.BillableDetail;

public interface LoadManifestService extends BillableService, TotaledTableService<BillableDetail> {

	Long getLoadManifestId();

	String getLoadManifestPrompt();

	String getRoutePrompt();

	List<String> listRoutes();

	void setRoute(String name);

	void updateUponReferenceIdValidation(Long id) throws Exception;
}
