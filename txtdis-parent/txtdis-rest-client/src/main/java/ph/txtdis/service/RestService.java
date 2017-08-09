package ph.txtdis.service;

import org.springframework.web.client.RestOperations;

public interface RestService
	extends RestOperations {

	RestService init();
}