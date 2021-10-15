package it.emanuelemelini.spotifybestsongweeklypoll.db;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface Contest_dayRepository extends CrudRepository<Contest_day, Long> {

	List<Contest_day> getAllContest_day();

	List<Contest_day> getAllContest_dayByGuild_id(String guild_id);

	List<Contest_day> getAllContest_dayByDay(Contest_day.Day day);

	Contest_day getContest_dayByIDcontest_day(long IDcontest_day);
}
