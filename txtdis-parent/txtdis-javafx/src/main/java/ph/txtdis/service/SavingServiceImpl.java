package ph.txtdis.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ph.txtdis.util.HttpHeader;

@Scope("prototype")
@Service("savingService")
public class SavingServiceImpl<T> extends AbstractSavingService<T, HttpHeader, RestService, RestServerService> {
}
