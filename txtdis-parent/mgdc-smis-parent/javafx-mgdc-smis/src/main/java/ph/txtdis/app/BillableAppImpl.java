package ph.txtdis.app;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component("billableApp")
public class BillableAppImpl {
	/*
	extends AbstractPercentBasedCustomerDiscountedBillableApp {
	
	@Autowired
	private AppButton uploadButton;
	
	@Autowired
	private AppField<BigDecimal> badOrderAllowanceDisplay;
	
	@Autowired
	private AppField<BigDecimal> remainingBadOrderAllowanceDisplay;
	
	@Autowired
	private SalesforceUploadApp salesforce;
	
	@Override
	public void refresh() {
		super.refresh();
		refreshBadOrderAllowanceNodes();
	}
	
	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		if (service.isAnInvoice() || service.isADeliveryReport())
			b.add(uploadButton);
		return b;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	protected void buildButttons() {
		super.buildButttons();
		uploadButton = salesforce.service((SalesforceUploadService<SalesforceSalesInfo>) service).stage(this)
				.addUploadButton();
	}
	
	@Override
	protected void addBadOrderPane(List<Node> l) {
		l.addAll(asList(badOrderAllowancePane(), returnedItemPane()));
	}
	
	private Node badOrderAllowancePane() {
		List<Node> l = badOrderAllowanceNodes();
		l.addAll(vatNodes());
		l.addAll(remainingBadOrderAllowanceNodes());
		return box.forHorizontalPane(l);
	}
	
	private List<Node> badOrderAllowanceNodes() {
		List<Node> l = asList(//
				label.name("Bad Order Allowance"), //
				badOrderAllowanceDisplay.readOnly().build(CURRENCY));
		return new ArrayList<>(l);
	}
	
	private List<Node> remainingBadOrderAllowanceNodes() {
		return asList(//
				label.name("Balance"), remainingBadOrderAllowanceDisplay.readOnly().build(CURRENCY));
	}
	
	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		uploadButton.disableIf(isOffSite);
	}
	
	@Override
	protected void refreshSummaryPane() {
		super.refreshSummaryPane();
		refreshRemainingBadOrderAllowance();
	}
	
	@Override
	protected void refreshCustomerRelatedInputs() {
		super.refreshCustomerRelatedInputs();
		refreshBadOrderAllowanceNodes();
	}
	
	private void refreshBadOrderAllowanceNodes() {
		badOrderAllowanceDisplay.setValue(service.getBadOrderAllowanceValue());
		refreshRemainingBadOrderAllowance();
	}
	
	private void refreshRemainingBadOrderAllowance() {
		remainingBadOrderAllowanceDisplay.setValue(((LimitedBadOrderAllowance) service).getRemainingBadOrderAllowance());
	}
	*/
}
