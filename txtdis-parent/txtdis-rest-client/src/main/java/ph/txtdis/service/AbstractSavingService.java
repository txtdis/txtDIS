package ph.txtdis.service;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.List;

import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.HttpHeader;

public class AbstractSavingService<T, H extends HttpHeader, RS extends RestService, RSS extends RestServerService> //
		implements SavingService<T> {

	@Autowired
	private H httpHeader;

	@Autowired
	private RS restService;

	@Autowired
	private RSS serverService;

	private String module;

	@Override
	@SuppressWarnings("unchecked")
	public T save(T entity) throws Exception {
		try {
			return entity == null ? null
					: (T) restService.init().postForObject( //
							url(entity), //
							httpEntity(entity), //
							entity.getClass());
		} catch (ResourceAccessException e) {
			throw new NoServerConnectionException(serverService.getLocation());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getStatusCode() == UNAUTHORIZED) {
				if (e.getResponseBodyAsString().contains("This connection has been closed"))
					throw new StoppedServerException();
				else
					throw new FailedAuthenticationException();
			}
			throw new Exception(e);
		}
	}

	private HttpEntity<T> httpEntity(T entity) {
		return new HttpEntity<>(entity, httpHeader.headers());
	}

	private String plural() {
		return English.plural(module);
	}

	private String url(T t) {
		String url = "https://" + serverService.address() + ":" + serverService.getPort() + "/" + plural();
		if (t instanceof List<?>)
			url += "/all";
		return url;
	}

	@Override
	public SavingService<T> module(String module) {
		this.module = module;
		return this;
	}
}
