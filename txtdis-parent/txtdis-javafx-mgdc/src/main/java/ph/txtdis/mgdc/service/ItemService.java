package ph.txtdis.mgdc.service;

import java.util.List;

import ph.txtdis.dto.QtyPerUom;

public interface ItemService {

	String getDescription();

	Long getId();

	String getName();

	String getVendorNo();

	boolean hasPurchaseUom();

	boolean isNew();

	List<QtyPerUom> listQtyPerUom();

	void setDescription(String text);

	void setName(String name);

	void setNameIfUnique(String name) throws Exception;

	void setQtyPerUomList(List<QtyPerUom> l);

	void setVendorId(String id);

}
