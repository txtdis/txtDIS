package ph.txtdis.mgdc.service.server;

import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.dto.Location;
import ph.txtdis.mgdc.domain.LocationEntity;
import ph.txtdis.mgdc.domain.LocationTreeEntity;
import ph.txtdis.mgdc.repository.LocationRepository;
import ph.txtdis.mgdc.repository.LocationTreeRepository;
import ph.txtdis.type.LocationType;

public abstract class AbstractLocationService //
		implements LocationService {

	@Autowired
	protected LocationRepository repository;

	@Autowired
	protected LocationTreeRepository treeRepository;

	@Override
	public List<Location> listBarangays(String city) {
		return getLocations(BARANGAY, city);
	}

	@Override
	public List<Location> listCities(String province) {
		return getLocations(CITY, province);
	}

	@Override
	public List<Location> listProvinces() {
		List<LocationEntity> l = repository.findByTypeOrderByNameAsc(PROVINCE);
		return l.stream().map(e -> toModel(e)).collect(Collectors.toList());
	}

	@Override
	public LocationEntity toEntity(Location t) {
		return t == null ? null : getEntity(t);
	}

	private LocationEntity getEntity(Location t) {
		LocationEntity e = repository.findByNameAndType(t.getName(), t.getType());
		return e != null ? e : createEntity(t);
	}

	private LocationEntity createEntity(Location t) {
		LocationEntity e = new LocationEntity();
		e.setName(t.getName());
		e.setType(t.getType());
		return e;
	}

	private List<Location> getLocations(LocationType type, String parent) {
		List<LocationTreeEntity> trees = treeRepository.findByLocationTypeAndParentNameOrderByLocationNameAsc(type, parent);
		return trees.stream().map(t -> toModel(t.getLocation())).collect(Collectors.toList());
	}

	@Override
	public Location toModel(LocationEntity e) {
		return e == null ? null : newLocation(e);
	}

	private Location newLocation(LocationEntity e) {
		Location l = new Location();
		l.setId(e.getId());
		l.setName(e.getName());
		l.setType(e.getType());
		l.setCreatedBy(e.getCreatedBy());
		l.setCreatedOn(e.getCreatedOn());
		return l;
	}
}