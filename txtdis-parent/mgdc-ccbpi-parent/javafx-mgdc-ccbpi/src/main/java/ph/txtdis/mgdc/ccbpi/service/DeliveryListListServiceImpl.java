package ph.txtdis.mgdc.ccbpi.service;

import org.springframework.stereotype.Service;

@Service("deliveryListListService")
public class DeliveryListListServiceImpl //
	extends AbstractListService //
	implements DeliveryListListService {

	@Override
	public String getHeaderName() {
		return "DDL";
	}

	@Override
	public void listDDL(String[] ids) throws Exception {
		item = item(itemVendorNo(ids));
		list = getList(
			"/ddlList?itemVendorNo=" + itemVendorNo(ids) + "&route=" + route(ids) + "&start=" + startDate(ids) + "&end=" +
				endDate(ids));
	}
}
