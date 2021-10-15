package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;

@Entity
@Table(name = "winners")
public class Winner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idwinner;
	private String name;
	private String id;
	private boolean deleted;

	protected Winner() {}

	public Winner(String name, String id, boolean deleted) {
		this.name = name;
		this.id = id;
		this.deleted = deleted;
	}

	public long getIdwinner() {
		return idwinner;
	}

	public void setIdwinner(long IDwinner) {
		this.idwinner = IDwinner;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
