package ph.txtdis.service;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesVolume;
import ph.txtdis.exception.DateBeforeGoLiveException;
import ph.txtdis.exception.EndDateBeforeStartException;

public interface SalesVolumeService {

	List<SalesVolume> list(LocalDate startDate, LocalDate endDate)
			throws DateBeforeGoLiveException, EndDateBeforeStartException;
}