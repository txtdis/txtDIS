package ph.txtdis.app;

public interface LaunchableApp //
	extends App {

	void actOn(String... id);

	default boolean isDialogCloserOnly() {
		return false;
	}

	default void startAndWait() {
		initialize();
		showAndWait();
	}

	void showAndWait();
}
