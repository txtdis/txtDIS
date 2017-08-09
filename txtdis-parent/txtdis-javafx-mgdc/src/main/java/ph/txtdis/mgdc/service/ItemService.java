package ph.txtdis.mgdc.service;

import ph.txtdis.dto.QtyPerUom;

import java.util.List;

public interface ItemService {

	String getDescription();

	void setDescription(String text);

	Long getId();

	String getName();

	void setName(String name);

	String getVendorNo();

	boolean hasPurchaseUom();

	boolean isNew();

	List<QtyPerUom> listQtyPerUom();

	void setNameIfUnique(String name) throws Exception;

	void setQtyPerUomList(List<QtyPerUom> l);

	void setVendorId(String id);

}
