package ph.txtdis.app;

import ph.txtdis.service.RmaService;

public abstract class AbstractReturnOrderedBillableApp<AS extends RmaService> {
	/*
	extends AbstractBillableApp<AS, Long> {
	
	@Autowired
	private AppButton disposalButton, paymentButton;
	
	@Autowired
	private ReturnPaymentDialog paymentDialog;
	
	@Autowired
	protected AppButton receiptButton, replacementButton;
	
	private BooleanProperty returnIsValid;
	
	@Override
	protected List<AppButton> addButtons() {
		List<AppButton> b = new ArrayList<>(super.addButtons());
		if (service.isABadOrder() || service.isAReturnOrder())
			b.addAll(returnItemButtons());
		return b;
	}
	
	@Override
	protected void buildButttons() {
		super.buildButttons();
		disposalButton.icon("disposal").tooltip("Dispose...").build();
		paymentButton.icon("cheque").tooltip("Pay...").build();
		receiptButton.icon(returnReceiptButtonIconName()).tooltip("Receive...").build();
		replacementButton.icon("returnOrder").tooltip("Replace...").build();
	}
	
	private String returnReceiptButtonIconName() {
		if (service.isABadOrder())
			return "badOrderReceipt";
		return "returnReceipt";
	}
	
	protected List<AppButton> returnItemButtons() {
		List<AppButton> l = new ArrayList<>(asList(receiptButton, invalidateButton, paymentButton));
		if (service.isABadOrder())
			l.add(2, disposalButton);
		return l;
	}
	
	@Override
	public void refresh() {
		super.refresh();
		returnIsValid.set(service.isReturnValid());
	}
	
	@Override
	protected void setBooleanProperties() {
		super.setBooleanProperties();
		returnIsValid = new SimpleBooleanProperty(service.isReturnValid());
	}
	
	@Override
	protected void setButtonBindings() {
		super.setButtonBindings();
		disposalButton.disableIf(notValidReturn()//
				.or(isOffSite)//
				.or(notReceived())//
				.or(modifiedReceivingOnDisplay.isNotEmpty()));
		paymentButton.disableIf(notValidReturn()
				.or(when(isABadOrder())//
						.then(modifiedReceivingOnDisplay.isEmpty())//
						.otherwise(notReceived()))//
				.or(billedOnDisplay.isNotEmpty()));
		replacementButton.disableIf(notValidReturn()
				.or(when(isABadOrder())//
						.then(modifiedReceivingOnDisplay.isEmpty())//
						.otherwise(notReceived()))//
				.or(billedOnDisplay.isNotEmpty()));
		receiptButton.disableIf(notValidReturn()//
				.or(printedOnDisplay.isEmpty())//
				.or(receivedOnDisplay.isNotEmpty()));
	}
	
	@Override
	protected BooleanBinding invalidateButtonNeedsToBeDisabled() {
		return notValidReturn().or(super.invalidateButtonNeedsToBeDisabled());
	}
	
	private BooleanBinding notValidReturn() {
		return returnIsValid.not();
	}
	
	@Override
	protected void setListeners() {
		super.setListeners();
		disposalButton.setOnAction(e -> saveDisposalData());
		paymentButton.setOnAction(e -> saveInputtedItemReturnPaymentData());
		receiptButton.setOnAction(e -> saveItemReturnReceiptData());
	}
	
	private void saveDisposalData() {
		try {
			((BadOrderedBillableService) service).saveDisposalData();
			save();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}
	
	private void saveInputtedItemReturnPaymentData() {
		paymentDialog.addParent(this).start();
		LocalDate d = paymentDialog.getAddedItem();
		saveItemReturnPaymentData(d);
	}
	
	private void saveItemReturnPaymentData(LocalDate d) {
		if (d != null) {
			((ItemReturnableBillableService) service).setItemReturnPaymentData(d);
			save();
		} else
			((ItemReturnableBillableService) service).clearItemReturnPaymentDataSetByItsInputDialogDuringDataEntry();
	}
	
	private void saveItemReturnReceiptData() {
		try {
			((ItemReturnableBillableService) service).saveItemReturnReceiptData();
			save();
		} catch (Exception e) {
			showErrorDialog(e);
		}
	}
	*/
}
