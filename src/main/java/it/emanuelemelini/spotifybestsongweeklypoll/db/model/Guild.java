package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "guilds")
public class Guild {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idguild;

	@Column(name = "guild_id")
	private long guildid;

	@Column(name = "role_id")
	private long roleid;

	@OneToMany(mappedBy = "guild")
	private Set<Winner> winners;

	@OneToMany(mappedBy = "guild")
	private Set<ContestDay> contestDays;

	@OneToMany(mappedBy = "guild")
	private Set<User> users;

	private boolean deleted;

	protected Guild() {
	}

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

	public long getRoleid() {
		return roleid;
	}

	public void setRoleid(long roleid) {
		this.roleid = roleid;
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

}
