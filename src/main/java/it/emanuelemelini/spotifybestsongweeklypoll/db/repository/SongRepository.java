package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Song;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SongRepository extends CrudRepository<Song, Long> {

	Song getSongBySpotifyIDAndDeleted(String spotifyID, boolean deleted);

	List<Song> getSongsByGuildAndDeleted(Guild guild, boolean deleted);

	Song getSongByIdsongAndDeleted(long IDsong, boolean deleted);

}
