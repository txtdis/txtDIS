package ph.txtdis.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.Billable;

public interface ExTruckService extends SpunBillableService {

	List<Billable> findBookedExTrucks(Date d);

	Billable findLoadOrder(LocalDate date, String exTruck);

	Billable findOpenLoadOrder(String s, Date d);
}
