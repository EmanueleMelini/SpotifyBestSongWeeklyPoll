package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;

@Entity
@Table(name = "guilds")
public class Guild {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idguild;

	@Column(name = "guild_id")
	private long guildid;

	private boolean deleted;

	protected Guild() {}

	public Guild(long guildid, boolean deleted) {
		this.guildid = guildid;
		this.deleted = deleted;
	}

	public long getIdguild() {
		return idguild;
	}

	public long getGuildid() {
		return guildid;
	}

	public void setGuildid(long guildid) {
		this.guildid = guildid;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
