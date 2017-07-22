package ph.txtdis.mgdc.gsm.service;

public interface ChangeableSalesOrderDetailsService {

	boolean canChangeDetails();

	boolean detailsChanged();

	void resetDetailsAndUpdateDiscountsAndPrices();
}
