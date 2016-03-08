package ph.txtdis.fx.dialog;

import static javafx.beans.binding.Bindings.notEqual;
import static org.apache.log4j.Logger.getLogger;
import static ph.txtdis.type.Type.CURRENCY;
import static ph.txtdis.type.Type.ID;
import static ph.txtdis.type.Type.INTEGER;
import static ph.txtdis.util.NumberUtils.toLong;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ObservableLongValue;
import ph.txtdis.dto.Item;
import ph.txtdis.exception.DuplicateException;
import ph.txtdis.exception.FailedAuthenticationException;
import ph.txtdis.exception.InvalidException;
import ph.txtdis.exception.NoServerConnectionException;
import ph.txtdis.exception.RestException;
import ph.txtdis.exception.StoppedServerException;
import ph.txtdis.fx.control.InputNode;
import ph.txtdis.fx.control.LabeledField;
import ph.txtdis.service.ItemService;

@Scope("prototype")
@Component("itemDialog")
public class ItemDialog extends NameListDialog<Item, ItemService> {

	private static Logger logger = getLogger(ItemDialog.class);

	@Autowired
	private LabeledField<BigDecimal> priceField;

	@Autowired
	private LabeledField<Long> idField;

	@Autowired
	private LabeledField<Integer> bottlePerCaseField;

	private Item item;

	private ObservableLongValue id;

	private BooleanProperty hasName = new SimpleBooleanProperty(false);

	public ItemDialog codeOf(Long id) {
		this.id = new SimpleLongProperty(id);
		return this;
	}

	public ItemDialog codeOf(String idText) {
		long id = toLong(idText);
		return codeOf(id);
	}

	@Override
	public void refresh() {
		super.refresh();
		if (id != null)
			try {
				setForBottlePerCaseAndPriceOnlyInputs();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}

	private Item getItem() throws NoServerConnectionException, StoppedServerException, FailedAuthenticationException,
			InvalidException, RestException {
		if (id != null && hasName.get())
			return service.save(pricedItem());
		return service.save(idField.getValue(), nameField.getValue(), bottlePerCaseField.getValue(),
				priceField.getValue());
	}

	private BooleanBinding hasCode() {
		return notEqual(0L, id);
	}

	private LabeledField<Long> idField() {
		idField.name("Code").build(ID);
		idField.setOnAction(e -> verifyIdIsUnique(idField.getValue()));
		idField.disableIf(hasCode());
		return idField;
	}

	private Item pricedItem() {
		item.setBottlePerCase(bottlePerCaseField.getValue());
		item.setPriceValue(priceField.getValue());
		logger.info("Item from pricedItem = " + item);
		return item;
	}

	private void setForBottlePerCaseAndPriceOnlyInputs() throws NoServerConnectionException, StoppedServerException,
			FailedAuthenticationException, InvalidException, DuplicateException, RestException {
		item = service.findByCode(id.get());
		hasName.set(item != null);
		if (hasName.get()) {
			idField.setValue(item.getCode());
			nameField.setValue(item.getName());
			logger.info("Item from code = " + item);
		} else {
			idField.setValue(id.get());
		}
	}

	private void verifyIdIsUnique(Long id) {
		if (this.id == null && id != 0)
			try {
				service.duplicateExists(id);
			} catch (Exception e) {
				dialog.show(e).addParent(this).start();
				refresh();
			}
	}

	@Override
	protected List<InputNode<?>> addNodes() {
		List<InputNode<?>> l = new ArrayList<>();
		l.add(idField());
		l.addAll(super.addNodes());
		l.add(bottlePerCaseField.name("Bottle/Case").build(INTEGER));
		l.add(priceField.name("Price").build(CURRENCY));
		return l;
	}

	@Override
	protected Item createEntity() {
		try {
			Item i = getItem();
			logger.info("Item after save = " + i);
			return i;
		} catch (Exception e) {
			resetNodesOnError(e);
			return null;
		}
	}

	@Override
	protected String headerText() {
		return "Add New Product";
	}

	@Override
	protected LabeledField<String> nameField() {
		super.nameField();
		nameField.width(320);
		nameField.disableIf(hasName);
		return nameField;
	}
}