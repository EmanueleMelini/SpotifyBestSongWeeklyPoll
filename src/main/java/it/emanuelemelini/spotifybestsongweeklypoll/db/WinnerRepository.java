package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Winner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WinnerRepository extends CrudRepository<Winner, Long> {

	List<Winner> getWinnersById(String id);

	List<Winner> getWinnersByName(String name);

	List<Winner> getWinnersByGuildid(long guildid);

	List<Winner> getWinnersByGuildidAndDeletedAndWinnerdate(long guildid, boolean deleted, Date winnerdate);

	Winner getWinnersByIdwinner(long IDwinner);
}
