package it.emanuelemelini.spotifybestsongweeklypoll.db;

import javax.persistence.*;

@Entity
@Table(name = "contest_day")
public class Contest_day {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long idcontest_day;
	private String guild_id;
	private Day day;
	private boolean deleted;

	protected Contest_day() {}

	public Contest_day(String guild_id, Day day, boolean deleted) {
		this.guild_id = guild_id;
		this.day = day;
		this.deleted = deleted;
	}

	public long getIdcontest_day() {
		return idcontest_day;
	}

	public void setIdcontest_day(long idcontest_day) {
		this.idcontest_day = idcontest_day;
	}

	public String getGuild_id() {
		return guild_id;
	}

	public void setGuild_id(String guild_id) {
		this.guild_id = guild_id;
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
