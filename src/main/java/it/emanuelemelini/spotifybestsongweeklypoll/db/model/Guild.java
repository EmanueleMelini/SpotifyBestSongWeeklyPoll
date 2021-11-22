package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "guilds")
public class Guild {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idguild;

	@Column(name = "guild_id")
	private Long guildid;

	@Column(name = "role_id")
	private Long roleid;

	@OneToMany(mappedBy = "guild")
	private Set<Winner> winners;

	@OneToMany(mappedBy = "guild")
	private Set<ContestDay> contestDays;

	@OneToMany(mappedBy = "guild")
	private Set<User> users;

	@OneToMany(mappedBy = "guild")
	private Set<Contest> contests;

	private boolean deleted;

	protected Guild() {
	}

	public Guild(Long guildid, boolean deleted) {
		this.guildid = guildid;
		this.deleted = deleted;
	}

	public Long getIdguild() {
		return idguild;
	}

	public Long getGuildid() {
		return guildid;
	}

	public void setGuildid(Long guildid) {
		this.guildid = guildid;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Long getRoleid() {
		return roleid;
	}

	public void setRoleid(Long roleid) {
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

	public Set<Contest> getContests() {
		return contests;
	}

}
