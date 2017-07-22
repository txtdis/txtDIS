package ph.txtdis.service;

public interface SavingService<T> {

	T save(T entity) throws Exception;

	SavingService<T> module(String module);
}