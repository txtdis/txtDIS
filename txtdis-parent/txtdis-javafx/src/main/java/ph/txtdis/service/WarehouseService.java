package ph.txtdis.service;

import java.util.List;

import ph.txtdis.dto.ItemFamily;
import ph.txtdis.dto.Warehouse;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.NotAllowedOffSiteTransactionException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.info.SuccessfulSaveInfo;

public interface WarehouseService extends ItemFamilyLimited, Listed<Warehouse>, Titled, UniquelyNamed<Warehouse> {

	List<String> listNames();

	Warehouse save(String name, ItemFamily family) throws SuccessfulSaveInfo, NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, InvalidException, NotAllowedOffSiteTransactionException;

	boolean isOffSite();
}