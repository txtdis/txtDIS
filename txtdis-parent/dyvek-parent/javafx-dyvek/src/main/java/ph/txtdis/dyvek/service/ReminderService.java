package ph.txtdis.dyvek.service;

import java.util.List;

import ph.txtdis.info.Information;

public interface ReminderService {

	void checkThingToDo() throws Information, Exception;

	List<String> getThingToDo();

	void ignore();

	void setThingsToDo();
}
