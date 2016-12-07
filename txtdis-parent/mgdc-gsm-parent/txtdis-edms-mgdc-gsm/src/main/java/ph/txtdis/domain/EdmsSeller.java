package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "master_salesmanlist")
public class EdmsSeller implements Serializable {

	private static final long serialVersionUID = 8284134600131466673L;

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "xCode")
	private String code;

	@Column(name = "xName")
	private String name;

	@Column(name = "sales_address")
	private String address;

	@Column(name = "tr_plateNo")
	private String plateNo;

	@Column(name = "tr_code")
	private String truckCode;

	@Column(name = "tr_name")
	private String truckDescription;

	@Column(name = "dr_driver")
	private String driver;

	@Column(name = "dr_helper")
	private String helper;

	@Column(name = "warehouse")
	private String warehouseCode;

	@Column(name = "loc_area")
	private String areaCode;

	@Column(name = "loc_territory")
	private String territoryCode;

	@Column(name = "loc_district")
	private String districtCode;

	@Column(name = "loc_dss")
	private String supervisor;

	@Column(name = "createBy")
	private String createdBy;

	@Column(name = "dateCreate")
	private String createdOn;

	@Column(name = "modiBy")
	private String modifiedBy;

	@Column(name = "dateModi")
	private String modifiedOn;

	@Override
	public String toString() {
		return getName();
	}
}
