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
@Table(name = "tr_so_details")
@EqualsAndHashCode(callSuper = true)
public class EdmsSalesOrderDetail extends EdmsAbstractBillableDetail implements Serializable {

	private static final long serialVersionUID = 7265020548100099294L;

	@Column(name = "deliveredQty")
	private BigDecimal deliveredQty;
}
