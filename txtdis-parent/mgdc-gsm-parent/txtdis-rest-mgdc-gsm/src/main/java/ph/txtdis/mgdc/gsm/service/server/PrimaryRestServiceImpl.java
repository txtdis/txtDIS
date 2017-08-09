package ph.txtdis.mgdc.gsm.service.server;

import org.springframework.stereotype.Service;
import ph.txtdis.service.AbstractRestService;

@Service("primaryRestService")
public class PrimaryRestServiceImpl //
	extends AbstractRestService<PrimaryRestServerService> //
	implements PrimaryRestService {

	public PrimaryRestServiceImpl(PrimaryRestServerService serverService) {
		super(serverService);
	}
}
