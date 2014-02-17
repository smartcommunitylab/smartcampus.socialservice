package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

public interface EntityRepository extends
		PagingAndSortingRepository<SocialEntity, String> {

	public List<SocialEntity> findByOwnerOrCommunityOwner(SocialUser owner,
			SocialCommunity communityOwner);

}
