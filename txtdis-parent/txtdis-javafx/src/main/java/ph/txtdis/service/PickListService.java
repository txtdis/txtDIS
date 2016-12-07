package ph.txtdis.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Booking;

public interface PickListService extends NextWorkDayDated, Reset, Serviced<Long> {

	List<Booking> getBookings();

	String getHelper();

	LocalDate getPickDate();

	String getPrintedBy();

	ZonedDateTime getPrintedOn();

	String getRemarks();

	List<String> listDrivers();

	List<String> listHelpers();

	List<String> listRoutes();

	List<String> listTrucks();

	List<Booking> listUnpickedBookings(String route);

	void print() throws Exception;

	void setBookings(List<Booking> bookings);

	void setDriverUponValidation(String driver) throws Exception;

	void setHelperUponValidation(String helper) throws Exception;

	void setPickDateUponValidation(LocalDate pickDate) throws Exception;

	void setRemarks(String remarks);

	void setTruckUponValidation(String truck) throws Exception;

	void unpick(Booking booking);
}
