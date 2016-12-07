package ph.txtdis.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@Table(name = "master_customerlist_foc")
@EqualsAndHashCode(callSuper = true)
public class EdmsWeeklyVisit extends EdmsAbstractCodedId implements Serializable {

	private static final long serialVersionUID = 5318747057659803871L;

	@Column(name = "foc")
	private String frequency;

	@Column(name = "w1mon")
	private String week1mon;

	@Column(name = "w1tue")
	private String week1tue;

	@Column(name = "w1wed")
	private String week1wed;

	@Column(name = "w1thu")
	private String week1thu;

	@Column(name = "w1fri")
	private String week1fri;

	@Column(name = "w1sat")
	private String week1sat;

	@Column(name = "w1sun")
	private String week1sun;

	@Column(name = "w2mon")
	private String week2mon;

	@Column(name = "w2tue")
	private String week2tue;

	@Column(name = "w2wed")
	private String week2wed;

	@Column(name = "w2thu")
	private String week2thu;

	@Column(name = "w2fri")
	private String week2fri;

	@Column(name = "w2sat")
	private String week2sat;

	@Column(name = "w2sun")
	private String week2sun;

	@Column(name = "w3mon")
	private String week3mon;

	@Column(name = "w3tue")
	private String week3tue;

	@Column(name = "w3wed")
	private String week3wed;

	@Column(name = "w3thu")
	private String week3thu;

	@Column(name = "w3fri")
	private String week3fri;

	@Column(name = "w3sat")
	private String week3sat;

	@Column(name = "w3sun")
	private String week3sun;

	@Column(name = "w4mon")
	private String week4mon;

	@Column(name = "w4tue")
	private String week4tue;

	@Column(name = "w4wed")
	private String week4wed;

	@Column(name = "w4thu")
	private String week4thu;

	@Column(name = "w4fri")
	private String week4fri;

	@Column(name = "w4sat")
	private String week4sat;

	@Column(name = "w4sun")
	private String week4sun;
}
