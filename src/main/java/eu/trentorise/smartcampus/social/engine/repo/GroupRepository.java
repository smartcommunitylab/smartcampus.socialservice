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

import eu.trentorise.smartcampus.social.engine.model.SocialGroup;

public interface GroupRepository extends
		PagingAndSortingRepository<SocialGroup, Long> {

	public List<SocialGroup> findByCreatorId(String creatorId);

	public List<SocialGroup> findByCreatorId(String creatorId, Pageable pager);

	public List<SocialGroup> findByCreatorId(String creatorId, Sort sort);

	public List<SocialGroup> findByCreatorIdAndCreationTimeBetween(
			String creatorId, Long beginTime, Long endTime, Pageable pager);

	public List<SocialGroup> findByCreatorIdAndCreationTimeBetween(
			String creatorId, Long beginTime, Long endTime, Sort sort);

	public List<SocialGroup> findByCreatorIdAndCreationTimeGreaterThan(
			String creatorId, Long beginTime, Pageable pager);

	public List<SocialGroup> findByCreatorIdAndCreationTimeGreaterThan(
			String creatorId, Long beginTime, Sort sort);

	public List<SocialGroup> findByCreatorIdAndCreationTimeLessThan(
			String creatorId, Long endTime, Pageable pager);

	public List<SocialGroup> findByCreatorIdAndCreationTimeLessThan(
			String creatorId, Long endTime, Sort sort);

	public List<SocialGroup> findByCreationTimeBetween(Long beginTime,
			Long endTime, Pageable pager);

	public List<SocialGroup> findByCreationTimeBetween(Long beginTime,
			Long endTime, Sort sort);

	public List<SocialGroup> findByCreationTimeGreaterThan(Long beginTime,
			Pageable pager);

	public List<SocialGroup> findByCreationTimeGreaterThan(Long beginTime,
			Sort sort);

	public List<SocialGroup> findByCreationTimeLessThan(Long endTime,
			Pageable pager);

	public List<SocialGroup> findByCreationTimeLessThan(Long endTime, Sort sort);

	public SocialGroup findByNameIgnoreCase(String name);

	public SocialGroup findByCreatorIdAndNameIgnoreCase(String creatorId,
			String name);

}
