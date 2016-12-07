package ph.txtdis.printer;

import java.time.ZonedDateTime;

public interface Printed {

	void setPrintedBy(String user);

	void setPrintedOn(ZonedDateTime zdt);
}
