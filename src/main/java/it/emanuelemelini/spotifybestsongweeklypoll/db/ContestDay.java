package it.emanuelemelini.spotifybestsongweeklypoll.db;

import javax.persistence.*;

@Entity
@Table(name = "contest_day")
public class ContestDay {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@Column(name = "idcontest_day")
	private long idcontestday;
	@Column(name = "guild_id")
	private String guildid;
	private Day day;
	private boolean deleted;

	protected ContestDay() {}

	public ContestDay(String guildid, Day day, boolean deleted) {
		this.guildid = guildid;
		this.day = day;
		this.deleted = deleted;
	}

	public long getIdcontestday() {
		return idcontestday;
	}

	public void setIdcontestday(long idcontest_day) {
		this.idcontestday = idcontest_day;
	}

	public String getGuildid() {
		return guildid;
	}

	public void setGuildid(String guild_id) {
		this.guildid = guild_id;
	}

	public Day getDay() {
		return day;
	}

	public void setDay(Day day) {
		this.day = day;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public enum Day {
		MONDAY,
		TUESDAY,
		WEDNSDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY,
		SUNDAY;
	}
}
