package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private long iduser;

	@Column(name = "spotify_id")
	private String spotifyid;

	@Column(name = "discord_id")
	private long discordid;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	private boolean deleted;

	@OneToMany(mappedBy = "user")
	private Set<Winner> winners;

	protected User() {
	}

	public User(String spotifyid, long discordid, boolean deleted) {
		this.spotifyid = spotifyid;
		this.discordid = discordid;
		this.deleted = deleted;
	}

	public long getIduser() {
		return iduser;
	}

	public String getSpotifyid() {
		return spotifyid;
	}

	public void setSpotifyid(String spotifyid) {
		this.spotifyid = spotifyid;
	}

	public long getDiscordid() {
		return discordid;
	}

	public void setDiscordid(long discordid) {
		this.discordid = discordid;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<Winner> getWinners() {
		return winners;
	}

}
