package ph.txtdis.mgdc.printer;

public interface ServerPrinter<T> {

	void print(T entity) throws Exception;
}