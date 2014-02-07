package eu.trentorise.smartcampus.social.engine.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;

@Repository
public interface CommunityRepository extends
		CrudRepository<SocialCommunity, Long> {

}
