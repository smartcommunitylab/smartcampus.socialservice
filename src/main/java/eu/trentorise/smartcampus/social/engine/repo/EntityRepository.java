/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;
import eu.trentorise.smartcampus.social.engine.model.SocialEntity;
import eu.trentorise.smartcampus.social.engine.model.SocialUser;

@Repository
public interface EntityRepository extends
		PagingAndSortingRepository<SocialEntity, String> {

	@Query("SELECT se FROM SocialEntity se WHERE (?1=se.owner OR ?2=se.communityOwner) AND se.creationTime BETWEEN ?3 AND ?4")
	public List<SocialEntity> findMyEntities(SocialUser owner,
			SocialCommunity communityOwner, long fromDate, long toDate,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE (?1=se.owner OR ?2=se.communityOwner) AND se.namespace=?5 AND se.creationTime BETWEEN ?3 AND ?4")
	public List<SocialEntity> findMyEntitiesByAppId(SocialUser owner,
			SocialCommunity communityOwner, long fromDate, long toDate,
			String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE (?1=se.owner OR ?2=se.communityOwner) AND se.creationTime BETWEEN ?3 AND ?4")
	public List<SocialEntity> findMyEntities(SocialUser owner,
			SocialCommunity communityOwner, long fromDate, long toDate,
			Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE (?1=se.owner OR ?2=se.communityOwner) AND se.namespace=?5 AND se.creationTime BETWEEN ?3 AND ?4")
	public List<SocialEntity> findMyEntitiesByAppId(SocialUser owner,
			SocialCommunity communityOwner, long fromDate, long toDate,
			String appId, Sort sort);

	/** find all entities shared with user */
	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByUserSharedWith(String userId,
			long fromDate, long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND  se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByUserSharedWith(String userId,
			long fromDate, long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND se.namespace=?4 AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByUserSharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE ?1 MEMBER OF se.usersSharedWith AND se.namespace=?4 AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByUserSharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?1 MEMBER OF g.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByGroupSharedWith(String userId,
			long fromDate, long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE ?1 MEMBER OF g.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByGroupSharedWith(String userId,
			long fromDate, long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE se.namespace=?4 AND ?1 MEMBER OF g.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByGroupSharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.groupsSharedWith) AS g WHERE se.namespace=?4 AND ?1 MEMBER OF g.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByGroupSharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1 MEMBER OF comm.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByCommunitySharedWith(String userId,
			long fromDate, long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1 MEMBER OF comm.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByCommunitySharedWith(String userId,
			long fromDate, long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE  se.namespace=?4 AND ?1 MEMBER OF comm.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByCommunitySharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE se.namespace=?4 AND ?1 MEMBER OF comm.members AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findByCommunitySharedWithByAppId(String userId,
			long fromDate, long toDate, String appId, Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findPublicEntities(String userId, long fromDate,
			long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findPublicEntities(String userId, long fromDate,
			long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.namespace=?4 AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findPublicEntitiesByAppId(String userId,
			long fromDate, long toDate, String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.namespace=?4 AND se.creationTime BETWEEN ?2 AND ?3 AND (se.owner IS NULL OR ?1 <> se.owner.id)")
	public List<SocialEntity> findPublicEntitiesByAppId(String userId,
			long fromDate, long toDate, String appId, Sort sort);

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
	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1=comm.id AND se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findBySharedWithCommunity(Long communityId,
			long fromDate, long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE se.namespace=?4 AND ?1=comm.id AND se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findBySharedWithCommunityByAppId(
			Long communityId, long fromDate, long toDate, String appId,
			Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE se.namespace=?4 AND ?1=comm.id AND se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findBySharedWithCommunityByAppId(
			Long communityId, long fromDate, long toDate, String appId,
			Pageable pager);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?1=comm.id AND se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findBySharedWithCommunity(Long communityId,
			long fromDate, long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se, IN (se.communitiesSharedWith) AS comm WHERE ?2=se.id AND ?1=comm.id AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public SocialEntity findBySharedWithCommunity(Long communityId, String uri);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND  se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntities(Long communityId,
			long fromDate, long toDate, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND  se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntities(Long communityId,
			long fromDate, long toDate, Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.namespace=?4 AND  se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntitiesByAppId(Long communityId,
			long fromDate, long toDate, String appId, Pageable pager);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND se.namespace=?4 AND se.creationTime BETWEEN ?2 AND ?3 AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public List<SocialEntity> findPublicEntitiesByAppId(Long communityId,
			long fromDate, long toDate, String appId, Sort sort);

	@Query("SELECT se FROM SocialEntity se WHERE publicShared=true AND ?2=se.id AND (se.communityOwner IS NULL OR ?1 <> se.communityOwner.id)")
	public SocialEntity findPublicEntities(Long communityId, String uri);

	public List<SocialEntity> findByPublicShared(boolean publicShared);
}
