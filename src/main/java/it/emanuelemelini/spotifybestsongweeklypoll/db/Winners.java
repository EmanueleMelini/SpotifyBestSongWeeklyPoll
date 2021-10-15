package it.emanuelemelini.spotifybestsongweeklypoll.db;

import javax.persistence.*;

@Entity
public class Winners {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long IDwinner;
	private String name;
	private String id;
	private boolean deleted;

	public long getIDwinner() {
		return IDwinner;
	}

	public void setIDwinner(long IDwinner) {
		this.IDwinner = IDwinner;
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
