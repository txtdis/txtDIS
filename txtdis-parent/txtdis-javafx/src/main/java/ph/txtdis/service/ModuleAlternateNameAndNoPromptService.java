package ph.txtdis.service;

public interface ModuleAlternateNameAndNoPromptService {

	String getAlternateName();

	default String getAbbreviatedModuleNoPrompt() {
		return getAlternateName() + " No. ";
	}
}
