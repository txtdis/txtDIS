package ph.txtdis.service;

import java.util.stream.Stream;

import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.dto.Billable;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.Route;
import ph.txtdis.mgdc.gsm.dto.Customer;

public interface EdmsSellerService {

	EdmsSeller extractFrom(Billable b);

	EdmsSeller extractFrom(PickList p);

	EdmsSeller findByCode(String route);

	EdmsSeller findByTruckPlateNo(String truck);

	EdmsSeller findByUsername(String username);

	Stream<EdmsSeller> getAll();

	String getCode(Billable i);

	String getCode(Customer c);

	String getCode(PickList p);

	String getSurname(EdmsSeller s);

	String getUsername(EdmsSeller s);

	String getUsernameFromCode(String code);

	Route save(Route r);
}