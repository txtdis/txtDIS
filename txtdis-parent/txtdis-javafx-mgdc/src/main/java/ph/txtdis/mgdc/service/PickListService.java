package ph.txtdis.mgdc.service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import ph.txtdis.dto.Booking;

public interface PickListService //
		extends LoadingService {

	List<Booking> getBookings();

	String getPrintedBy();

	ZonedDateTime getPrintedOn();

	List<String> listDrivers();

	List<String> listHelpers();

	List<String> listRouteNames();

	List<String> listTrucks();

	List<Booking> listUnpickedBookings(String route);

	void print() throws Exception;

	void setBookings(List<Booking> bookings);

	void setDriverUponValidation(String driver) throws Exception;

	void setAssistantUponValidation(String helper) throws Exception;

	void setPickDateUponValidation(LocalDate pickDate) throws Exception;

	void setRemarks(String remarks);

	void setTruckUponValidation(String truck) throws Exception;

	void unpick(Booking booking);
}
