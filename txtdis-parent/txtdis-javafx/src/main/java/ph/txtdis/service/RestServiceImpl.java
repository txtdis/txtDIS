package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("restService")
public class RestServiceImpl //
	extends AbstractRestService<RestServerService> {

	public RestServiceImpl(RestServerService serverService) {
		super(serverService);
	}
}
