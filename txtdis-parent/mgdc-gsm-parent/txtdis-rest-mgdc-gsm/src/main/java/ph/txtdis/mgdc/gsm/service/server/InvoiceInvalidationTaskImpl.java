package ph.txtdis.mgdc.gsm.service.server;

import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.util.DateTimeUtils.toDateDisplay;
import static ph.txtdis.util.DateTimeUtils.toUtilDate;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.SyncEntity;
import ph.txtdis.dto.Billable;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.service.SyncService;
import ph.txtdis.type.SyncType;

@Component("InvoiceInvalidationTask")
public class InvoiceInvalidationTaskImpl //
		implements InvoiceInvalidationTask {

	private static Logger logger = getLogger(InvoiceInvalidationTaskImpl.class);

	@Autowired
	private SyncRepository syncRepository;

	@Autowired
	private InvoiceService billingService;

	@Autowired
	private SyncService syncService;

	@Override
	@Transactional
	@Scheduled(initialDelay = 1000, fixedRate = 99999999)
	public void update() {
		if (syncService.getUpdateVersion().equalsIgnoreCase("0.0.1.1")) {
			logger.info("\n     Started invalidation");
			invalidateBillings();
			logger.info("\n     saveInvalidationUpdateSync");
			syncRepository.save(invalidationUpdateSync());
			logger.info("\n     Completed invalidation");
		}
	}

	private SyncEntity invalidationUpdateSync() {
		SyncEntity s = new SyncEntity();
		s.setLastSync(toUtilDate("2000-01-02"));
		s.setType(SyncType.UPDATE);
		return s;
	}

	private void invalidateBillings() {
		List<Long> invoiceIds = Arrays.asList( //
				75487L, //
				75215L, //
				75225L, //
				69294L, //
				75325L, //
				76668L, //
				76681L, //
				76695L, //
				76587L, //
				115145L, //
				159203L, //
				159203L, //
				158888L, //
				158888L, //
				158888L, //
				115148L, //
				76099L, //
				76100L, //
				159233L, //
				159238L, //
				76715L, //
				76731L, //
				158892L, //
				117006L, //
				117011L, //
				1170163L, //
				76609L, //
				75210L, //
				116980L, //
				159700L, //
				159699L, //
				159692L, //
				142351L, //
				77409L, //
				77413L, //
				76361L, //
				159651L, //
				142454L, //
				74821L, //
				74979L, //
				74980L, //
				74986L, //
				74995L, //
				76801L, //
				117042L, //
				76859L, //
				76862L, //
				76870L, //
				76875L, //
				74803L, //
				76976L, //
				117032L, //
				159332L, //
				159328L, //
				159329L, //
				142423L, //
				159531L, //
				159902L, //
				159828L, //
				159818L, //
				116989L, //
				1176392L, //
				142481L, //
				117140L, //
				117384L, //
				128834L, //
				128841L, //
				158218L, //
				158325L, //
				129006L, //
				158258L, //
				158298L, //
				129028L, //
				158186L, //
				158806L, //
				158808L, //
				158811L, //
				158814L, //
				158815L, //
				158822L, //
				76572L, //
				76574L, //
				76577L, //
				76582L, //
				76639L, //
				76640L, //
				76643L, //
				76645L, //
				76648L, //
				158854L, //
				158855L, //
				158858L, //
				75335L, //
				75340L, //
				75345L, //
				76505L, //
				76506L, //
				76507L, //
				76508L, //
				76514L, //
				76520L, //
				158861L, //
				158864L, //
				158868L, //
				76521L, //
				76526L, //
				76532L, //
				76534L, //
				76537L, //
				158841L, //
				158846L, //
				158849L, //
				159202L, //
				158835L, //
				159204L, //
				159214L, //
				159216L, //
				159219L, //
				159220L, //
				159221L, //
				158876L, //
				158886L, //
				76753L, //
				76756L, //
				159236L, //
				76780L, //
				76781L, //
				76782L, //
				78787L, //
				76789L, //
				76791L, //
				158896L, //
				158897L, //
				159303L, //
				159246L, //
				159248L, //
				159105L, //
				159106L, //
				159107L, //
				159108L, //
				159109L, //
				159111L, //
				159115L, //
				159116L, //
				74857L, //
				74861L, //
				74868L, //
				77209L, //
				77213L, //
				77215L, //
				76849L, //
				76850L, //
				159652L, //
				159655L, //
				159656L, //
				142350L, //
				142348L, //
				142455L, //
				142457L, //
				74811L, //
				74812L, //
				74815L, //
				74816L, //
				74819L, //
				74821L, //
				74829L, //
				74833L, //
				74835L, //
				74838L, //
				74844L, //
				74846L, //
				76804L, //
				76805L, //
				76818L, //
				76821L, //
				768232L, //
				768234L, //
				768238L, //
				768242L, //
				74936L, //
				74937L, //
				74943L, //
				74945L, //
				74950L, //
				74801L, //
				74802L, //
				74804L, //
				159341L, //
				159342L, //
				159343L, //
				159344L, //
				159346L, //
				159347L, //
				159348L, //
				159991L, //
				159992L, //
				159995L, //
				159997L, //
				159999L, //
				142302L, //
				142306L, //
				142313L, //
				142314L, //
				142319L, //
				142320L, //
				142321L, //
				142322L, //
				142331L, //
				142333L, //
				142335L, //
				142337L, //
				142338L, //
				142339L, //
				159532L, //
				159534L, //
				159535L, //
				159537L, //
				159550L, //
				158339L, //
				77877L, //
				77878L, //
				77881L, //
				77882L, //
				77883L, //
				77885L, //
				77886L, //
				77891L, //
				128862L, //
				128826L, //
				128802L, //
				128803L, //
				128839L, //
				77867L, //
				79904L, //
				77884L, //
				77458L, //
				79979L, //
				77817L, //
				116964L, //
				116951L, //
				117315L, //
				76684L, //
				158835L, //
				115149L, //
				115150L, //
				159242L, //
				74984L, //
				159332L, //
				159984L, //
				159513L, //
				159668L, //
				159677L, //
				159684L, //
				117168L, //
				77661L, //
				77682L, //
				117322L, //
				77046L, //
				77044L, //
				117161L, //
				116923L, //
				117189L, //
				128860L, //
				128861L, //
				129008L, //
				128872L, //
				128873L, //
				158045L, //
				158044L, //
				158249L, //
				158254L, //
				158115L, //
				128881L, //
				128865L, //
				158088L, //
				77758L, //
				158095L, //
				159886L, //
				159863L, //
				63311L, //
				76209L, // 
				76224L, //
				76233L, //
				76242L, //
				76326L, //
				76107L, //
				76135L, //
				76150L, //
				75448L, //
				75121L, //
				75131L, //
				75146L, //
				141671L, //
				75004L, //
				75027L);
		List<Billable> l = billingService.findAllInvalidInvoices(invoiceIds);
		if (l != null)
			l.stream() //
					.map(b -> invalidate(b)) //
					.forEach(b -> billingService.save(b));
	}

	private Billable invalidate(Billable b) {
		b.setIsValid(false);
		b.setDecidedBy("LORNA");
		b.setDecidedOn(ZonedDateTime.now());
		b.setRemarks(remarks(b));
		return b;
	}

	private String remarks(Billable b) {
		String r = b.getRemarks();
		if (r != null && !r.isEmpty())
			r = "\n" + r;
		else
			r = "";
		return "[DISAPPROVED: LORNA - " + toDateDisplay(LocalDate.now()) + "]" + r;
	}
}
