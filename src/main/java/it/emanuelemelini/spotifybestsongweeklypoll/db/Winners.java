package it.emanuelemelini.spotifybestsongweeklypoll.db;

import javax.persistence.*;

@Entity
@Table(name = "winners")
public class Winners {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long idwinner;
	private String name;
	private String id;
	private boolean deleted;

	protected Winners () {}

	public Winners(String name, String id, boolean deleted) {
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
