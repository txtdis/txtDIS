package ph.txtdis.mgdc.gsm.service;

import static org.apache.log4j.Logger.getLogger;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.type.QualityType;

@Service("goodRmaService")
public class GoodRmaServiceImpl //
		extends AbstractCustomerValidatedRmaService //
		implements GoodRefundedRmaService {

	private static Logger logger = getLogger(GoodRmaServiceImpl.class);

	@Override
	public String getAlternateName() {
		return "Good " + super.getAlternateName();
	}

	@Override
	public String getModuleName() {
		return "goodRma";
	}

	@Override
	protected QualityType quality() {
		return QualityType.GOOD;
	}

	@Override
	public void save() throws Information, Exception {
		get().setIsRma(true);
		logger.info("\n    Details@save: " + get().getDetails());
		super.save();
	}
}
