package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
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
			SocialCommunity communityOwner, Pageable pager);

	/** find all entities shared with user */
	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByUserSharedWith(String userId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?1 MEMBER OF g.members AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByGroupSharedWith(String userId,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1 MEMBER OF comm.members AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByCommunitySharedWith(String userId,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findPublicEntities(String userId, Pageable pager);

	/** find entity shared with user */
	@Query("SELECT se FROM SocialEntity se WHERE ?2=se.id AND ?1 MEMBER OF se.usersSharedWith AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public SocialEntity findByUserSharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?2=se.id AND ?1 MEMBER OF g.members AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public SocialEntity findByGroupSharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?2=se.id AND ?1 MEMBER OF comm.members AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public SocialEntity findByCommunitySharedWith(String userId, String uri);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND ?2=se.id AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public SocialEntity findPublicEntities(String userId, String uri);

	/** find entities shared with community */
	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1=comm.id AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findBySharedWithCommunity(Long communityId,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?2=se.id AND ?1=comm.id AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public SocialEntity findBySharedWithCommunity(Long communityId, String uri);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntities(Long communityId,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND ?2=se.id AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public SocialEntity findPublicEntities(Long communityId, String uri);

	public List<SocialEntity> findByPublicShared(boolean publicShared);
}
