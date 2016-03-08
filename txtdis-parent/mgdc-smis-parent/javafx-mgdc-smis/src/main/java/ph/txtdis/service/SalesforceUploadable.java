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

public interface SalesforceUploadable {

	String getUploadedBy();

	ZonedDateTime getUploadedOn();

	<T extends SalesforceEntity> T save(T t) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, RestException;

	void upload() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException, NotFoundException;

	void saveUploadedData(List<? extends SalesforceEntity> list) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException;

	List<? extends SalesforceEntity> forUpload() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException;
}
