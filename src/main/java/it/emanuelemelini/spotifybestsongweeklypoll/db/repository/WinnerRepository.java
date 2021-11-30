package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.User;
import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Winner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface WinnerRepository extends CrudRepository<Winner, Long> {

	List<Winner> getWinnersByUserAndDeleted(User user, boolean deleted);

	List<Winner> getWinnersByNameAndDeleted(String name, boolean deleted);

	List<Winner> getWinnersByGuildAndDeleted(Guild guild, boolean deleted);

	List<Winner> getWinnersByGuildAndDeletedAndWinnerDate(Guild guild, boolean deleted, LocalDateTime winnerDate);

	Winner findTopByGuildAndDeletedOrderByWinnerDateDesc(Guild guild, boolean deleted);

	Winner getWinnersById(long id);

}
