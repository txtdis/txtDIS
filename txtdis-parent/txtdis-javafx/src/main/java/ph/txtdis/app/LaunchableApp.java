package ph.txtdis.app;

public interface LaunchableApp //
		extends StartableApp {

	void actOn(String... id);

	default boolean isDialogCloserOnly() {
		return false;
	}

	void showAndWait();

	default void startAndWait() {
		initialize();
		showAndWait();
	}
}
