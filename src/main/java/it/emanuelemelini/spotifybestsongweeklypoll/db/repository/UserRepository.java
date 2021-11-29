package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

	List<User> getUsersByGuildAndDeleted(Guild guild, boolean deleted);

	User getUserBySpotifyIdAndDeleted(String spotifyId, boolean deleted);

	User getUserByDiscordIdAndDeleted(Long discordId, boolean deleted);

	User getUserByDiscordIdAndSpotifyIdAndDeleted(Long discordId, String spotifyId, boolean deleted);

	User getUserByIdAndDeleted(Long idr, boolean deleted);

}
