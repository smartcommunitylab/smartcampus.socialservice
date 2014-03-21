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
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;

@Repository
public interface CommunityRepository extends
		PagingAndSortingRepository<SocialCommunity, Long> {

	public SocialCommunity findByName(String name);

	public SocialCommunity findByNameIgnoreCase(String name);

	public List<SocialCommunity> findByCreationTimeBetween(Long beginTime,
			Long endTime, Pageable pager);

	public List<SocialCommunity> findByCreationTimeBetween(Long beginTime,
			Long endTime, Sort sort);

	public List<SocialCommunity> findByCreationTimeGreaterThan(Long beginTime,
			Pageable pager);

	public List<SocialCommunity> findByCreationTimeLessThan(Long endTime,
			Pageable pager);

	public List<SocialCommunity> findByAppId(String appId);

	public List<SocialCommunity> findByAppIdAndCreationTimeBetween(
			String appId, Long beginTime, Long endTime, Pageable pager);

	public List<SocialCommunity> findByAppIdAndCreationTimeGreaterThan(
			String appId, Long beginTime, Pageable pager);

	public List<SocialCommunity> findByAppIdAndCreationTimeLessThan(
			String appId, Long endTime, Pageable pager);

}
