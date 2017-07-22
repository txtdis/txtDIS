package ph.txtdis.service;

import java.math.BigDecimal;
import java.util.List;

import ph.txtdis.domain.EdmsInvoiceDetail;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsPurchaseReceiptDetail;
import ph.txtdis.domain.EdmsSalesOrderDetail;
import ph.txtdis.dto.BillableDetail;
import ph.txtdis.dto.ItemVendorNo;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.mgdc.gsm.dto.Item;

public interface EdmsItemService //
		extends SavedService<Item> {

	BigDecimal getBottlesPerCase(BillableDetail bd);

	BigDecimal getBottlesPerCase(EdmsInvoiceDetail id);

	BigDecimal getBottlesPerCase(EdmsPurchaseReceiptDetail pd);

	BigDecimal getBottlesPerCase(PickListDetail pd);

	EdmsItem getItem(ItemVendorNo d);

	BigDecimal getPricePerBottle(EdmsSalesOrderDetail d);

	BigDecimal getPricePerCase(EdmsSalesOrderDetail d);

	BigDecimal getPricePerCase(EdmsInvoiceDetail i);

	List<Item> list();

	List<String> listBrandNames(String category);

	List<String> listCategoryNames(String clazz);

	List<String> listClassNames();
}