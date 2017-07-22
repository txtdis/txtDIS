package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "master_customerlist")
@EqualsAndHashCode(callSuper = true)
public class EdmsCustomer //
		extends EdmsAbstractMaster //
		implements Serializable {

	private static final long serialVersionUID = -6671533661614579186L;

	@Column(name = "ci_oName")
	private String owner;

	@Column(name = "ci_bName")
	private String businessName;

	@Column(name = "ci_tin")
	private String tin = "";

	@Column(name = "ci_yrOwned")
	private String yearsOwned = "0";

	@Column(name = "ci_outletType")
	private String channel;

	@Column(name = "ci_outletStatus")
	private String status;

	@Column(name = "oi_homeAddress")
	private String homeAddress = "";

	@Column(name = "oi_contactNo")
	private String phoneNo;

	@Column(name = "oi_bDate")
	private String birthDate = "";

	@Column(name = "oi_status")
	private String exclusivity;

	@Column(name = "l_area")
	private String areaCode;

	@Column(name = "l_territory")
	private String territoryCode;

	@Column(name = "l_district")
	private String districtCode;

	@Column(name = "l_dss")
	private String supervisor;

	@Column(name = "ba_province")
	private String province;

	@Column(name = "ba_city")
	private String city;

	@Column(name = "ba_barangay")
	private String barangay;

	@Column(name = "ba_no")
	private String streetNo = "";

	@Column(name = "ba_street")
	private String street = "";

	@Column(name = "ba_bldg")
	private String bldg = "";

	@Column(name = "ri_salesman")
	private String seller = "";

	@Column(name = "ri_foc")
	private String visitFrequency;

	@Column(name = "ri_smCode")
	private String sellerCode;

	@Column(name = "ri_assignedDay")
	private String visitDay;
}
