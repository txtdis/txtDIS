package ph.txtdis;

import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.StreamSupport.stream;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.DeliveryType.PICK_UP;
import static ph.txtdis.type.SyncType.BACKUP;
import static ph.txtdis.type.UserType.SYSGEN;
import static ph.txtdis.util.DateTimeUtils.toLocalDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.SyncEntity;
import ph.txtdis.mgdc.ccbpi.domain.BillableEntity;
import ph.txtdis.mgdc.ccbpi.domain.ChannelEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerDiscountEntity;
import ph.txtdis.mgdc.ccbpi.domain.CustomerEntity;
import ph.txtdis.mgdc.ccbpi.domain.ItemEntity;
import ph.txtdis.mgdc.ccbpi.domain.RoutingEntity;
import ph.txtdis.mgdc.ccbpi.repository.ChannelRepository;
import ph.txtdis.mgdc.ccbpi.repository.CustomerRepository;
import ph.txtdis.mgdc.ccbpi.repository.ItemRepository;
import ph.txtdis.mgdc.ccbpi.repository.OrderConfirmationRepository;
import ph.txtdis.mgdc.ccbpi.repository.RoutingRepository;
import ph.txtdis.mgdc.domain.ItemFamilyEntity;
import ph.txtdis.mgdc.domain.RouteEntity;
import ph.txtdis.mgdc.repository.ItemFamilyRepository;
import ph.txtdis.mgdc.repository.RouteRepository;
import ph.txtdis.repository.SyncRepository;
import ph.txtdis.type.BeverageType;
import ph.txtdis.type.ItemTier;
import ph.txtdis.util.DateTimeUtils;

@Component("revision2")
public class Revision2Impl //
		implements Revision2 {

	private static Logger logger = getLogger(Revision2Impl.class);

	@Autowired
	private ChannelRepository channelRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ItemFamilyRepository itemFamilyRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private OrderConfirmationRepository ocsRepository;

	@Autowired
	private RouteRepository routeRepository;

	@Autowired
	private RoutingRepository routingRepository;

	@Autowired
	private SyncRepository syncRepository;

	@Value("${go.live}")
	private String goLive;

	private RouteEntity pickUp, rommel, aljon, gerald, jerome;

	private ItemFamilyEntity fulls, empties;

	@Override
	@Transactional
	public void givePickUpDiscountsToSelectedCustomersAndReplaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels() {
		Date date = DateTimeUtils.toUtilDate("2009-06-06");
		SyncEntity sync = backupSync();
		if (areDiscountsGivenAndRouteReplacementAndConversionNotDone(date, sync))
			givePickUpDiscountsToSelectedCustomersAndReplaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels(date, sync);
	}

	private SyncEntity backupSync() {
		return syncRepository.findOne(BACKUP);
	}

	private boolean areDiscountsGivenAndRouteReplacementAndConversionNotDone(Date date, SyncEntity sync) {
		LocalDate syncDate = toLocalDate(sync.getLastSync());
		LocalDate referenceDate = toLocalDate(date);
		return !syncDate.isEqual(referenceDate);
	}

	private void givePickUpDiscountsToSelectedCustomersAndReplaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels(Date date, SyncEntity sync) {
		replaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels();
		givePickUpDiscountsToSelectedCustomers();
		syncRepository.save(updateSync(sync, date));
	}

	private void replaceRoutesFromCokestoTrucksAndConvertTheFormerToChannels() {
		convertCokeRoutesToChannels();
		setCustomerChannels();
		replaceRoutesFromOfCoketoDelivery();
	}

	private void convertCokeRoutesToChannels() {
		Iterable<RouteEntity> routes = routeRepository.findAll();
		routes.forEach(r -> newChannel(r));
	}

	private void newChannel(RouteEntity r) {
		ChannelEntity e = new ChannelEntity();
		e.setName(r.getName());
		channelRepository.save(e);
	}

	private void setCustomerChannels() {
		customerRepository.findAll().forEach(c -> updateChannel(c));
	}

	private void updateChannel(CustomerEntity c) {
		c.setChannel(channel(c));
		customerRepository.save(c);
	}

	private ChannelEntity channel(CustomerEntity c) {
		ChannelEntity channel = channelRepository.findOne(routeIdAsChannel(c));
		logger.info("\n    ChannelEntity = " + channel);
		return channel;
	}

	private Long routeIdAsChannel(CustomerEntity c) {
		return c == null ? 0L : routeId(c);
	}

	private Long routeId(CustomerEntity c) {
		RoutingEntity r = latestRouting(c);
		return r == null ? 0L : routeId(r);
	}

	private Long routeId(RoutingEntity e) {
		RouteEntity r = e.getRoute();
		return r == null ? 0L : r.getId();
	}

	private RoutingEntity latestRouting(CustomerEntity c) {
		List<RoutingEntity> l = findAllRoutings(c);
		return l == null ? null
				: l.stream() //
						.collect(maxBy(comparing(RoutingEntity::getStartDate))) //
						.orElse(null);
	}

	private List<RoutingEntity> findAllRoutings(CustomerEntity c) {
		return routingRepository.findByCustomerOrderByStartDateDesc(c);
	}

	private void replaceRoutesFromOfCoketoDelivery() {
		routeRepository.delete(ewhs());
		routeRepository.save(deliveryRoutes());
		assignDeliveryRoutesToCustomer();
	}

	private long ewhs() {
		return 1L;
	}

	private List<RouteEntity> deliveryRoutes() {
		return Arrays.asList( //
				pickUp = route(2, PICK_UP.toString()), //
				rommel = route(3, "ROMMEL"), //
				aljon = route(4, "ALJON"), //
				gerald = route(5, "GERALD"), //
				jerome = route(6, "JEROME"));
	}

	private RouteEntity route(long id, String name) {
		RouteEntity r = routeRepository.findOne(id);
		r.setName(name);
		return routeRepository.save(r);
	}

	private void assignDeliveryRoutesToCustomer() {
		customerRepository.save(deliveryRouteAssignedCustomers());
		setPickUpToCustomersWithNoAssignedDeliveryRoutes();
	}

	private List<CustomerEntity> deliveryRouteAssignedCustomers() {
		routingRepository.deleteAll();
		return Arrays.asList(//
				updateCustomer(503746808, jerome), //
				updateCustomer(503746953, rommel), //
				updateCustomer(503747162, pickUp), //
				updateCustomer(503747201, rommel), //
				updateCustomer(503747217, jerome), //
				updateCustomer(503747238, jerome), //
				updateCustomer(503747281, aljon), //
				updateCustomer(503747379, jerome), //
				updateCustomer(503747698, aljon), //
				updateCustomer(503747623, gerald), //
				updateCustomer(503747831, gerald), //
				updateCustomer(503747911, gerald), //
				updateCustomer(503747912, gerald), //
				updateCustomer(503747925, jerome), //
				updateCustomer(504606047, aljon), //
				updateCustomer(503748295, gerald), //
				updateCustomer(503748407, jerome), //
				updateCustomer(503748530, rommel), //
				updateCustomer(503748713, rommel), //
				updateCustomer(504432704, gerald), //
				updateCustomer(503749662, gerald), //
				updateCustomer(503751264, pickUp), //
				updateCustomer(503750019, jerome), //
				updateCustomer(503750237, aljon), //
				updateCustomer(503750579, jerome), //
				updateCustomer(503750892, jerome), //
				updateCustomer(503750913, gerald), //
				updateCustomer(503750926, rommel), //
				updateCustomer(503750934, rommel), //
				updateCustomer(503751022, jerome), //
				updateCustomer(503751360, gerald), //
				updateCustomer(503751484, jerome), //
				updateCustomer(503752200, jerome), //
				updateCustomer(503752446, rommel), //
				updateCustomer(503752363, gerald), //
				updateCustomer(504877694, gerald), //
				updateCustomer(503752543, rommel), //
				updateCustomer(503753068, jerome), //
				updateCustomer(503753244, jerome), //
				updateCustomer(503753258, jerome), //
				updateCustomer(503753300, gerald), //
				updateCustomer(503933776, gerald), //
				updateCustomer(503753608, jerome), //
				updateCustomer(503752934, jerome), //
				updateCustomer(503753723, rommel), //
				updateCustomer(504479809, gerald), //
				updateCustomer(503753772, jerome), //
				updateCustomer(503753825, rommel), //
				updateCustomer(503754145, gerald), //
				updateCustomer(503754670, rommel), //
				updateCustomer(503754508, gerald), //
				updateCustomer(503754865, jerome), //
				updateCustomer(503755035, aljon), //
				updateCustomer(503755109, rommel), //
				updateCustomer(503755157, rommel), //
				updateCustomer(503755959, aljon), //
				updateCustomer(503755844, jerome), //
				updateCustomer(503755864, jerome), //
				updateCustomer(503755582, rommel), //
				updateCustomer(503756018, rommel), //
				updateCustomer(503756020, jerome), //
				updateCustomer(503756476, rommel), //
				updateCustomer(503756477, rommel), //
				updateCustomer(503755039, aljon), //
				updateCustomer(503756717, rommel), //
				updateCustomer(503757101, aljon), //
				updateCustomer(503757213, rommel), //
				updateCustomer(503757301, rommel), //
				updateCustomer(503757526, rommel), //
				updateCustomer(503757537, gerald), //
				updateCustomer(503757684, jerome), //
				updateCustomer(503757929, rommel), //
				updateCustomer(503757980, jerome), //
				updateCustomer(503758116, gerald), //
				updateCustomer(503758473, aljon), //
				updateCustomer(503758485, pickUp), //
				updateCustomer(503758699, aljon), //
				updateCustomer(503758813, aljon), //
				updateCustomer(503758814, gerald), //
				updateCustomer(503758894, jerome), //
				updateCustomer(503759376, aljon), //
				updateCustomer(503759306, aljon), //
				updateCustomer(503759670, jerome), //
				updateCustomer(503759825, gerald), //
				updateCustomer(503759842, jerome), //
				updateCustomer(503901657, gerald), //
				updateCustomer(503760351, rommel), //
				updateCustomer(503760622, gerald), //
				updateCustomer(503760766, rommel), //
				updateCustomer(503760771, rommel), //
				updateCustomer(503760995, gerald), //
				updateCustomer(503761282, jerome), //
				updateCustomer(503761290, jerome), //
				updateCustomer(503761403, pickUp), //
				updateCustomer(503761418, jerome), //
				updateCustomer(503761785, jerome), //
				updateCustomer(503761561, jerome), //
				updateCustomer(503761285, jerome), //
				updateCustomer(503760720, gerald), //
				updateCustomer(503761824, aljon), //
				updateCustomer(503762287, gerald), //
				updateCustomer(503762333, aljon), //
				updateCustomer(503762589, aljon), //
				updateCustomer(503762626, jerome), //
				updateCustomer(503762755, aljon), //
				updateCustomer(503763083, rommel), //
				updateCustomer(503763537, jerome), //
				updateCustomer(503763637, jerome), //
				updateCustomer(503763801, jerome), //
				updateCustomer(503764422, jerome), //
				updateCustomer(503763930, gerald), //
				updateCustomer(503764899, jerome), //
				updateCustomer(503839793, gerald), //
				updateCustomer(503765005, jerome), //
				updateCustomer(503765520, gerald), //
				updateCustomer(503765626, gerald), //
				updateCustomer(503765915, rommel), //
				updateCustomer(503766123, jerome), //
				updateCustomer(503766146, jerome), //
				updateCustomer(503766357, rommel), //
				updateCustomer(503766418, jerome), //
				updateCustomer(503766607, gerald), //
				updateCustomer(503766930, rommel), //
				updateCustomer(503766206, jerome), //
				updateCustomer(503767719, jerome), //
				updateCustomer(503767671, gerald), //
				updateCustomer(503768215, gerald), //
				updateCustomer(503768858, rommel), //
				updateCustomer(503768715, gerald), //
				updateCustomer(503769772, jerome), //
				updateCustomer(503769864, rommel), //
				updateCustomer(503769484, jerome), //
				updateCustomer(503769366, jerome), //
				updateCustomer(503770480, aljon), //
				updateCustomer(503842532, gerald), //
				updateCustomer(503770772, aljon), //
				updateCustomer(503770853, gerald), //
				updateCustomer(503771123, gerald), //
				updateCustomer(503838180, pickUp), //
				updateCustomer(503771628, jerome), //
				updateCustomer(503771748, jerome), //
				updateCustomer(503771671, jerome), //
				updateCustomer(503772004, gerald), //
				updateCustomer(504762683, gerald), //
				updateCustomer(503773439, pickUp), //
				updateCustomer(503774396, gerald), //
				updateCustomer(504491845, gerald), //
				updateCustomer(503747182, rommel), //
				updateCustomer(503774419, gerald), //
				updateCustomer(503774438, rommel), //
				updateCustomer(503774792, aljon), //
				updateCustomer(503775016, rommel), //
				updateCustomer(503775277, jerome), //
				updateCustomer(503775077, rommel), //
				updateCustomer(503775342, rommel), //
				updateCustomer(503775457, rommel), //
				updateCustomer(503775509, jerome), //
				updateCustomer(503775536, gerald), //
				updateCustomer(503775697, rommel), //
				updateCustomer(503775715, jerome), //
				updateCustomer(503775742, gerald), //
				updateCustomer(503776010, jerome), //
				updateCustomer(503776719, jerome), //
				updateCustomer(503776891, jerome), //
				updateCustomer(503776916, jerome), //
				updateCustomer(503776949, gerald), //
				updateCustomer(503776993, gerald), //
				updateCustomer(503777529, rommel), //
				updateCustomer(503777815, jerome), //
				updateCustomer(503931867, gerald), //
				updateCustomer(503778037, jerome), //
				updateCustomer(503778077, jerome), //
				updateCustomer(503778079, gerald), //
				updateCustomer(503778412, rommel), //
				updateCustomer(504107365, gerald), //
				updateCustomer(503838181, aljon), //
				updateCustomer(503880977, aljon), //
				updateCustomer(503842528, aljon), //
				updateCustomer(503842493, jerome), //
				updateCustomer(503841872, gerald), //
				updateCustomer(503880974, jerome), //
				updateCustomer(503842491, jerome), //
				updateCustomer(503880669, rommel), //
				updateCustomer(503880675, rommel), //
				updateCustomer(503931983, jerome), //
				updateCustomer(503932976, aljon), //
				updateCustomer(503933164, jerome), //
				updateCustomer(503931879, jerome), //
				updateCustomer(503931997, jerome), //
				updateCustomer(503900649, jerome), //
				updateCustomer(503931981, rommel), //
				updateCustomer(503907248, jerome), //
				updateCustomer(503931864, jerome), //
				updateCustomer(503931945, gerald), //
				updateCustomer(503931972, jerome), //
				updateCustomer(503932943, rommel), //
				updateCustomer(503931420, gerald), //
				updateCustomer(503931965, gerald), //
				updateCustomer(503900659, jerome), //
				updateCustomer(503926144, jerome), //
				updateCustomer(503774676, gerald), //
				updateCustomer(503900660, jerome), //
				updateCustomer(503931937, gerald), //
				updateCustomer(503900658, jerome), //
				updateCustomer(504107341, aljon), //
				updateCustomer(504129589, aljon), //
				updateCustomer(504101850, pickUp), //
				updateCustomer(504107368, jerome), //
				updateCustomer(504107391, jerome), //
				updateCustomer(504115738, aljon), //
				updateCustomer(504124334, rommel), //
				updateCustomer(504235426, pickUp), //
				updateCustomer(504259858, pickUp), //
				updateCustomer(504448585, pickUp), //
				updateCustomer(504404283, gerald), //
				updateCustomer(504408512, gerald), //
				updateCustomer(504448611, gerald), //
				updateCustomer(504408553, jerome), //
				updateCustomer(504408554, jerome), //
				updateCustomer(504408557, pickUp), //
				updateCustomer(504408560, jerome), //
				updateCustomer(504408563, jerome), //
				updateCustomer(504408565, jerome), //
				updateCustomer(504410792, aljon), //
				updateCustomer(504411756, gerald), //
				updateCustomer(504411759, gerald), //
				updateCustomer(504423619, aljon), //
				updateCustomer(504423621, rommel), //
				updateCustomer(504423984, gerald), //
				updateCustomer(504423986, gerald), //
				updateCustomer(504426447, rommel), //
				updateCustomer(504428318, aljon), //
				updateCustomer(504428825, gerald), //
				updateCustomer(504451112, aljon), //
				updateCustomer(504490326, gerald), //
				updateCustomer(504425768, gerald), //
				updateCustomer(504549747, aljon), //
				updateCustomer(504626966, aljon), //
				updateCustomer(504627641, gerald), //
				updateCustomer(504649368, jerome), //
				updateCustomer(504630858, jerome), //
				updateCustomer(504630870, jerome), //
				updateCustomer(504630874, gerald), //
				updateCustomer(504649332, aljon), //
				updateCustomer(504650923, pickUp), //
				updateCustomer(504650979, aljon), //
				updateCustomer(504650980, jerome), //
				updateCustomer(504660554, gerald), //
				updateCustomer(504660558, gerald), //
				updateCustomer(504666957, aljon), //
				updateCustomer(504666967, pickUp), //
				updateCustomer(504696211, pickUp), //
				updateCustomer(504697319, gerald), //
				updateCustomer(504650981, jerome), //
				updateCustomer(504765291, jerome), //
				updateCustomer(504702510, aljon), //
				updateCustomer(504710384, rommel), //
				updateCustomer(504768710, rommel), //
				updateCustomer(504710910, jerome), //
				updateCustomer(504717525, aljon), //
				updateCustomer(504717526, aljon), //
				updateCustomer(504721051, gerald), //
				updateCustomer(504721054, jerome), //
				updateCustomer(504721060, gerald), //
				updateCustomer(504721133, aljon), //
				updateCustomer(504725985, aljon), //
				updateCustomer(504784877, pickUp), //
				updateCustomer(504727831, aljon), //
				updateCustomer(504747872, jerome), //
				updateCustomer(504758674, aljon), //
				updateCustomer(504778817, pickUp), //
				updateCustomer(504769733, jerome), //
				updateCustomer(504782127, jerome), //
				updateCustomer(504767938, gerald), //
				updateCustomer(504758680, rommel), //
				updateCustomer(504759804, jerome), //
				updateCustomer(504765879, jerome), //
				updateCustomer(504759802, jerome), //
				updateCustomer(504782124, aljon), //
				updateCustomer(504762684, gerald), //
				updateCustomer(504759877, jerome), //
				updateCustomer(503773500, rommel), //
				updateCustomer(504762829, gerald), //
				updateCustomer(504765872, gerald), //
				updateCustomer(504788865, pickUp), //
				updateCustomer(504736501, aljon), //
				updateCustomer(504882613, jerome), //
				updateCustomer(504886279, aljon), //
				updateCustomer(504428799, gerald), //
				updateCustomer(504889399, rommel), //
				updateCustomer(504903060, aljon), //
				updateCustomer(503760483, jerome), //
				updateCustomer(503761501, gerald), //
				updateCustomer(503765453, jerome), //
				updateCustomer(503763019, aljon), //
				updateCustomer(504878579, jerome), //
				updateCustomer(504658699, jerome), //
				updateCustomer(503765930, jerome), //
				updateCustomer(503749173, jerome), //
				updateCustomer(503748520, jerome), //
				updateCustomer(503865009, aljon), //
				updateCustomer(504713707, aljon), //
				updateCustomer(503880976, jerome), //
				updateCustomer(503773649, rommel), //
				updateCustomer(503759460, jerome), //
				updateCustomer(503761667, rommel), //
				updateCustomer(504214828, jerome), //
				updateCustomer(503751718, rommel), //
				updateCustomer(503933773, rommel), //
				updateCustomer(504935791, aljon), //
				updateCustomer(504990891, aljon), //
				updateCustomer(503747266, pickUp), //
				updateCustomer(503760889, aljon), //
				updateCustomer(503771143, aljon), //
				updateCustomer(503768561, rommel), //
				updateCustomer(504615013, jerome), //
				updateCustomer(503750833, rommel), //
				updateCustomer(503926138, rommel), //
				updateCustomer(504728875, aljon), //
				updateCustomer(504990464, pickUp), //
				updateCustomer(503749315, gerald), //
				updateCustomer(503760066, gerald), //
				updateCustomer(503772994, pickUp), //
				updateCustomer(1, rommel), //
				updateCustomer(504876642, rommel), //
				updateCustomer(504777825, rommel), //
				updateCustomer(503750631, gerald), //
				updateCustomer(503757587, rommel), //
				updateCustomer(504658145, gerald), //
				updateCustomer(503751934, gerald), //
				updateCustomer(503768364, rommel), //
				updateCustomer(504879751, gerald), //
				updateCustomer(7, aljon), //
				updateCustomer(503760397, aljon), //
				updateCustomer(504408555, aljon), //
				updateCustomer(503748772, rommel), //
				updateCustomer(503931984, rommel), //
				updateCustomer(2, gerald), //
				updateCustomer(3, gerald), //
				updateCustomer(503765869, rommel), //
				updateCustomer(504549745, rommel), //
				updateCustomer(503766907, rommel), //
				updateCustomer(503770856, rommel), //
				updateCustomer(4, rommel), //
				updateCustomer(5, rommel), //
				updateCustomer(503773512, jerome), //
				updateCustomer(503747371, gerald), //
				updateCustomer(503749014, aljon), //
				updateCustomer(6, gerald), //
				updateCustomer(504998416, pickUp), //
				updateCustomer(504935540, pickUp), //
				updateCustomer(503749441, pickUp), //
				updateCustomer(503778398, rommel), //
				updateCustomer(503772959, pickUp), //
				updateCustomer(503774779, gerald), //
				updateCustomer(503768678, gerald), //
				updateCustomer(503749718, rommel), //
				updateCustomer(503757005, rommel), //
				updateCustomer(503774627, rommel), //
				updateCustomer(504357591, rommel), //
				updateCustomer(503759443, gerald), //
				updateCustomer(503764755, aljon), //
				updateCustomer(503763044, gerald), //
				updateCustomer(503907277, rommel), //
				updateCustomer(504428826, gerald), //
				updateCustomer(503776842, rommel), //
				updateCustomer(503762629, rommel), //
				updateCustomer(503753953, gerald), //
				updateCustomer(503777404, gerald), //
				updateCustomer(504762686, gerald), //
				updateCustomer(503767531, gerald), //
				updateCustomer(503764156, gerald), //
				updateCustomer(504450723, gerald), //
				updateCustomer(504432700, gerald), //
				updateCustomer(504887316, gerald), //
				updateCustomer(504891520, gerald), //
				updateCustomer(504769668, gerald), //
				updateCustomer(504768607, gerald), //
				updateCustomer(504762685, gerald), //
				updateCustomer(503775126, gerald), //
				updateCustomer(504491823, gerald), //
				updateCustomer(503748194, gerald), //
				updateCustomer(503751366, gerald), //
				updateCustomer(503768216, gerald), //
				updateCustomer(503778495, gerald), //
				updateCustomer(5037558447L, gerald), //
				updateCustomer(503753302, jerome), //
				updateCustomer(503765812, jerome), //
				updateCustomer(504765603, jerome), //
				updateCustomer(503760440, jerome), //
				updateCustomer(505032025, aljon), //
				updateCustomer(503931982, aljon), //
				updateCustomer(503750273, aljon), //
				updateCustomer(505032034, aljon), //
				updateCustomer(505032535, aljon), //
				updateCustomer(505032040, aljon), //
				updateCustomer(505032043, aljon), //
				updateCustomer(505032045, aljon), //
				updateCustomer(504115739, aljon), //
				updateCustomer(503765747, jerome), //
				updateCustomer(503932854, gerald), //
				updateCustomer(503926145, jerome), //
				updateCustomer(504700043, pickUp), //
				updateCustomer(503777380, jerome), //
				updateCustomer(503751803, gerald), //
				updateCustomer(503760206, gerald), //
				updateCustomer(503769092, gerald), //
				updateCustomer(503931903, rommel), //
				updateCustomer(503778662, gerald), //
				updateCustomer(504782122, jerome), //
				updateCustomer(503778214, rommel), //
				updateCustomer(503932701, pickUp), //
				updateCustomer(503759690, jerome), //
				updateCustomer(503901656, jerome), //
				updateCustomer(503770640, jerome), //
				updateCustomer(504666965, gerald), //
				updateCustomer(503778290, gerald), //
				updateCustomer(503839792, gerald), //
				updateCustomer(504404284, gerald), //
				updateCustomer(503748789, aljon), //
				updateCustomer(504994018, pickUp), //
				updateCustomer(5047574331L, rommel), //
				updateCustomer(503756603, rommel), //
				updateCustomer(504426450, gerald), //
				updateCustomer(503901655, gerald), //
				updateCustomer(504121772, gerald), //f
				updateCustomer(504101871, gerald), //
				updateCustomer(503747875, gerald), //
				updateCustomer(503776614, jerome), //
				updateCustomer(504411524, aljon), //
				updateCustomer(503755388, gerald), //
				updateCustomer(503931869, gerald), //
				updateCustomer(503756512, gerald), //
				updateCustomer(503763800, rommel), //
				updateCustomer(504891914, rommel), //
				updateCustomer(503926327, gerald), //
				updateCustomer(504947721, gerald), //
				updateCustomer(504428285, gerald), //
				updateCustomer(503755757, gerald), //
				updateCustomer(503768985, gerald), //
				updateCustomer(8, aljon), //
				updateCustomer(504757430, aljon), //
				updateCustomer(503760171, aljon), //
				updateCustomer(503763493, rommel), //
				updateCustomer(503897623, aljon), //
				updateCustomer(505033154, jerome), //
				updateCustomer(503762015, jerome), //
				updateCustomer(5050324040L, jerome), //
				updateCustomer(503755717, jerome), //
				updateCustomer(503769788, jerome), //
				updateCustomer(503754939, pickUp), //
				updateCustomer(504726952, aljon), //
				updateCustomer(504115745, rommel), //
				updateCustomer(503834485, aljon), //
				updateCustomer(503757868, pickUp), //
				updateCustomer(504716456, gerald), //
				updateCustomer(504107373, aljon), //
				updateCustomer(9, pickUp), //
				updateCustomer(504726957, jerome), //
				updateCustomer(503931772, jerome), //
				updateCustomer(503931979, gerald), //
				updateCustomer(503751559, gerald), //
				updateCustomer(503897622, gerald), //
				updateCustomer(503763746, gerald), //
				updateCustomer(10, gerald), //
				updateCustomer(11, aljon), //
				updateCustomer(503767795, gerald), //
				updateCustomer(504893932, jerome), //
				updateCustomer(504385942, gerald), //
				updateCustomer(503776607, rommel), //
				updateCustomer(503768971, gerald), //
				updateCustomer(503777057, gerald), //
				updateCustomer(504342824, jerome), //
				updateCustomer(503757593, rommel), //
				updateCustomer(504997894, pickUp), //
				updateCustomer(12, pickUp), //
				updateCustomer(15, pickUp), //
				updateCustomer(13, pickUp), //
				updateCustomer(503749230, rommel), //
				updateCustomer(503748963, rommel), //
				updateCustomer(503842520, rommel), //
				updateCustomer(503750031, jerome), //
				updateCustomer(505037052, aljon), //
				updateCustomer(505032036, aljon), //
				updateCustomer(50375965, aljon), //
				updateCustomer(503759730, jerome), //
				updateCustomer(14, aljon), //
				updateCustomer(16, rommel), //
				updateCustomer(17, pickUp), //
				updateCustomer(18, pickUp), //
				updateCustomer(19, gerald), //
				updateCustomer(20, pickUp), //
				updateCustomer(21, pickUp), //
				updateCustomer(22, jerome), //
				updateCustomer(23, pickUp), //
				updateCustomer(504765875, jerome), //
				updateCustomer(503757586, jerome), //
				updateCustomer(503774418, gerald), //
				updateCustomer(503750632, pickUp), //
				updateCustomer(503771342, aljon), //
				updateCustomer(504660544, jerome), //
				updateCustomer(503749165, gerald), //
				updateCustomer(503767648, gerald), //
				updateCustomer(504767931, gerald), //
				updateCustomer(503763444, gerald), //
				updateCustomer(503768161, gerald), //
				updateCustomer(504887315, gerald), //
				updateCustomer(24, gerald), //
				updateCustomer(25, pickUp), //
				updateCustomer(26, pickUp), //
				updateCustomer(27, pickUp), //
				updateCustomer(28, pickUp), //
				updateCustomer(29, pickUp), //
				updateCustomer(504423617, rommel), //
				updateCustomer(504757431, rommel), //
				updateCustomer(30, pickUp), //
				updateCustomer(504012625, gerald), //
				updateCustomer(504777805, gerald), //
				updateCustomer(503747552, gerald), //
				updateCustomer(504414471, gerald), //
				updateCustomer(503747234, aljon), //
				updateCustomer(504743781, aljon), //
				updateCustomer(504107338, gerald), //
				updateCustomer(504408556, pickUp), //
				updateCustomer(503764546, pickUp), //
				updateCustomer(504765881, jerome), //
				updateCustomer(31, jerome), //
				updateCustomer(32, aljon), //
				updateCustomer(503777774, jerome), //
				updateCustomer(503776852, aljon), //
				updateCustomer(503757098, gerald), //
				updateCustomer(504697320, gerald), //
				updateCustomer(503774294, pickUp), //
				updateCustomer(33, pickUp), //
				updateCustomer(34, pickUp), //
				updateCustomer(503762708, pickUp), //
				updateCustomer(504649369, jerome), //
				updateCustomer(503752224, pickUp), //
				updateCustomer(503931977, jerome), //
				updateCustomer(503752566, aljon), //
				updateCustomer(503748253, gerald), //
				updateCustomer(504417008, gerald), //
				updateCustomer(503756221, gerald), //
				updateCustomer(35, pickUp), //
				updateCustomer(36, rommel), //
				updateCustomer(37, pickUp), //
				updateCustomer(38, pickUp), //
				updateCustomer(39, gerald), //
				updateCustomer(40, pickUp), //
				updateCustomer(41, pickUp), //
				updateCustomer(504748112, jerome), //
				updateCustomer(504408558, jerome), //
				updateCustomer(504877693, gerald), //
				updateCustomer(42, jerome), //
				updateCustomer(43, pickUp), //
				updateCustomer(44, pickUp), //
				updateCustomer(503776054, jerome), //
				updateCustomer(503754131, aljon), //
				updateCustomer(504767934, gerald), //
				updateCustomer(504432701, gerald), //
				updateCustomer(503772341, pickUp), //
				updateCustomer(45, gerald), //
				updateCustomer(47, pickUp), //
				updateCustomer(46, pickUp), //
				updateCustomer(48, pickUp), //
				updateCustomer(503753546, aljon), //
				updateCustomer(504716457, rommel), //
				updateCustomer(503759828, jerome), //
				updateCustomer(503748726, pickUp), //
				updateCustomer(49, pickUp), //
				updateCustomer(504981678, pickUp), //
				updateCustomer(504457716, gerald), //
				updateCustomer(50, gerald), //
				updateCustomer(51, pickUp), //
				updateCustomer(52, aljon), //
				updateCustomer(504124377, rommel), //
				updateCustomer(503763952, gerald), //
				updateCustomer(504107398, rommel), //
				updateCustomer(503759265, aljon), //
				updateCustomer(504700040, aljon), //
				updateCustomer(53, gerald), //
				updateCustomer(54, pickUp), //
				updateCustomer(55, pickUp), //
				updateCustomer(56, pickUp), //
				updateCustomer(57, jerome), //
				updateCustomer(58, pickUp), //
				updateCustomer(503757482, pickUp), //
				updateCustomer(503747386, rommel), //
				updateCustomer(59, rommel), //
				updateCustomer(60, pickUp), //
				updateCustomer(61, jerome), //
				updateCustomer(62, aljon), //
				updateCustomer(63, jerome), //
				updateCustomer(64, pickUp), //
				updateCustomer(504777816, pickUp), //
				updateCustomer(504765874, pickUp), //
				updateCustomer(503900641, pickUp), //
				updateCustomer(503774532, pickUp), //
				updateCustomer(503766776, pickUp), //
				updateCustomer(504941030, gerald), //
				updateCustomer(504408561, jerome), //
				updateCustomer(65, pickUp), //
				updateCustomer(503760223, gerald), //
				updateCustomer(503766611, gerald), //
				updateCustomer(66, aljon), //
				updateCustomer(503774431, gerald), //
				updateCustomer(504889386, pickUp), //
				updateCustomer(503769786, pickUp), //
				updateCustomer(503760571, pickUp), //
				updateCustomer(67, gerald), //
				updateCustomer(503838851, aljon), //
				updateCustomer(503747471, jerome), //
				updateCustomer(68, jerome), //
				updateCustomer(69, jerome), //
				updateCustomer(503771837, jerome), //
				updateCustomer(70, jerome), //
				updateCustomer(71, jerome), //
				updateCustomer(505050391, jerome), //
				updateCustomer(504878577, gerald), //
				updateCustomer(503751367, gerald), //
				updateCustomer(504889403, gerald), //
				updateCustomer(504891912, gerald), //
				updateCustomer(72, pickUp), //
				updateCustomer(503775647, jerome), //
				updateCustomer(505052129, jerome), //
				updateCustomer(505050438, gerald), //
				updateCustomer(504012654, pickUp), //
				updateCustomer(503897624, pickUp), //
				updateCustomer(503758129, pickUp), //
				updateCustomer(503755716, gerald), //
				updateCustomer(73, aljon), //
				updateCustomer(504696031, jerome), //
				updateCustomer(503752406, jerome), //
				updateCustomer(504935541, rommel), //
				updateCustomer(505039624, jerome), //
				updateCustomer(505050411, jerome), //
				updateCustomer(75, jerome), //
				updateCustomer(76, pickUp), //
				updateCustomer(77, pickUp), //
				updateCustomer(78, pickUp), //
				updateCustomer(503753184, jerome), //
				updateCustomer(503776234, jerome), //
				updateCustomer(503775854, jerome), //
				updateCustomer(503756199, gerald), //
				updateCustomer(504757259, rommel), //
				updateCustomer(503776226, rommel), //
				updateCustomer(504346123, aljon), //
				updateCustomer(503773868, rommel), //
				updateCustomer(503758936, rommel), //
				updateCustomer(503756429, rommel), //
				updateCustomer(504762392, rommel), //
				updateCustomer(503767078, rommel), //
				updateCustomer(503770782, rommel), //
				updateCustomer(503776793, rommel), //
				updateCustomer(503756147, rommel), //
				updateCustomer(504448584, aljon), //
				updateCustomer(504224443, gerald), //
				updateCustomer(504701239, gerald), //
				updateCustomer(503751659, gerald), //
				updateCustomer(503774095, gerald), //
				updateCustomer(505052130, jerome), //
				updateCustomer(503757108, gerald), //
				updateCustomer(503751097, gerald), //
				updateCustomer(503749922, rommel), //
				updateCustomer(503771550, gerald), //
				updateCustomer(504658338, gerald), //
				updateCustomer(503765237, aljon), //
				updateCustomer(504767932, gerald), //
				updateCustomer(503751025, gerald), //
				updateCustomer(505050437, gerald), //
				updateCustomer(504411758, gerald), //
				updateCustomer(504845598, gerald), //
				updateCustomer(503747504, gerald), //
				updateCustomer(503907264, gerald), //
				updateCustomer(503752675, gerald), //
				updateCustomer(503757852, gerald), //
				updateCustomer(503768698, rommel) //
		);
	}

	private CustomerEntity updateCustomer(long vendorId, RouteEntity route) {
		CustomerEntity c = customerRepository.findByVendorId(vendorId);
		c.setRouteHistory(routings(c, route));
		return c;
	}

	private List<RoutingEntity> routings(CustomerEntity customer, RouteEntity route) {
		List<RoutingEntity> l = findAllRoutings(customer);
		return l == null || l.isEmpty() ? asList(routing(customer, route)) : routings(new ArrayList<>(l), customer, route);
	}

	private List<RoutingEntity> routings(List<RoutingEntity> l, CustomerEntity customer, RouteEntity route) {
		l.set(0, routing(customer, route));
		return l;
	}

	private RoutingEntity routing(CustomerEntity customer, RouteEntity route) {
		RoutingEntity r = routing(customer);
		r.setRoute(route);
		return r;
	}

	private RoutingEntity routing(CustomerEntity customer) {
		RoutingEntity r = latestRouting(customer);
		return r != null ? r : newRouting(customer);
	}

	private RoutingEntity newRouting(CustomerEntity customer) {
		RoutingEntity r = new RoutingEntity();
		r.setCustomer(customer);
		r.setStartDate(goLiveDate());
		return r;
	}

	private void setPickUpToCustomersWithNoAssignedDeliveryRoutes() {
		List<CustomerEntity> customers = customerRepository.findByVendorIdNotNullAndRouteHistoryNull();
		if (customers != null)
			customerRepository.save(customers.stream().map(c -> setRouting(c)).collect(toList()));
	}

	private CustomerEntity setRouting(CustomerEntity c) {
		c.setRouteHistory(routings(c, pickUp));
		return c;
	}

	private void givePickUpDiscountsToSelectedCustomers() {
		createFullsAndEmptiesItemFamilies();
		setItemFamilyAndNotDiscountedValue();
		updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId();
		givePickUpDiscountsToApprovedCustomers();
	}

	private void createFullsAndEmptiesItemFamilies() {
		itemFamilyRepository.save(Arrays.asList(//
				fulls = newItemFamily(BeverageType.FULL_GOODS), //
				empties = newItemFamily(BeverageType.EMPTIES)));
	}

	private ItemFamilyEntity newItemFamily(BeverageType type) {
		ItemFamilyEntity e = new ItemFamilyEntity();
		e.setName(type.toString());
		e.setTier(ItemTier.PRODUCT);
		return e;
	}

	private void setItemFamilyAndNotDiscountedValue() {
		Iterable<ItemEntity> list = itemRepository.findAll();
		List<ItemEntity> items = stream(list.spliterator(), false).map(i -> setItemFamilyAndNotDiscountedValue(i)).collect(toList());
		itemRepository.save(items);
	}

	private ItemEntity setItemFamilyAndNotDiscountedValue(ItemEntity i) {
		i.setFamily(family(i));
		i.setNotDiscounted(notDiscounted(i));
		return i;
	}

	private ItemFamilyEntity family(ItemEntity i) {
		return isNotDiscounted(i) ? empties : fulls;
	}

	private boolean isNotDiscounted(ItemEntity i) {
		return i.getNotDiscounted() == true;
	}

	private Boolean notDiscounted(ItemEntity i) {
		return isNotDiscounted(i) || isMismo(i) ? true : null;
	}

	private boolean isMismo(ItemEntity i) {
		List<String> mismos = Arrays.asList( //
				"104473", "104954", "104952", "105346", "105311", "105506", "105947", "105953", "105428", "104793", "104655", "106148", "106149",
				"105429", "105892", "105718", "105896", "105949", "105418", "105672", "105895", "105720", "104886", "105394", "104955", "104632",
				"104956", "104656", "105909", "104630", "105059", "104866", "106118");
		return mismos.stream().anyMatch(m -> m.equalsIgnoreCase(i.getVendorId()));
	}

	private void updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId() {
		List<Long> obsoleteVendorIds = Arrays.asList(18L, 27L, 30L, 52L);
		List<Long> updatedVendorIds = Arrays.asList(504990464L, 503760889L, 503773439L, 504782124L);
		for (int i = 0; i < obsoleteVendorIds.size(); i++)
			updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId(obsoleteVendorIds.get(i), updatedVendorIds.get(i));
	}

	private void updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId(Long obsoleteId, Long updatedId) {
		List<BillableEntity> list = ocsRepository.findByCustomerVendorId(obsoleteId);
		CustomerEntity c = customerRepository.findByVendorId(updatedId);
		if (list != null && c != null)
			updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId(list, c);
	}

	private void updateBillingToCustomerWithUpdatedVendorIdThenDeleteCustomerWithObsoleteVendorId(List<BillableEntity> list, CustomerEntity c) {
		ocsRepository.save(list.stream().map(e -> updateCustomer(e, c)).collect(toList()));
		//customerRepository.delete(c);
	}

	private BillableEntity updateCustomer(BillableEntity e, CustomerEntity c) {
		e.setCustomer(c);
		return e;
	}

	private void givePickUpDiscountsToApprovedCustomers() {
		List<Long> vendorIds = Arrays.asList(13L, 21L, 26L, 29L, 35L, 504990464L, 503760889L, 503773439L, 504777816L, 504696211L);
		vendorIds.forEach(v -> giveDiscountToCustomer(v));
	}

	private void giveDiscountToCustomer(Long vendorId) {
		CustomerEntity e = customerRepository.findByVendorId(vendorId);
		if (e != null)
			giveDiscountToCustomer(e);
	}

	private void giveDiscountToCustomer(CustomerEntity e) {
		e.setCustomerDiscounts(customerDiscounts(e));
		customerRepository.save(e);
	}

	private List<CustomerDiscountEntity> customerDiscounts(CustomerEntity e) {
		CustomerDiscountEntity d = new CustomerDiscountEntity();
		d.setValue(new BigDecimal("3.00"));
		d.setStartDate(goLiveDate());
		d.setIsValid(true);
		d.setDecidedBy(SYSGEN.toString());
		d.setDecidedOn(ZonedDateTime.now());
		return Arrays.asList(d);
	}

	private LocalDate goLiveDate() {
		return DateTimeUtils.toDate(goLive);
	}

	private SyncEntity updateSync(SyncEntity sync, Date date) {
		sync.setLastSync(date);
		return sync;
	}
}
