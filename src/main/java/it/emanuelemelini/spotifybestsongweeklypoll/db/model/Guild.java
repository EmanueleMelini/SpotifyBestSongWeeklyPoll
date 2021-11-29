package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "guilds")
public class Guild {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_guild")
	private Long id;

	@Column(name = "guild_id")
	private Long guildId;

	@Column(name = "role_id")
	private Long roleId;

	@Column(name = "deleted")
	private boolean deleted;

	@OneToMany(mappedBy = "guild")
	private Set<Winner> winners;

	@OneToMany(mappedBy = "guild")
	private Set<ContestDay> contestDays;

	@OneToMany(mappedBy = "guild")
	private Set<User> users;

	@OneToMany(mappedBy = "guild")
	private Set<Contest> contests;

	@OneToMany(mappedBy = "guild")
	private Set<ContestTrack> contestTracks;

	@OneToMany(mappedBy = "guild")
	private Set<Playlist> playlists;

	protected Guild() {

	}

	public Guild(Long guildid, boolean deleted) {
		this.guildId = guildid;
		this.deleted = deleted;
	}

	public Long getId() {
		return id;
	}

	public Long getGuildId() {
		return guildId;
	}

	public void setGuildId(Long guildId) {
		this.guildId = guildId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Set<Winner> getWinners() {
		return winners;
	}

	public Set<ContestDay> getContestDays() {
		return contestDays;
	}

	public Set<User> getUsers() {
		return users;
	}

	public Set<Contest> getContests() {
		return contests;
	}

	public Set<ContestTrack> getContestTracks() {
		return contestTracks;
	}

	public Set<Playlist> getPlaylists() {
		return playlists;
	}

}
