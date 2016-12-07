package ph.txtdis.service;

import org.springframework.stereotype.Service;

import ph.txtdis.info.Information;
import ph.txtdis.type.QualityType;

@Service("goodRmaReplacementService")
public class GoodRmaReplacementServiceImpl //
		extends AbstractRmaReplacementService //
		implements GoodRmaReplacementService {

	@Override
	public String getAlternateName() {
		return "Good " + super.getAlternateName();
	}

	@Override
	public String getSpunModule() {
		return "goodRma";
	}

	@Override
	protected QualityType quality() {
		return QualityType.GOOD;
	}

	@Override
	public void save() throws Information, Exception {
		get().setIsRma(true);
		super.save();
	}
}
