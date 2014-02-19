package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

@Repository
public interface EntityRepository extends
		PagingAndSortingRepository<SocialEntity, String> {

	public List<SocialEntity> findByOwnerOrCommunityOwner(SocialUser owner,
			SocialCommunity communityOwner);

	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByUserSharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?1 MEMBER OF g.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByGroupSharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1 MEMBER OF comm.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByCommunitySharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntities(String userId);

	public List<SocialEntity> findByPublicShared(boolean publicShared);
}
