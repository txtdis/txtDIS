package ph.txtdis.mgdc.ccbpi.app;

// @Scope("prototype")
//@Component("orderConfirmationApp")
public class BillableAppImpl {//
	// extends AbstractBillableApp<OrderConfirmationService, OrderConfirmationTable, Long> 
	// implements OrderConfirmationApp {
	/*
		private static final String ORDER_NO = "Order No. ";
	
		private static final String ORDER_DATE = "Order Date ";
	
		private static final String CUSTOMER_NO = "Customer No. ";
	
		private static final String FOR = "\nfor ";
	
		private static final String FORMAT_IS = "Format is: ";
	
		private static final String SHIPMENT_ID = "12345678";
	
		private static final String CUSTOMER_ID = SHIPMENT_ID + "9";
	
		private static final String SHIPMENT_NO = "Shipment No. ";
	
		@Autowired
		private AppCombo<String> routeCombo, typeCombo;
	
		@Autowired
		private LocalDatePicker dueDatePicker;
	
		@Autowired
		private NewCustomerDialog newCustomerDialog;
	
		@Autowired
		private TotaledTableApp<BillableDetail> totaledTableApp;
	
		// @Override
		protected void addressAndOrRemarksGridLines() {
		}
	
		// @Override
		protected void customerGridLine() {
			// customerWithDueDateGridLine();
		}
	
		//@Override
		protected Node dueDateNode() {
			return null; //stackPane(super.dueDateNode(), dueDatePicker);
		}
	
		@Override
		protected String getDialogInput() {
			openByIdDialog //
					.idPrompt(CUSTOMER_NO + "+ " + ORDER_DATE + "+ " + ORDER_NO) //
					.header(openByIdDialogHeader()) //
					.prompt(openByIdDialogPrompt()) //
					.addParent(this).start();
			return openByIdDialog.getId();
		}
	
		@Override
		protected List<Node> mainVerticalPaneNodes() {
			buildDisplays();
			return Arrays.asList( //
					gridPane(), //
					totaledTableApp.addNoSubHeadTablePane(table), //
					trackedPane());
		}
	
		@Override
		protected String openByIdDialogPrompt() {
			return FORMAT_IS + CUSTOMER_ID + "-20080808/1" + FOR + CUSTOMER_NO + CUSTOMER_ID + ", " + ORDER_DATE +
			"8/8/08 & "
					+ ORDER_NO + "1";
		}
	
		@Override
		protected Node orderDateNode() {
			return stackPane(orderDateDisplay, orderDatePicker);
		}
	
		@Override
		public void refresh() {
			super.refresh();
			dueDatePicker.setValue(service.getDueDate());
			routeCombo.items(service.listRoutes());
			//typeCombo.items(service.listTypes());
			updateSummaries();
		}
	
		@Override
		protected void reset() {
			super.reset();
			//		if (service.isADeliveryListOrAnOrderConfirmationOrALoadManifest())
			//			orderDatePicker.requestFocus();
			//		else
			//			referenceOrderNoInput.requestFocus();
		}
	
		// TODO @Override
		protected void rrGridNodes() {
			// TODO receivingReferenceNodes(3);
		}
	
		@Override
		protected void setBindings() {
			super.setBindings();
			// TODO routeCombo.disableIf(idNoInput.isEmpty());
			typeCombo.disableIf(routeCombo.isEmpty());
			dueDatePicker.disableIf(routeCombo.isEmpty());
			dueDatePicker.visibleProperty().bind(isPosted().not());
			dueDateDisplay.visibleProperty().bind(dueDatePicker.visibleProperty().not());
			// TODO customerIdInput.disableProperty().unbind();
			// TODO customerIdInput.disableIf(dueDatePicker.isEmpty().or(isPosted()).or(customerNameDisplay.isNotEmpty
			()));
		}
	
		@Override
		protected void setButtonBindings() {
			// TODO super.setButtonBindings();
			// TODO customerSearchButton.visibleProperty().unbind();
			// TODO customerSearchButton.setVisible(false);
			saveButton.disableProperty().unbind();
			saveButton.disableIf(isPosted().or(table.isEmpty()));
		}
	
		@Override
		public void setFocus() {
			newButton.requestFocus();
		}
	
		@Override
		protected void setInputFieldBindings() {
			super.setInputFieldBindings();
			remarksDisplay.editableIf(createdOnDisplay.isEmpty()); // TODO .and(isAnOrderConfirmation()));
		}
	
		@Override
		protected void setListeners() {
			super.setListeners();
			//dueDatePicker.onAction(e -> service.setDueDate(dueDatePicker.getValue()));
			// referenceOrderNoInput.onAction(e -> updateUponReferenceIdValidation());
			//routeCombo.onAction(e -> service.setRoute(routeCombo.getValue()));
			//typeCombo.onAction(e -> service.setType(typeCombo.getValue()));
		}
	
		private void updateUponReferenceIdValidation() {
			//		String id = referenceOrderNoInput.getValue();
			//		if (service.isNew() && id != null && !id.trim().isEmpty())
			//			try {
			//				service.updateUponReferenceOrderNoValidation(id);
			//			} catch (Exception e) {
			//				handleError(e, referenceOrderNoInput);
			//			} finally {
			//				refresh();
			//			}
		}
	
		@Override
		protected void setTableBindings() {
			// TODO table.disableIf(when(isAnOrderReturn()).then(orderDateDisplay.isEmpty()).otherwise
			(customerNameDisplay.isEmpty()));
		}
	
		@Override
		public void start() {
			//if (service.isADeliveryListOrAnOrderConfirmation())
			totaledTableApp.addTotalDisplays(1);
			super.start();
		}
	
		// TODO @Override
		protected void updateSummaries() {
			//if (service.isADeliveryListOrAnOrderConfirmation())
			totaledTableApp.refresh(service);
		}
	
		// TODO @Override
		protected void updateUponCustomerValidation() {
			Long id = null; // TODO customerIdInput.getValue();
			if (// TODO isNew() && 
			id != 0)
				try {
					//service.updateUponCustomerVendorIdValidation(id);
					//} catch (NotFoundException e) {
					//showCreateNewOutletOrExitDialog(e, id);
				} catch (Exception e) {
					// TODO handleError(e, customerIdInput);
				} finally {
					// TODO refreshCustomerRelatedInputs();
					// TODO 	setFocusAfterCustomerValidation();
				}
		}
	
		private void showCreateNewOutletOrExitDialog(Exception x, Long id) {
			messageDialog.showOption(x.getMessage(), "Create", "Exit");
			messageDialog.setOnOptionSelection(e -> openAddNewOutletDialog(id));
			// TODO messageDialog.setOnDefaultSelection(e -> resetCustomerData());
			messageDialog.addParent(this).start();
		}
	
		private void openAddNewOutletDialog(Long id) {
			newCustomerDialog.vendorId(id).addParent(this).start();
			messageDialog.close();
			// TODO service.setCustomer(newCustomerDialog.getCustomer());
			// TODO service.setCustomerRelatedData();
		}
	
		@Override
		protected void topGridLine() {
			dateGridNodes();
			gridPane.add(label.field("Type"), 7, 0);
			gridPane.add(typeCombo, 8, 0);
		}
	
		// TODO  @Override
		protected void updateUponDateValidation(LocalDate d) {
			// TODO if (isNew() && d != null && (service.isADeliveryListOrAnOrderConfirmationOrALoadManifest()))
			setOrderDateUponValidation(d);
		}
	*/
}
