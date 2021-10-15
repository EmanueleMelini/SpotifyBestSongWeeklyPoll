package it.emanuelemelini.spotifybestsongweeklypoll.db;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Winner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerRepository extends CrudRepository<Winner, Long> {

	List<Winner> getWinnersById(String id);

	List<Winner> getWinnersByName(String name);

	Winner getWinnersByIdwinner(long IDwinner);
}
