package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.type.QualityType;

@Service("badRmaReplacementService")
public class BadRmaReplacementServiceImpl //
		extends AbstractRmaReplacementService //
		implements BadRmaReplacementService {

	@Override
	public String getAlternateName() {
		return "Bad " + super.getAlternateName();
	}

	@Override
	public String getSpunModule() {
		return "badRma";
	}

	@Override
	protected QualityType quality() {
		return QualityType.BAD;
	}

	@Override
	public void save() throws Information, Exception {
		get().setIsRma(false);
		super.save();
	}
}
