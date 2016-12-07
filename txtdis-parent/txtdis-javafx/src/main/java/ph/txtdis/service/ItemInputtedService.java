package ph.txtdis.service;

public interface ItemInputtedService<T> {

	T createDetail();

	String getItemName();

	void setItemUponValidation(long id) throws Exception;
}
