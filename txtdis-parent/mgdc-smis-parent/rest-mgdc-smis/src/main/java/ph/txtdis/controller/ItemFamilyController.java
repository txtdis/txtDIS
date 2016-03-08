package ph.txtdis.controller;

import static java.util.Arrays.asList;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static ph.txtdis.type.ItemTier.PRODLINE;

import ph.txtdis.domain.ItemFamily;
import ph.txtdis.domain.ItemTree;
import ph.txtdis.repository.ItemFamilyRepository;
import ph.txtdis.repository.ItemTreeRepository;

@RestController("itemFamilyController")
@RequestMapping("/itemFamilies")
public class ItemFamilyController extends NameListController<ItemFamilyRepository, ItemFamily> {

	@Autowired
	private ItemTreeRepository treeRepository;

	private ItemTree tree;

	private List<ItemFamily> list;

	@RequestMapping(path = "/ancestry", method = GET)
	public ResponseEntity<?> find(@RequestParam("family") ItemFamily family) {
		setItemAncentryByTraversingTree(family);
		return new ResponseEntity<>(list, OK);
	}

	@RequestMapping(path = "/parents", method = GET)
	public ResponseEntity<?> findParents() {
		List<ItemFamily> l = repository.findByTierOrderByNameAsc(PRODLINE);
		return new ResponseEntity<>(l, OK);
	}

	private ItemTree getTree(ItemFamily family) {
		return tree = treeRepository.findByFamily(family);
	}

	private void setItemAncentryByTraversingTree(ItemFamily family) {
		list = new ArrayList<>(asList(family));
		traverseTree(family);
	}

	private void traverseTree(ItemFamily family) {
		while (getTree(family) != null) {
			family = tree.getParent();
			list.add(family);
		}
	}

}