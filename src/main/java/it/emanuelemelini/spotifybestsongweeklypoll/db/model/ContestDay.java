package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "contest_day")
public class ContestDay {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idcontest_day")
	private long id;

	@ManyToOne
	@JoinColumn(name = "id_guild")
	private Guild guild;

	@Enumerated(EnumType.STRING)
	private DayOfWeek day;

	@Column(name = "deleted")
	private boolean deleted;

	protected ContestDay() {

	}

	public ContestDay(Guild guild, DayOfWeek day, boolean deleted) {
		this.guild = guild;
		this.day = day;
		this.deleted = deleted;
	}

	public long getId() {
		return id;
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

}
