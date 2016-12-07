package ph.txtdis.service;

public interface Titled extends Moduled {

	default String getTitleText() {
		return getHeaderText() + " Master";
	}
}
