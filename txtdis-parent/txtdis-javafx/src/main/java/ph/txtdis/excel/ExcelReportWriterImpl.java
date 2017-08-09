package ph.txtdis.excel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("excelReportWriter")
public class ExcelReportWriterImpl //
	extends AbstractExcelWriter //
	implements ExcelReportWriter {
}