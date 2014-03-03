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

	/** find all entities shared with user */
	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByUserSharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?1 MEMBER OF g.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByGroupSharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1 MEMBER OF comm.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findByCommunitySharedWith(String userId);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntities(String userId);

	/** find entity shared with user */
	@Query("SELECT se FROM SocialEntity se WHERE ?2=se.id AND ?1 MEMBER OF se.usersSharedWith AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public SocialEntity findByUserSharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?2=se.id AND ?1 MEMBER OF g.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public SocialEntity findByGroupSharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?2=se.id AND ?1 MEMBER OF comm.members AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public SocialEntity findByCommunitySharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND ?2=se.id AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public SocialEntity findPublicEntities(String userId, String uri);

	/** find entities shared with community */
	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1=comm.id")
	public List<SocialEntity> findBySharedWithCommunity(Long communityId);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?2=se.id AND ?1 MEMBER OF comm.id AND (?1 <> se.owner.id OR ?1 <> se.communityOwner.id)")
	public SocialEntity findBySharedWithCommunity(String communityId, String uri);

	public List<SocialEntity> findByPublicShared(boolean publicShared);
}
