package ph.txtdis.service;

import static ph.txtdis.util.DateTimeUtils.toTimeDisplay;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.EdmsEndOfDay;
import ph.txtdis.repository.EdmsEndOfDayRepository;

@Service("endOfDayService")
public class EndOfDayServiceImpl //
		implements EndOfDayService {

	@Autowired
	private EdmsEndOfDayRepository repository;

	@Value("${client.user}")
	private String userName;

	@Override
	public void updateEndOfDay() {
		EdmsEndOfDay last = repository.findFirstByOrderByEdmsDateDesc();
		LocalDate dayAfterLast = dayAfter(last);
		if (!dayAfterLast.isAfter(yesterday()))
			addEndOfDaysBetweenYesterdayAnd(dayAfterLast);
	}

	private LocalDate dayAfter(EdmsEndOfDay last) {
		return last.getEdmsDate().plusDays(1L);
	}

	private LocalDate yesterday() {
		return LocalDate.now().minusDays(1L);
	}

	private void addEndOfDaysBetweenYesterdayAnd(LocalDate dayAfterLast) {
		List<EdmsEndOfDay> endOfDays = new ArrayList<>();
		do {
			endOfDays.add(newEdmsEndOfDay(dayAfterLast));
			dayAfterLast = dayAfterLast.plusDays(1L);
		} while (dayAfterLast.isBefore(LocalDate.now()));
		repository.save(endOfDays);
	}

	private EdmsEndOfDay newEdmsEndOfDay(LocalDate start) {
		EdmsEndOfDay e = new EdmsEndOfDay();
		e.setEdmsDate(start);
		e.setCreatedBy(userName);
		e.setCreationDate(LocalDate.now());
		e.setCreationTime(toTimeDisplay(LocalTime.now()));
		return e;
	}
}
