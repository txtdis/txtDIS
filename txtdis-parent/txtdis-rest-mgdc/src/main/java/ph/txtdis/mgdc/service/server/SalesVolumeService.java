package ph.txtdis.mgdc.service.server;

import java.time.LocalDate;
import java.util.List;

import ph.txtdis.dto.SalesVolume;

public interface SalesVolumeService {

	List<SalesVolume> list(LocalDate startDate, LocalDate endDate) throws Exception;
}