package it.emanuelemelini.spotifybestsongweeklypoll.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Contest_day {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private long IDcontest_day;
	private String guild_id;
	//TODO: trasformare sia qua che in db day in enum
	private String day;
	private boolean deleted;

	public long getIDcontest_day() {
		return IDcontest_day;
	}

	public void setIDcontest_day(long IDcontest_day) {
		this.IDcontest_day = IDcontest_day;
	}

	public String getGuild_id() {
		return guild_id;
	}

	public void setGuild_id(String guild_id) {
		this.guild_id = guild_id;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

}
