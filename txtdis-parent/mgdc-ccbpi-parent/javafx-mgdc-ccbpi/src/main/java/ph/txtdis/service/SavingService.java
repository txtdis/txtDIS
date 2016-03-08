package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.List;

import org.apache.log4j.Logger;
import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.HttpHeader;
import ph.txtdis.util.Server;

@Scope("prototype")
@Service("savingService")
public class SavingService<T> {

	private static Logger logger = getLogger(ImportService.class);

	@Autowired
	private HttpHeader http;

	@Autowired
	private RestService restService;

	@Autowired
	private Server server;

	private String module;

	@SuppressWarnings("unchecked")
	public T save(T entity) throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, RestException {
		try {
			return entity == null ? null
					: (T) restService.init().postForObject(url(entity), httpEntity(entity), entity.getClass());
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new NoServerConnectionException(server.location());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getStatusCode() == UNAUTHORIZED) {
				if (e.getResponseBodyAsString().contains("This connection has been closed"))
					throw new StoppedServerException();
				else
					throw new FailedAuthenticationException();
			} else
				throw new RestException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InvalidException(e.getMessage());
		}
	}

	private HttpEntity<T> httpEntity(T entity) {
		return new HttpEntity<T>(entity, http.headers());
	}

	private String plural() {
		return English.plural(module);
	}

	private String url(T t) {
		String url = "https://" + server.address() + ":" + server.getPort() + "/" + plural();
		if (t instanceof List<?>)
			url += "/all";
		logger.info("URL = " + url);
		return url;
	}

	protected SavingService<T> module(String module) {
		this.module = module;
		return this;
	}
}
