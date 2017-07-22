package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;

@Service("orderReturnListService")
public class OrderReturnListServiceImpl //
		extends AbstractListService //
		implements OrderReturnListService {

	@Override
	public String getHeaderName() {
		return "Order Return";
	}

	@Override
	public void listRR(String[] ids) throws Exception {
		item = item(itemVendorNo(ids));
		list = getList("/rrList?itemVendorNo=" + itemVendorNo(ids) + "&route=" + route(ids) + "&start=" + startDate(ids) + "&end=" + endDate(ids));
	}
}
