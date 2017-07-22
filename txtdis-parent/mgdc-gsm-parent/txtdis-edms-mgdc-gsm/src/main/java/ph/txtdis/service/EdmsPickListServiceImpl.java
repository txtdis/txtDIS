package ph.txtdis.service;

import static java.math.BigDecimal.valueOf;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static ph.txtdis.util.Code.PICKING_PREFIX;
import static ph.txtdis.util.Code.RECEIVING_PREFIX;
import static ph.txtdis.util.DateTimeUtils.toTimestampWithSecondText;
import static ph.txtdis.util.NumberUtils.divide;
import static ph.txtdis.util.NumberUtils.isPositive;
import static ph.txtdis.util.NumberUtils.remainder;
import static ph.txtdis.util.NumberUtils.toWholeNo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ph.txtdis.domain.EdmsIncomingLoad;
import ph.txtdis.domain.EdmsIncomingLoadDetail;
import ph.txtdis.domain.EdmsItem;
import ph.txtdis.domain.EdmsLoadOrder;
import ph.txtdis.domain.EdmsLoadOrderDetail;
import ph.txtdis.domain.EdmsSeller;
import ph.txtdis.domain.EdmsTruck;
import ph.txtdis.dto.PickList;
import ph.txtdis.dto.PickListDetail;
import ph.txtdis.repository.EdmsIncomingLoadDetailRepository;
import ph.txtdis.repository.EdmsIncomingLoadRepository;
import ph.txtdis.repository.EdmsLoadOrderDetailRepository;
import ph.txtdis.repository.EdmsLoadOrderRepository;
import ph.txtdis.util.Code;

@Transactional
@Service("pickListService")
public class EdmsPickListServiceImpl //
		implements EdmsPickListService {

	@Autowired
	private EdmsIncomingLoadRepository incomingLoadRepository;

	@Autowired
	private EdmsIncomingLoadDetailRepository incomingLoadDetailRepository;

	@Autowired
	private EdmsLoadOrderRepository loadOrderRepository;

	@Autowired
	private EdmsLoadOrderDetailRepository loadOrderDetailRepository;

	@Autowired
	private AutoNumberService autoNumberService;

	@Autowired
	private DriverService driverService;

	@Autowired
	private EdmsSellerService sellerService;

	@Autowired
	private EdmsTruckService truckService;

	@Autowired
	private EdmsWarehouseService warehouseService;

	@Autowired
	private HelperService helperService;

	@Autowired
	private EdmsItemService itemService;

	@Value("${transaction.code}")
	private String transactionCode;

	@Value("${client.user}")
	private String userName;

	@Override
	public PickList save(PickList p) {
		List<EdmsIncomingLoadDetail> l = incomingLoadDetailRepository.findByReferenceNo(incomingLoadNo(p));
		if (l == null || l.isEmpty())
			saveNewLoadOrdersAndIncomingLoadReports(p);
		else
			updateIncomingLoadReport(l, p);
		return p;
	}

	private void saveNewLoadOrdersAndIncomingLoadReports(PickList p) {
		saveNewLoadOrders(p);
		saveNewIncomingLoadReports(p);
	}

	private void saveNewLoadOrders(PickList p) {
		saveLoadOrder(p);
		p.getDetails().forEach(d -> saveLoadOrderDetail(p, d));
		saveLoadOrderAutoNo(p);
	}

	private void saveLoadOrder(PickList p) {
		EdmsLoadOrder e = new EdmsLoadOrder();
		e.setReferenceNo(loadOrderNo(p));
		e.setStatus(Code.CLOSED);
		e.setOrderDate(p.getPickDate());
		e.setSellerCode(sellerCode(p));
		e.setTruckCode(truckCode(p));
		e.setPlateNo(plateNo(p));
		e.setDriverCode(driverService.getName(p));
		e.setHelperCode(helperService.getName(p));
		e.setWarehouseCode(warehouseService.getMainCode());
		e.setCreatedBy(userName);
		e.setCreatedOn(toTimestampWithSecondText(p.getCreatedOn()));
		e.setPostedRemittanceVariance(Code.TRUE);
		loadOrderRepository.save(e);
	}

	private String loadOrderNo(PickList p) {
		return PICKING_PREFIX + transactionCode + p.getId();
	}

	private String sellerCode(PickList p) {
		EdmsSeller s = sellerService.findByUsername(p.getLeadAssistant());
		return s == null ? "" : s.getName();
	}

	private String truckCode(PickList p) {
		EdmsTruck t = truckService.findEntityByPlateNo(plateNo(p));
		return t == null ? "" : t.getCode();
	}

	public String plateNo(PickList p) {
		String no = p.getTruck();
		return no == null ? "" : no;
	}

	private void saveLoadOrderDetail(PickList p, PickListDetail d) {
		String loadOrderNo = loadOrderNo(p);
		saveLoadedCases(loadOrderNo, d);
		saveLoadedBottles(loadOrderNo, d);
	}

	private void saveLoadedCases(String loadOrderNo, PickListDetail d) {
		BigDecimal cases = loadedCases(d);
		if (isPositive(cases))
			saveLoadOrderDetail(d, loadOrderNo, Code.CS, cases);
	}

	private BigDecimal loadedCases(PickListDetail d) {
		return toWholeNo(divide(d.getPickedQty(), new BigDecimal(d.getQtyPerCase())));
	}

	private void saveLoadedBottles(String loadOrderNo, PickListDetail d) {
		BigDecimal bottles = loadedBottles(d);
		if (isPositive(bottles))
			saveLoadOrderDetail(d, loadOrderNo, Code.BTL, bottles);
	}

	private BigDecimal loadedBottles(PickListDetail d) {
		return remainder(d.getPickedQty(), new BigDecimal(d.getQtyPerCase()));
	}

	private void saveLoadOrderDetail(PickListDetail d, String loadOrderNo, String uom, BigDecimal qty) {
		EdmsLoadOrderDetail e = new EdmsLoadOrderDetail();
		e.setReferenceNo(loadOrderNo);
		e.setItemCode(d.getItemVendorNo());
		e.setItemName(itemName(d));
		e.setQty(qty);
		e.setUomCode(uom);
		loadOrderDetailRepository.save(e);
	}

	private String itemName(PickListDetail d) {
		EdmsItem i = itemService.getItem(d);
		return i == null ? null : i.getName();
	}

	private void saveLoadOrderAutoNo(PickList p) {
		saveAutoNo(p, PICKING_PREFIX);
	}

	private void saveAutoNo(PickList p, String prefix) {
		autoNumberService.saveAutoNo(prefix, getAutoNo(p.getId()));
	}

	private String getAutoNo(Long id) {
		return Code.addZeroes(id.toString());
	}

	private void saveNewIncomingLoadReports(PickList p) {
		saveIncomingLoad(p);
		p.getDetails().forEach(d -> saveIncomingLoadDetail(p, d));
		saveIncomingLoadAutoNo(p);
	}

	private void saveIncomingLoad(PickList p) {
		EdmsIncomingLoad e = new EdmsIncomingLoad();
		e.setReferenceNo(incomingLoadNo(p));
		e.setStatus(Code.CLOSED);
		e.setOrderDate(p.getPickDate());
		e.setSellerCode(sellerCode(p));
		e.setTruckCode(truckCode(p));
		e.setPlateNo(plateNo(p));
		e.setDriverCode(driverService.getName(p));
		e.setHelperCode(helperService.getName(p));
		e.setWarehouseCode(warehouseService.getMainCode());
		e.setCreatedBy(userName);
		e.setCreatedOn(toTimestampWithSecondText(p.getCreatedOn()));
		e.setPostedRemittanceVariance(Code.TRUE);
		incomingLoadRepository.save(e);
	}

	private String incomingLoadNo(PickList p) {
		return RECEIVING_PREFIX + transactionCode + p.getId();
	}

	private void saveIncomingLoadDetail(PickList p, PickListDetail d) {
		EdmsIncomingLoadDetail e = new EdmsIncomingLoadDetail();
		e.setReferenceNo(incomingLoadNo(p));
		e.setItemCode(d.getItemVendorNo());
		e.setItemName(itemName(d));
		incomingLoadDetailRepository.save(e);
	}

	private void saveIncomingLoadAutoNo(PickList p) {
		saveAutoNo(p, RECEIVING_PREFIX);
	}

	private void updateIncomingLoadReport(List<EdmsIncomingLoadDetail> l, PickList p) {
		Map<String, EdmsIncomingLoadDetail> m = l.stream().collect(toMap(EdmsIncomingLoadDetail::getItemCode, identity()));
		for (PickListDetail d : p.getDetails())
			updateQty(m, d);
	}

	private void updateQty(Map<String, EdmsIncomingLoadDetail> m, PickListDetail d) {
		EdmsIncomingLoadDetail e = m.get(d.getItemVendorNo());
		e.setFullCaseQty(cases(e, d));
		e.setFullBottleQty(bottles(e, d));
		incomingLoadDetailRepository.save(e);
	}

	private BigDecimal cases(EdmsIncomingLoadDetail e, PickListDetail d) {
		BigDecimal qty = toWholeNo(d.getPickedQtyInCases());
		return e.getFullCaseQty().add(qty);
	}

	private BigDecimal bottles(EdmsIncomingLoadDetail e, PickListDetail d) {
		BigDecimal qty = remainder(d.getPickedQty(), valueOf(d.getQtyPerCase()));
		return e.getFullBottleQty().add(qty);
	}
}
