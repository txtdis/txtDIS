package ph.txtdis.mgdc.service.server;

public interface ConvertibleService<E, T> {

	E toEntity(T t);

	T toModel(E e);
}
