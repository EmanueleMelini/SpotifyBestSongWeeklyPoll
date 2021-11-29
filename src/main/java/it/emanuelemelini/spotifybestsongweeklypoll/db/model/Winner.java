package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "winners")
public class Winner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "spotify_name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	@Column(name = "deleted")
	private boolean deleted;

	@Column(name = "winner_date")
	private LocalDateTime winnerDate;

	protected Winner() {

	}

	public Winner(Guild guild, String name, User user, LocalDateTime winnerdate) {
		this.guild = guild;
		this.name = name;
		this.user = user;
		this.deleted = false;
		this.winnerDate = winnerdate;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public LocalDateTime getWinnerDate() {
		return winnerDate;
	}

	public void setWinnerDate(LocalDateTime winnerDate) {
		this.winnerDate = winnerDate;
	}

}
