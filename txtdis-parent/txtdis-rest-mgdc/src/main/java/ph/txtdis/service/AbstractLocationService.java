package ph.txtdis.service;

import static ph.txtdis.type.LocationType.BARANGAY;
import static ph.txtdis.type.LocationType.CITY;
import static ph.txtdis.type.LocationType.PROVINCE;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import ph.txtdis.domain.LocationEntity;
import ph.txtdis.domain.LocationTreeEntity;
import ph.txtdis.dto.Location;
import ph.txtdis.repository.LocationRepository;
import ph.txtdis.repository.LocationTreeRepository;
import ph.txtdis.type.LocationType;

public abstract class AbstractLocationService implements PrimaryLocationService {

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
		return l.stream().map(e -> toDTO(e)).collect(Collectors.toList());
	}

	@Override
	public Location toDTO(LocationEntity e) {
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
		List<LocationTreeEntity> trees = treeRepository.findByLocationTypeAndParentNameOrderByLocationNameAsc(type,
				parent);
		return trees.stream().map(t -> toDTO(t.getLocation())).collect(Collectors.toList());
	}
}