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

	protected Guild() {}

	public Guild(long guildid) {
		this.guildid = guildid;
	}

	public long getGuildid() {
		return guildid;
	}

	public void setGuildid(long guildid) {
		this.guildid = guildid;
	}

}
