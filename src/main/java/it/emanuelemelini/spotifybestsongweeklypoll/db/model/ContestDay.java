package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "contest_day")
public class ContestDay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcontest_day")
	private long idcontestday;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	@Enumerated(EnumType.STRING)
	private DayOfWeek day;

	private boolean deleted;

	protected ContestDay() {
	}

	public ContestDay(Guild guild, DayOfWeek day, boolean deleted) {
		this.guild = guild;
		this.day = day;
		this.deleted = deleted;
	}

	public long getIdcontestday() {
		return idcontestday;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

	public DayOfWeek getDay() {
		return day;
	}

	public void setDay(DayOfWeek day) {
		this.day = day;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	/*
	public enum Day {
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY,
		SUNDAY;
	}
	 */

}
