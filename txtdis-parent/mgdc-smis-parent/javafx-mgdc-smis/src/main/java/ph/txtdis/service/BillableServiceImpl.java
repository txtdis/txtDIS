package ph.txtdis.service;

import org.springframework.stereotype.Service;

@Service("billableService")
public class BillableServiceImpl {
	/*
	extends AbstractPercentBasedCustomerDiscountedBillable}
		implements LimitedBadOrderAllowance, SalesforceUploadService<SalesforceSalesInfo> {
	
	@Autowired
	private ReadOnlyService<SalesforceSalesInfo> salesforceService;
	
	@Autowired
	private SalesInfoUploader salesInfoUploader;
	
	private BigDecimal remainingBadOrderAllowance;
	
	private List<VolumeDiscount> itemVolumeDiscounts;
	
	@Override
	public BigDecimal getRemainingBadOrderAllowance() {
		if (isNew())
			return remainingBadOrderAllowance;
		BigDecimal badOrderAllowance = get().getBadOrderAllowanceValue();
		if (badOrderAllowance == null)
			return null;
		return badOrderAllowance.subtract(get().getTotalValue());
	}
	
	@Override
	public String getUploadedBy() {
		return get().getUploadedBy();
	}
	
	@Override
	public ZonedDateTime getUploadedOn() {
		return get().getUploadedOn();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void upload() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			RestException, InvalidException, NotFoundException {
		List<SalesforceAccount> a = ((SalesforceUploadService<SalesforceAccount>) customerService).forUpload();
		if (a != null)
			salesInfoUploader.accounts(a);
		List<SalesforceSalesInfo> i = forUpload();
		if (i != null)
			salesInfoUploader.invoices(i);
		saveUploadedData(salesInfoUploader.start());
	}
	
	@Override
	public List<SalesforceSalesInfo> forUpload() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		return salesforceService.module("salesforceSalesInfo").getList();
	}
	
	@Override
	public void saveUploadedData(List<? extends SalesforceEntity> list) throws NoServerConnectionException,
			StoppedServerException, FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		List<SalesforceAccount> a = new ArrayList<>();
		for (SalesforceEntity entity : list)
			if (entity instanceof SalesforceAccount)
				a.add((SalesforceAccount) entity);
			else
				saveBilling(entity);
	}
	
	private void saveBilling(SalesforceEntity entity) throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotFoundException {
		Billable b = find(entity.getIdNo());
		b.setUploadedBy(username());
		b.setUploadedOn(entity.getUploadedOn());
		save(b);
	}
	
	@Override
	public void setQtyUponValidation(UomType uom, BigDecimal qty) throws Exception {
		if (!isABadOrder())
			return;
		BigDecimal subtotal = getPrice(uom, qty).multiply(qty);
		BigDecimal balance = remainingBadOrderAllowance.subtract(subtotal);
		if (balance.compareTo(ZERO) <= 0)
			throw new InsufficientBadOrderAllowanceException();
		remainingBadOrderAllowance = balance;
		super.setQtyUponValidation(uom, qty);
	}
	
	private BigDecimal getPrice(UomType uom, BigDecimal qty) {
		BigDecimal qtyPerUom = getQtyPerUom(uom);
		BigDecimal discountedPrice = primaryUomPrice;
		if (itemService.getLatestVolumeDiscount(customer.getChannel(), getOrderDate()) != null)
			discountedPrice = getVolumeDiscountValue(qty.multiply(qtyPerUom));
		return discountedPrice.multiply(qtyPerUom);
	}
	
	private BigDecimal getVolumeDiscountValue(BigDecimal qty) {
		setItemVolumeDiscounts();
		if (itemVolumeDiscounts.get(0).getType() == VolumeDiscountType.TIER)
			return getVolumeDiscountOfTierTypePrice(qty);
		return getVolumeDiscountOfSetTypePrice(qty);
	}
	
	private void setItemVolumeDiscounts() {
		List<VolumeDiscount> list = item.getVolumeDiscounts();
		LocalDate date = list.stream().filter(vd -> isApprovedAndStartDateIsNotInTheFuture(vd, getOrderDate()))
				.max(VolumeDiscount::compareTo).get().getStartDate();
		itemVolumeDiscounts = list.stream().filter(vd -> vd.getStartDate().isEqual(date)).collect(toList());
	}
	
	private BigDecimal getVolumeDiscountOfTierTypePrice(BigDecimal qty) {
		BigDecimal unitDiscount = ZERO;
		itemVolumeDiscounts.sort(compareTierTypeVolumeDiscountChannelLimitsThenReverseCutOff());
		for (VolumeDiscount vd : itemVolumeDiscounts)
			if (getCutOff(vd).compareTo(qty) <= 0 && (areChannelLimitsEqual(vd) || isAnAllChannelVolumeDiscount(vd))) {
				unitDiscount = vd.getDiscount().divide(getQtyPerUom(vd.getUom()), 8, RoundingMode.HALF_EVEN);
				break;
			}
		return primaryUomPrice.subtract(unitDiscount);
	}
	
	private Comparator<VolumeDiscount> compareTierTypeVolumeDiscountChannelLimitsThenReverseCutOff() {
		return (a, b) -> reverseCompareCutOffsWhenChannelLimitsAreEqual(a, b);
	}
	
	private int reverseCompareCutOffsWhenChannelLimitsAreEqual(VolumeDiscount a, VolumeDiscount b) {
		int comp = compareVolumeDiscountChannelLimits(a, b);
		return comp != 0 ? comp : valueOf(b.getCutoff()).compareTo(valueOf(a.getCutoff()));
	}
	
	private int compareVolumeDiscountChannelLimits(VolumeDiscount d1, VolumeDiscount d2) {
		Channel c1 = d1.getChannelLimit();
		Channel c2 = d2.getChannelLimit();
		if (c1 == null)
			return c2 == null ? 0 : 1;
		return c2 == null ? -1 : c1.getName().compareTo(c2.getName());
	}
	
	private BigDecimal getCutOff(VolumeDiscount vd) {
		BigDecimal cutoff = new BigDecimal(vd.getCutoff());
		BigDecimal qtyPerUom = getQtyPerUom(vd.getUom());
		return cutoff.multiply(qtyPerUom);
	}
	
	private boolean areChannelLimitsEqual(VolumeDiscount vd) {
		return areEqual(vd.getChannelLimit(), customer.getChannel());
	}
	
	private boolean isAnAllChannelVolumeDiscount(VolumeDiscount vd) {
		return vd.getChannelLimit() == null;
	}
	
	private BigDecimal getVolumeDiscountOfSetTypePrice(BigDecimal qty) {
		BigDecimal discount = ZERO;
		itemVolumeDiscounts.sort(compareSetTypeVolumeDiscountChannelLimits());
		for (VolumeDiscount vd : itemVolumeDiscounts)
			if (areChannelLimitsEqual(vd) || isAnAllChannelVolumeDiscount(vd)) {
				BigDecimal discountSet = qty.divideToIntegralValue(getCutOff(vd));
				discount = discountSet.multiply(vd.getDiscount());
				break;
			}
		BigDecimal net = qty.multiply(primaryUomPrice).subtract(discount);
		return net.divide(qty, 8, HALF_EVEN);
	}
	
	private Comparator<VolumeDiscount> compareSetTypeVolumeDiscountChannelLimits() {
		return (a, b) -> compareVolumeDiscountChannelLimits(a, b);
	}
	
	@Override
	public void updateUponIdNoValidation(Long id) {
	}
	
	@Override
	protected void verifyCustomerHasBadOrderReturnAllowance() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException, NotAllowedToReturnBadOrderException {
		if (isABadOrder() && hasLessThanAHundredBadOrderAllowance())
			throw new NotAllowedToReturnBadOrderException(customer);
	}
	
	private boolean hasLessThanAHundredBadOrderAllowance() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		remainingBadOrderAllowance = ZERO;
		BigDecimal badOrder = totalValue("badOrders");
		BigDecimal netRevenue = netRevenue();
		remainingBadOrderAllowance = divide(netRevenue, HUNDRED).subtract(badOrder);
		get().setBadOrderAllowanceValue(remainingBadOrderAllowance);
		return remainingBadOrderAllowance.compareTo(HUNDRED) < 0;
	}
	
	private BigDecimal netRevenue() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, RestException, InvalidException {
		BigDecimal sold = totalValue("billed");
		BigDecimal returned = totalValue("returnOrders");
		return sold.subtract(returned);
	}
	
	@Override
	protected void nullifyAll() {
		super.nullifyAll();
		remainingBadOrderAllowance = null;
		itemVolumeDiscounts = null;
	}
	
	@Override
	protected BigDecimal getPrice(BillableDetail sd) {
		return getPrice(sd.getUom(), sd.getQty());
	}
	*/
}
