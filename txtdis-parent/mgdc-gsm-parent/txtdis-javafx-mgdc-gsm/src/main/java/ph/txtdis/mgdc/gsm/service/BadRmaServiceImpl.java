package ph.txtdis.mgdc.gsm.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.type.QualityType;

@Service("badRmaService")
public class BadRmaServiceImpl //
		extends AbstractCustomerValidatedRmaService //
		implements BadRefundedRmaService {

	private static Logger logger = getLogger(BadRmaServiceImpl.class);

	@Override
	public String getAlternateName() {
		return "Bad " + super.getAlternateName();
	}

	@Override
	public String getModuleName() {
		return "badRma";
	}

	@Override
	protected QualityType quality() {
		return QualityType.BAD;
	}

	@Override
	public void save() throws Information, Exception {
		get().setIsRma(false);
		logger.info("\n    Details@save: " + get().getDetails());
		super.save();
	}
}
