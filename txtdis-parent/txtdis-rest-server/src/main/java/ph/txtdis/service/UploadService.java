package ph.txtdis.service;

import ph.txtdis.exception.FailedReplicationException;

public interface UploadService {

	void upload(String type) throws FailedReplicationException;

}