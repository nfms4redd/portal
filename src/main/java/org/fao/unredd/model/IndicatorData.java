package org.fao.unredd.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class IndicatorData {

	@Id
	private String id;

	@Id
	@ManyToOne(optional = false)
	private Indicator indicator;

	@Lob
	@Basic(fetch = FetchType.LAZY)
	private byte[] content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Indicator getIndicator() {
		return indicator;
	}

	public void setIndicator(Indicator indicator) {
		this.indicator = indicator;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
