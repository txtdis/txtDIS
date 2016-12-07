package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Account;
import ph.txtdis.dto.Route;
import ph.txtdis.info.Information;
import ph.txtdis.type.DeliveryType;

public interface RouteService extends Listed<Route>, Titled, UniquelyNamed<Route> {

	Route find(String id) throws Exception;

	Route find(String[] ids) throws Exception;

	List<Account> getSellerHistory();

	boolean isOffSite();

	List<String> listNames();

	List<String> listUsers() throws Exception;

	Route save(String name, DeliveryType type) throws Information, Exception;

	Account save(String seller, LocalDate date) throws Information, Exception;
}