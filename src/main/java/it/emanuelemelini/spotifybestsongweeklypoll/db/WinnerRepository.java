package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Winner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface WinnerRepository extends CrudRepository<Winner, Long> {

	List<Winner> getWinnersByIdAndDeleted(String id, boolean deleted);

	List<Winner> getWinnersByNameAndDeleted(String name, boolean deleted);

	List<Winner> getWinnersByGuildidAndDeleted(long guildid, boolean deleted);

	List<Winner> getWinnersByGuildidAndDeletedAndWinnerdate(long guildid, boolean deleted, LocalDateTime winnerdate);

	Winner getWinnersByIdwinner(long IDwinner);
}
