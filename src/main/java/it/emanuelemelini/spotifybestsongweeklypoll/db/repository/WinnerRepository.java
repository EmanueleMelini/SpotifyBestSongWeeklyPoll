package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Winner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WinnerRepository extends CrudRepository<Winner, Long> {

	List<Winner> getWinnersByIdAndDeleted(String id, boolean deleted);

	List<Winner> getWinnersByNameAndDeleted(String name, boolean deleted);

	List<Winner> getWinnersByGuildAndDeleted(Guild guild, boolean deleted);

	List<Winner> getWinnersByGuildAndDeletedAndWinnerdate(Guild guild, boolean deleted, LocalDateTime winnerdate);

	Winner findTopByGuildAndDeletedOrderByWinnerdateDesc(Guild guild, boolean deleted);

	Winner getWinnersByIdwinner(long IDwinner);

}
