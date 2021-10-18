package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "winners")
public class Winner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idwinner;

	@Column(name = "spotify_name")
	private String name;

	@Column(name = "spotify_id")
	private String id;

	private boolean deleted;

	@Column(name = "winner_date")
	private Date winnerdate;

	@Column(name = "guild_id")
	private long guildid;

	protected Winner() {}

	public Winner(String name, String id, boolean deleted, Date winnerdate) {
		this.name = name;
		this.id = id;
		this.deleted = deleted;
	}

	//TODO: manytoone

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

	public Date getWinnerdate() {
		return winnerdate;
	}

	public void setWinnerdate(Date winnerdate) {
		this.winnerdate = winnerdate;
	}

	public long getGuildid() {
		return guildid;
	}

	public void setGuildid(long guildid) {
		this.guildid = guildid;
	}

}
