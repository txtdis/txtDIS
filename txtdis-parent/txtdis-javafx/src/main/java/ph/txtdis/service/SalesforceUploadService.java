package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.SalesforceEntity;

public interface SalesforceUploadService<T extends SalesforceEntity> {

	String getUploadedBy();

	ZonedDateTime getUploadedOn();

	void upload() throws Exception;

	void saveUploadedData(List<? extends SalesforceEntity> list) throws Exception;

	List<T> forUpload() throws Exception;
}
