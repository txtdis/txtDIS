package ph.txtdis.mgdc.ccbpi.repository;

import java.util.List;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.repository.SpunRepository;

@Repository("itemRepository")
public interface ItemRepository //
		extends SpunRepository<ItemEntity, Long> {

	ItemEntity findByDescription( //
			@Param("description") String d);

	List<ItemEntity> findByDescriptionContaining( //
			@Param("description") String d);

	List<ItemEntity> findByFamilyName( //
			@Param("family") String f);

	ItemEntity findByName( //
			@Param("name") String s);

	List<ItemEntity> findByOrderById();

	ItemEntity findByVendorId( //
			@Param("vendorId") String s);
}
