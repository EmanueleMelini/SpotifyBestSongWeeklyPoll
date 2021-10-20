package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {

	List<User> getUsersByGuildAndDeleted(Guild guild, boolean deleted);

	User getUserBySpotifyidAndDeleted(String spotifyid, boolean deleted);

	User getUserByDiscordidAndDeleted(long discordid, boolean deleted);

	User getUserByDiscordidAndSpotifyidAndDeleted(long discordid, String spotifyid, boolean deleted);

	User getUserByIduser(long iduser);

}
