package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	@Column(name = "spotify_id")
	private String spotifyId;

	@Column(name = "discord_id")
	private Long discordId;

	@Column(name = "deleted")
	private boolean deleted;

	@OneToMany(mappedBy = "user")
	private Set<Winner> winners;

	protected User() {

	}

	public User(Guild guild, String spotifyid, Long discordid, boolean deleted) {
		this.guild = guild;
		this.spotifyId = spotifyid;
		this.discordId = discordid;
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public String getSpotifyId() {
		return spotifyId;
	}

	public void setSpotifyId(String spotifyId) {
		this.spotifyId = spotifyId;
	}

	public Long getDiscordId() {
		return discordId;
	}

	public void setDiscordId(Long discordId) {
		this.discordId = discordId;
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
