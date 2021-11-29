package it.emanuelemelini.spotifybestsongweeklypoll.db.repository;

import it.emanuelemelini.spotifybestsongweeklypoll.db.model.Guild;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuildRepository extends CrudRepository<Guild, Long> {

	Guild getGuildByGuildIdAndDeleted(Long guildId, boolean deleted);

	List<Guild> getGuildsByDeleted(boolean deleted);

	Guild getGuildById(Long id);

}
