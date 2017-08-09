package ph.txtdis.service;

import java.util.List;

public interface RestClientService<T> {

	List<T> getList() throws Exception;

	List<T> getList(String string) throws Exception;

	T getOne(String endpoint) throws Exception;

	RestClientService<T> module(String module);

	T save(T entity) throws Exception;
}