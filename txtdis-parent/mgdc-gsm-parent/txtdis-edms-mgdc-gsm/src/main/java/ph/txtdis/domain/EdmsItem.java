package ph.txtdis.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "master_itemlist")
@EqualsAndHashCode(callSuper = true)
public class EdmsItem //
		extends EdmsAbstractMaster //
		implements Serializable {

	private static final long serialVersionUID = -3563721761323835200L;

	@Column(name = "packaging")
	private String packaging;

	@Column(name = "class")
	private String clazz;

	@Column(name = "category")
	private String category;

	@Column(name = "brandName")
	private String brand;

	@Column(name = "convMl")
	private BigDecimal toMilliliterQty;

	@Column(name = "convFactor")
	private BigDecimal conversionFactorQty;

	@Column(name = "inventoryItem")
	private boolean isKept;

	@Column(name = "saleableItem")
	private boolean isSold;

	@Column(name = "purchaseItem")
	private boolean isBought;

	@Column(name = "lotNo")
	private boolean isLotNumbered;

	@Column(name = "admissionDate")
	private boolean isReceiptDated;

	@Column(name = "expirationDate")
	private boolean isExpiryDated;

	@Column(name = "manufacturingDate")
	private boolean isProductionDated;

	@Column(name = "purchaseQty")
	private BigDecimal purchaseQty;

	@Column(name = "minInvLvl")
	private BigDecimal minQty;

	@Column(name = "maxInvLvl")
	private BigDecimal maxQty;

	@Column(name = "active")
	private Boolean isActive;

	@Override
	public String toString() {
		return getName();
	}
}
