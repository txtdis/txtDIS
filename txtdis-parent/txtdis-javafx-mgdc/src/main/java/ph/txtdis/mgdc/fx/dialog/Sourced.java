package ph.txtdis.mgdc.fx.dialog;

public interface Sourced<DS> {

    void setSource(DS source);

    void showAndWait();
}
