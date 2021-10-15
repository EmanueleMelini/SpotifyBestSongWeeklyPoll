package it.emanuelemelini.spotifybestsongweeklypoll.db;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface WinnersRepository extends CrudRepository<Winners, Long> {

	List<Winners> getAllWinners();

	List<Winners> getWinnersById(String id);

	List<Winners> getWinnersByName(String name);

	Winners getWinnersByIdwinner(long IDwinner);
}
