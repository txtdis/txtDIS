package ph.txtdis.service;

public interface ModuleAlternateNameAndNoPromptService {

	default String getAbbreviatedModuleNoPrompt() {
		return getAlternateName() + " No. ";
	}

	String getAlternateName();
}
