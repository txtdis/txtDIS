package ph.txtdis.service;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.util.List;

import org.atteo.evo.inflector.English;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.util.HttpHeader;
import ph.txtdis.util.TypeMap;

public abstract class AbstractReadOnlyService<T, H extends HttpHeader, RS extends RestService, RSS extends RestServerService> //
		implements ReadOnlyService<T> {

	@Autowired
	private H http;

	@Autowired
	private RS restService;

	@Autowired
	private RSS serverService;

	@Autowired
	private TypeMap response;

	private String module;

	@Override
	public List<T> getList() throws Exception {
		return getList("");
	}

	@Override
	@SuppressWarnings("unchecked")
	public T getOne(String endpoint) throws Exception {
		return (T) responseEntity(endpoint, single()).getBody();
	}

	@Override
	public AbstractReadOnlyService<T, H, RS, RSS> module(String module) {
		this.module = module;
		return this;
	}

	private HttpEntity<T> httpEntity(T entity) {
		return new HttpEntity<>(entity, http.headers());
	}

	private String plural() {
		return English.plural(single());
	}

	private ResponseEntity<?> responseEntity(String endpoint, String path) throws Exception {
		try {
			return restService.init().exchange(url() + endpoint, GET, httpEntity(null), response.type(path));
		} catch (ResourceAccessException e) {
			e.printStackTrace();
			throw new NoServerConnectionException(serverService.getLocation());
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			if (e.getStatusCode() == UNAUTHORIZED) {
				if (errorMsg(e).contains("This connection has been closed"))
					throw new StoppedServerException();
				throw new FailedAuthenticationException();
			}
			e.printStackTrace();
			throw new Exception(errorMsg(e));
		}
	}

	private String url() {
		return "https://" + serverService.address() + ":" + serverService.getPort() + "/" + plural();
	}

	private String errorMsg(HttpStatusCodeException e) {
		String msg = e.getResponseBodyAsString();
		return substringBetween(msg, "message\":\"", "\",\"path");
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getList(String endpoint) throws Exception {
		return (List<T>) responseEntity(endpoint, plural()).getBody();
	}

	protected String single() {
		return module;
	}
}
