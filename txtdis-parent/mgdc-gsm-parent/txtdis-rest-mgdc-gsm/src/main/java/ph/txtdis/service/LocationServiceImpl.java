package ph.txtdis.service;

import static org.apache.log4j.Logger.getLogger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ph.txtdis.domain.LocationEntity;
import ph.txtdis.domain.LocationTreeEntity;
import ph.txtdis.dto.Location;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;

@Service("locationService")
public class LocationServiceImpl extends AbstractLocationService implements ImportedLocationService {

	private static Logger logger = getLogger(LocationServiceImpl.class);

	private static final String LOCATION = "location";

	@Autowired
	private ReadOnlyService<Location> readOnlyService;

	@Override
	public void importAll() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException {
		Location ncr = save(list("/provinces").get(0));
		List<Location> cities = createCityProvinceTrees(ncr);
		createBarangayCityTrees(cities);
	}

	private Location save(Location l) {
		LocationEntity e = toEntity(l);
		e = repository.save(e);
		logger.info("\n\t\t\t\tLocation: " + e.getName() + " - " + e.getType());
		return toDTO(e);
	}

	private List<Location> list(String endPoint) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return readOnlyService.module(LOCATION).getList(endPoint);
	}

	private List<Location> save(List<Location> ll) {
		List<LocationEntity> el = toEntities(ll);
		Iterable<LocationEntity> il = repository.save(el);
		return StreamSupport.stream(il.spliterator(), false).map(e -> toDTO(e)).collect(Collectors.toList());
	}

	private List<LocationEntity> toEntities(List<Location> l) {
		return l.stream().map(t -> toEntity(t)).collect(Collectors.toList());
	}

	private void newTree(Location child, Location parent) {
		LocationTreeEntity t = new LocationTreeEntity();
		t.setLocation(toEntity(child));
		t.setParent(toEntity(parent));
		treeRepository.save(t);
		logger.info("\n\t\t\t\tTree: " + child + ", " + parent);
	}

	private List<Location> createCityProvinceTrees(Location ncr) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		List<Location> cities = save(list("/cities?of=" + ncr.getName()));
		cities.forEach(c -> newTree(c, ncr));
		return cities;
	}

	private void createBarangayCityTrees(List<Location> cities) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException {
		for (Location c : cities)
			for (Location b : list("/barangays?of=" + c.getName()))
				newTree(save(b), c);
	}

	@Override
	public LocationEntity getByName(String name) {
		return repository.findByNameIgnoreCase(name);
	}
}