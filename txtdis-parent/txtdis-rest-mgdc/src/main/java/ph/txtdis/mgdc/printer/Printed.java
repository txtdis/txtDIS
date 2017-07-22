package ph.txtdis.mgdc.printer;

import java.time.ZonedDateTime;

public interface Printed {

	void setPrintedBy(String user);

	void setPrintedOn(ZonedDateTime zdt);
}
