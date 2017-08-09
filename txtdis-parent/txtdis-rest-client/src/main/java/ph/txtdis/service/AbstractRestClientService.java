package ph.txtdis.service;

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

import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public abstract class AbstractRestClientService<
	T,
	H extends HttpHeader,
	RS extends RestService,
	RSS extends RestServerService>
	implements RestClientService<T> {

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

	protected String single() {
		return module;
	}

	private String url() {
		return "https://" + serverService.getAddress() + ":" + serverService.getPort() + "/" + plural();
	}

	private HttpEntity<T> httpEntity(T entity) {
		return new HttpEntity<>(entity, http.headers());
	}

	private String errorMsg(HttpStatusCodeException e) {
		String msg = e.getResponseBodyAsString();
		return substringBetween(msg, "message\":\"", "\",\"path");
	}

	private String plural() {
		return English.plural(single());
	}

	@Override
	public AbstractRestClientService<T, H, RS, RSS> module(String module) {
		this.module = module;
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<T> getList(String endpoint) throws Exception {
		return (List<T>) responseEntity(endpoint, plural()).getBody();
	}

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

	private String url(T t) {
		String url = url();
		if (t instanceof List<?>)
			url += "/all";
		return url;
	}
}
