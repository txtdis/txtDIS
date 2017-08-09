package ph.txtdis.mgdc.service.server;

import ph.txtdis.dto.SalesVolume;

import java.time.LocalDate;
import java.util.List;

public interface SalesVolumeService {

	List<SalesVolume> list(LocalDate startDate, LocalDate endDate) throws Exception;
}