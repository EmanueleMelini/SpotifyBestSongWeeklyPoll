package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SongRepository extends CrudRepository<Song, Long> {

	Song getSongBySpotifyIdAndDeleted(String spotifyId, boolean deleted);

	List<Song> getSongsByGuildAndDeleted(Guild guild, boolean deleted);

	Song getSongByIdAndDeleted(long id, boolean deleted);

}
