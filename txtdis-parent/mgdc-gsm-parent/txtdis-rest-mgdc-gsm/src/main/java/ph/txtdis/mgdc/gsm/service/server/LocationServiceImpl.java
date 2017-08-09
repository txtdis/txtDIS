package ph.txtdis.mgdc.gsm.service.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.domain.LocationTreeEntity;
import ph.txtdis.mgdc.service.server.AbstractLocationService;
import ph.txtdis.service.RestClientService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.apache.log4j.Logger.getLogger;

@Service("locationService")
public class LocationServiceImpl //
	extends AbstractLocationService //
	implements ImportedLocationService {

	private static final String LOCATION = "location";

	private static Logger logger = getLogger(LocationServiceImpl.class);

	@Autowired
	private RestClientService<Location> restClientService;

	@Override
	public LocationEntity getByName(String name) {
		return repository.findByNameIgnoreCase(name);
	}

	@Override
	public void importAll() throws Exception {
		Location ncr = save(list("/provinces").get(0));
		List<Location> cities = createCityProvinceTrees(ncr);
		createBarangayCityTrees(cities);
	}

	private Location save(Location l) {
		LocationEntity e = toEntity(l);
		e = repository.save(e);
		logger.info("\n\t\t\t\tLocation: " + e.getName() + " - " + e.getType());
		return toModel(e);
	}

	private List<Location> list(String endPoint) throws Exception {
		return restClientService.module(LOCATION).getList(endPoint);
	}

	private List<Location> createCityProvinceTrees(Location ncr) throws Exception {
		List<Location> cities = save(list("/cities?of=" + ncr.getName()));
		cities.forEach(c -> newTree(c, ncr));
		return cities;
	}

	private void createBarangayCityTrees(List<Location> cities) throws Exception {
		for (Location c : cities)
			for (Location b : list("/barangays?of=" + c.getName()))
				newTree(save(b), c);
	}

	private List<Location> save(List<Location> ll) {
		List<LocationEntity> el = toEntities(ll);
		Iterable<LocationEntity> il = repository.save(el);
		return StreamSupport.stream(il.spliterator(), false).map(e -> toModel(e)).collect(Collectors.toList());
	}

	private void newTree(Location child, Location parent) {
		LocationTreeEntity t = new LocationTreeEntity();
		t.setLocation(toEntity(child));
		t.setParent(toEntity(parent));
		treeRepository.save(t);
		logger.info("\n\t\t\t\tTree: " + child + ", " + parent);
	}

	private List<LocationEntity> toEntities(List<Location> l) {
		return l.stream().map(t -> toEntity(t)).collect(Collectors.toList());
	}
}