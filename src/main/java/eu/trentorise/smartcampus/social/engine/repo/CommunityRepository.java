package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import eu.trentorise.smartcampus.social.engine.model.SocialCommunity;

@Repository
public interface CommunityRepository extends
		PagingAndSortingRepository<SocialCommunity, Long> {

	public List<SocialCommunity> findByCreationTimeBetween(Long beginTime,
			Long endTime, Pageable pager);

	public List<SocialCommunity> findByCreationTimeGreaterThan(Long beginTime,
			Pageable pager);

	public List<SocialCommunity> findByCreationTimeLessThan(Long endTime,
			Pageable pager);
}
