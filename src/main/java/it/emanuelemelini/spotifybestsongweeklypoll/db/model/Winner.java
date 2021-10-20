package it.emanuelemelini.spotifybestsongweeklypoll.db.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "winners")
public class Winner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long idwinner;

	@Column(name = "spotify_name")
	private String name;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	private boolean deleted;

	@Column(name = "winner_date")
	private LocalDateTime winnerdate;

	@ManyToOne
	@JoinColumn(name = "guild_id")
	private Guild guild;

	protected Winner() {
	}

	public Winner(String name, User user, LocalDateTime winnerdate, Guild guild) {
		this.name = name;
		this.user = user;
		this.deleted = false;
		this.winnerdate = winnerdate;
		this.guild = guild;
	}

	public long getIdwinner() {
		return idwinner;
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

	public LocalDateTime getWinnerdate() {
		return winnerdate;
	}

	public void setWinnerdate(LocalDateTime winnerdate) {
		this.winnerdate = winnerdate;
	}

	public Guild getGuild() {
		return guild;
	}

	public void setGuild(Guild guild) {
		this.guild = guild;
	}

}
