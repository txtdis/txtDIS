package ph.txtdis.service;

public interface AppendableDetailService {

	default String getAppendableErrorMessage() {
		return "Not Allowed";
	}

	boolean isAppendable();
}
