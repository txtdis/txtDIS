package ph.txtdis.service;

import static java.io.File.separator;
import static java.lang.System.getProperty;
import static java.nio.file.Files.newBufferedWriter;
import static java.nio.file.Paths.get;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.ScriptEntity;
import ph.txtdis.dto.Script;
import ph.txtdis.exception.FailedReplicationException;
import ph.txtdis.repository.ScriptRepository;
import ph.txtdis.type.ScriptType;

@Service("scriptService")
public class ScriptServiceImpl extends AbstractIdService<ScriptRepository, ScriptEntity, Script, Long>
		implements ScriptService {

	private static final String SCRIPT_CREATION = "Script Creation";

	@Autowired
	private BillableService billableService;

	@Autowired
	private CreditDetailService creditDetailService;

	@Autowired
	private CustomerDiscountService customerDiscountService;

	@Autowired
	private PriceService priceService;

	@Autowired
	private RemittanceService remittanceService;

	@Autowired
	private SyncService syncService;

	@Autowired
	private VolumeDiscountService volumeDiscountService;

	@Value("${database.name}")
	private String databaseName;

	@Override
	public Script getUnpostedTransactions() {
		ScriptEntity e = repository.findFirstBySentFalse();
		return toDTO(e);
	}

	@Override
	protected Script toDTO(ScriptEntity e) {
		return e == null ? null : newScript(e);
	}

	private Script newScript(ScriptEntity e) {
		Script t = new Script();
		t.setId(e.getId());
		t.setScript(e.getScript());
		t.setSent(e.isSent());
		t.setType(e.getType());
		return t;
	}

	@Override
	protected ScriptEntity toEntity(Script t) {
		return t == null ? null : newEntity(t);
	}

	private ScriptEntity newEntity(Script t) {
		ScriptEntity e = new ScriptEntity();
		e.setId(t.getId());
		e.setScript(t.getScript());
		e.setSent(t.isSent());
		e.setType(t.getType());
		return e;
	}

	@Override
	public List<Script> listScripts() {
		return toList(repository.findBySentFalse());
	}

	@Override
	@Transactional
	public void runDownloadedScripts() throws FailedReplicationException {
		try (Stream<String> stream = Files.lines(path())) {
			stream.map(l -> split(l, "|")).forEach(s -> {
				ScriptType type = ScriptType.valueOf(s[0]);
				switch (type) {
					case BILLING_APPROVAL:
						billableService.updateDecisionData(s);
						break;
					case DEPOSIT:
						remittanceService.updateDeposit(s);
						break;
					case FUND_TRANSFER:
						remittanceService.updateFundTransfer(s);
						break;
					case PAYMENT_VALIDATION:
						remittanceService.updatePaymentValidation(s);
						break;
					case RETURN_PAYMENT:
						billableService.updateItemReturnPayment(s);
						break;
					case CREDIT_APPROVAL:
						creditDetailService.updateDecisionData(s);
						break;
					case CUSTOMER_DISCOUNT_APPROVAL:
						customerDiscountService.updateDecisionData(s);
						break;
					case PRICE_APPROVAL:
						priceService.updateDecisionData(s);
						break;
					case VERSION_UPDATE:
						syncService.updateAppVersion(s);
						break;
					case VOLUME_DISCOUNT_APPROVAL:
						volumeDiscountService.updateDecisionData(s);
						break;
					default:
						break;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedReplicationException(SCRIPT_CREATION);
		}
	}

	@Override
	public void tagSentScriptsAsSuch(List<Script> scripts) {
		scripts.stream().forEach(s -> s.setSent(true));
		save(scripts);
	}

	@Override
	public void write(List<Script> l) throws FailedReplicationException {
		try (BufferedWriter writer = newBufferedWriter(path())) {
			for (Script s : l)
				writer.write(s.getType() + "|" + s.getScript() + "\n");
		} catch (Exception e) {
			e.printStackTrace();
			throw new FailedReplicationException(SCRIPT_CREATION);
		}
	}

	private Path path() {
		return get(getProperty("user.home") + separator + databaseName + ".script");
	}

	@Override
	public List<Script> save(List<Script> s) {
		List<ScriptEntity> l = s.stream().map(t -> toEntity(t)).collect(Collectors.toList());
		Iterable<ScriptEntity> i = repository.save(l);
		return StreamSupport.stream(i.spliterator(), false).map(e -> toDTO(e)).collect(Collectors.toList());
	}
}
