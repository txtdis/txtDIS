package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.ItemFamilyEntity;
import ph.txtdis.dto.ItemFamily;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.type.ItemTier;

@Service("itemFamilyService")
public class ItemFamilyServiceImpl extends AbstractItemFamilyService implements ImportedItemFamilyService {

	private static Logger logger = getLogger(ItemFamilyServiceImpl.class);

	@Autowired
	private ReadOnlyService<ItemFamily> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		ItemFamily liquor = save(list("/classes").get(0));
		List<ItemFamily> categories = createCategoryClassTrees(liquor);
		createBrandCategoryTrees(categories);
	}

	@Override
	public ItemFamily save(ItemFamily l) {
		ItemFamilyEntity e = toEntity(l);
		e = repository.save(e);
		logger.info("\n\t\t\t\tItemFamily: " + e.getName() + " - " + e.getTier());
		return toDTO(e);
	}

	private List<ItemFamily> list(String endPoint) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module("itemFamily").getList(endPoint);
	}

	private List<ItemFamily> createCategoryClassTrees(ItemFamily liquor) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		List<ItemFamily> categories = list("/categories?of=" + liquor.getName());
		repository.save(toEntities(categories));
		categories.forEach(category -> treeService.save(category, liquor));
		return categories;
	}

	private void createBrandCategoryTrees(List<ItemFamily> categories) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		for (ItemFamily category : categories)
			for (ItemFamily brand : list("/brands?of=" + category.getName()))
				treeService.save(save(brand), category);
	}

	@Override
	public List<ItemFamily> findParents() {
		return findByTier(ItemTier.BRAND);
	}
}