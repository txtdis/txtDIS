package ph.txtdis.app;

public abstract class AbstractPercentBasedCustomerDiscountedBillableApp {
	/*
		extends AbstractReturnOrderedBillableApp<RmaService> {
	
	@Autowired
	private AppCombo<String> discountCombo;
	
	@Autowired
	private InvoiceBookletApp invoiceBookletApp;
	
	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		if (service.isAnInvoice())
			b.add(invoiceBookletApp.addButton(this));
		return b;
	}
	
	@Override
	protected List<Node> mainVerticalPaneNodes() {
		buildDisplays();
		List<Node> l = new ArrayList<>(asList(gridPane(), tablePane()));
		if (service.isADeliveryReport())
			l.addAll(asList(discountedTotalPane(), paymentPane()));
		else if (service.isABadOrder())
			addBadOrderPane(l);
		else if (service.isASalesOrder())
			l.addAll(asList(discountedVatPane()));
		else if (service.isAnInvoice())
			l.addAll(asList(vatPane(), discountedPaymentPane()));
		l.add(auditPane());
		return l;
	}
	
	@Override
	protected void buildDisplays() {
		super.buildDisplays();
		discountCombo.readOnlyOfWidth(220);
	}
	
	private Node discountedPaymentPane() {
		return discountPane(paymentNodes());
	}
	
	private Node discountedTotalPane() {
		return discountPane(totalNodes());
	}
	
	private Node paymentPane() {
		return box.forHorizontalPane(paymentNodes());
	}
	
	private Node discountedVatPane() {
		return discountPane(vatNodes());
	}
	
	private Node discountPane(List<Node> nodes) {
		List<Node> list = discountNodes();
		list.addAll(nodes);
		return box.forHorizontalPane(list);
	}
	
	private List<Node> discountNodes() {
		List<Node> l = asList(label.name("Discount"), discountCombo);
		return new ArrayList<>(l);
	}
	
	@Override
	protected void refreshSummaryPane() {
		discountCombo.itemsSelectingFirst(service.getDiscounts());
		super.refreshSummaryPane();
	}
	*/
}
