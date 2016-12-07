package ph.txtdis.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ph.txtdis.dto.Script;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.ScriptType;

@Scope("prototype")
@Service("scriptService")
public class ScriptServiceImpl implements ScriptService {

	@Autowired
	private ReadOnlyService<Script> readOnlyService;

	@Autowired
	private SavingService<List<Script>> savingService;

	@Autowired
	private RestServerService serverService;

	private List<Script> scripts;

	public ScriptServiceImpl() {
		scripts = new ArrayList<>();
	}

	@Override
	public void saveScripts()
			throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException, InvalidException {
		if (serverService.isOffSite() && !scripts.isEmpty()) {
			savingService.module("script").save(scripts);
			scripts = new ArrayList<>();
		}
	}

	@Override
	public void set(ScriptType scriptType, String script) {
		if (serverService.isOffSite())
			scripts.add(new Script(scriptType, script));
	}

	@Override
	public boolean unpostedTransactionsExist() {
		try {
			Script s = readOnlyService.module("script").getOne("/unposted");
			return s != null;
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
	}
}
