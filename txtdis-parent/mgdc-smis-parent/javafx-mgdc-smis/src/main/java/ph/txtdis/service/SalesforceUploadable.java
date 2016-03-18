package ph.txtdis.service;

import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.SalesforceEntity;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotFoundException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

public interface SalesforceUploadable<T extends SalesforceEntity> {

	String getUploadedBy();

	ZonedDateTime getUploadedOn();

	void upload() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException, NotFoundException;

	void saveUploadedData(List<? extends SalesforceEntity> list) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException;

	List<T> forUpload() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException;
}
