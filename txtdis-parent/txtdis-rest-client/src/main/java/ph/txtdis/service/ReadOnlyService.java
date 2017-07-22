package ph.txtdis.service;

import java.util.List;

public interface ReadOnlyService<T> {

	List<T> getList() throws Exception;

	List<T> getList(String string) throws Exception;

	T getOne(String endpoint) throws Exception;

	ReadOnlyService<T> module(String module);
}