package ph.txtdis.service;

public abstract class AbstractPercentBasedCustomerDiscountedBillable {
	/*	
			extends AbstractCreditAndInvoiceAndPurchaseAndReturnAndVatTrackedBillableService {
	
		@Autowired
		private ItemFamilyService familyService;
	
		private List<CustomerDiscount> currentlyApprovedDiscounts;
	
		@Override
		public Item verifyItem(Long id) throws Exception {
			Item i = super.verifyItem(id);
			confirmCurrentAndPreviousItemDiscountsAreEqual(i);
			return i;
		}
	
		private void confirmCurrentAndPreviousItemDiscountsAreEqual(Item currentItem) throws DifferentDiscountException {
			if (customerHasNoPercentBasedDiscountsOrItemIsFirstTableEntry(currentItem) || isABadOrder() || isAPurchaseOrder())
				return;
			if (!areCurrentAndPreviousItemDiscountsEqual(currentItem))
				throw new DifferentDiscountException();
		}
	
		private boolean customerHasNoPercentBasedDiscountsOrItemIsFirstTableEntry(Item i) {
			if (getDetails().isEmpty())
				return getCurrentlyApprovedDiscounts(i);
			return noPercentBasedDiscountExists();
		}
	
		private boolean getCurrentlyApprovedDiscounts(Item i) {
			currentlyApprovedDiscounts = getLatestApprovedDiscounts(i);
			get().setDiscountIds(listDiscountIds());
			return true;
		}
	
		private boolean noPercentBasedDiscountExists() {
			return currentlyApprovedDiscounts.isEmpty();
		}
	
		private List<Long> listDiscountIds() {
			return currentlyApprovedDiscounts.stream().map(d -> d.getId()).collect(toList());
		}
	
		private boolean areCurrentAndPreviousItemDiscountsEqual(Item currentItem) {
			List<CustomerDiscount> currentItemDiscounts = getLatestApprovedDiscounts(currentItem);
			List<CustomerDiscount> previousItemDiscounts = currentlyApprovedDiscounts;
			if (previousItemDiscounts.size() != currentItemDiscounts.size())
				return false;
			if (previousItemDiscounts.isEmpty())
				return true;
			return discountValues(previousItemDiscounts).equals(discountValues(currentItemDiscounts));
		}
	
		private List<BigDecimal> discountValues(List<CustomerDiscount> l) {
			return l.stream().map(d -> d.getDiscount()).collect(toList());
		}
	
		@Override
		protected void nullifyAll() {
			super.nullifyAll();
			currentlyApprovedDiscounts = null;
		}
	
		@Override
		protected List<String> getDiscountTextList() {
			return noPercentBasedDiscountExists() ? null : createDiscountTextList();
		}
	
		private List<String> createDiscountTextList() {
			if (isBilling() && !getDetails().isEmpty())
				recomputeDiscounts();
			BigDecimal totalDiscount = getTotalPercentageBasedDiscountValue();
			return isZero(totalDiscount) ? null : createDiscountTextList(totalDiscount);
		}
	
		private void recomputeDiscounts() {
			try {
				Item i = itemService.find(getDetails().get(0).getId());
				currentlyApprovedDiscounts = getLatestApprovedDiscounts(i);
			} catch (Exception e) {
				currentlyApprovedDiscounts = Collections.emptyList();
			}
		}
	
		private List<CustomerDiscount> getLatestApprovedDiscounts(Item i) {
			try {
				List<ItemFamily> l = familyService.getItemAncestry(i);
				ItemFamily f = getHighestTierAmongApprovedDiscountFamilyLimits(l);
				return getLatestApprovedDiscounts(f);
			} catch (Exception e) {
				return Collections.emptyList();
			}
		}
	
		private ItemFamily getHighestTierAmongApprovedDiscountFamilyLimits(List<ItemFamily> families) {
			try {
				return getLatestApprovedCustomerDiscountStream()//
						.filter(cd -> families.contains(cd.getFamilyLimit()))//
						.map(cd -> cd.getFamilyLimit()).max(ItemFamily::compareTo).get();
			} catch (Exception e) {
				return null;
			}
		}
	
		private List<CustomerDiscount> getLatestApprovedDiscounts(ItemFamily f) {
			return getLatestApprovedFamilyFilteredCustomerDiscountStream(f)
					.filter(cd -> cd.getStartDate().isEqual(getStartDateOfLatestDiscount(f))).collect(toList());
		}
	
		private Stream<CustomerDiscount> getLatestApprovedFamilyFilteredCustomerDiscountStream(ItemFamily family) {
			return getLatestApprovedCustomerDiscountStream().filter(cd -> Util.areEqual(cd.getFamilyLimit(), family));
		}
	
		private LocalDate getStartDateOfLatestDiscount(ItemFamily family) {
			try {
				return getLatestApprovedFamilyFilteredCustomerDiscountStream(family).max(CustomerDiscount::compareTo).get()
						.getStartDate();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
	
		private List<String> createDiscountTextList(BigDecimal totalDiscount) {
			ArrayList<String> l = new ArrayList<>();
			if (currentlyApprovedDiscounts.size() > 1)
				l.add(getTotalInText(totalDiscount));
			return getEachLevelDiscountTextList(l);
		}
	
		private String getTotalInText(BigDecimal t) {
			return "[TOTAL] " + toCurrencyText(t);
		}
	
		private List<String> getEachLevelDiscountTextList(List<String> list) {
			BigDecimal net = getGross();
			BigDecimal total = ZERO;
			for (CustomerDiscount d : currentlyApprovedDiscounts) {
				BigDecimal perLevel = net.multiply(toPercentRate(d.getDiscount()));
				total = total.add(perLevel);
				net = net.subtract(total);
				list.add("[" + d.getLevel() + ": " + d.getDiscount() + "%] " + toCurrencyText(perLevel));
			}
			return list;
		}
	
		@Override
		protected BigDecimal getTotalPercentageBasedDiscountValue() {
			BigDecimal discount = ZERO;
			if (currentlyApprovedDiscounts != null) {
				BigDecimal gross = getGross();
				for (CustomerDiscount d : currentlyApprovedDiscounts) {
					discount = discount.add(gross.multiply(toPercentRate(d.getDiscount())));
					gross = gross.subtract(discount);
				}
			}
			return discount;
		}
		*/
}
