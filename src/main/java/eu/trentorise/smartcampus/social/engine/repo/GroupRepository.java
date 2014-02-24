package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialGroup;

//CrudRepository<SocialGroup, Long>
public interface GroupRepository extends PagingAndSortingRepository<SocialGroup, Long> {

	public List<SocialGroup> findByCreatorId(String creatorId);
	
	public List<SocialGroup> findByCreatorId(String creatorId, Pageable pager);
	
	public List<SocialGroup> findByCreatorIdAndCreationTimeBetween(String creatorId, Long beginTime, Long endTime, Pageable pager);
	
	public List<SocialGroup> findByCreatorIdAndCreationTimeGreaterThan(String creatorId, Long beginTime, Pageable pager);
	
	public List<SocialGroup> findByCreatorIdAndCreationTimeLessThan(String creatorId, Long endTime, Pageable pager);
	
	public List<SocialGroup> findByCreationTimeBetween(Long beginTime, Long endTime, Pageable pager);
	
	public List<SocialGroup> findByCreationTimeGreaterThan(Long beginTime, Pageable pager);
	
	public List<SocialGroup> findByCreationTimeLessThan(Long endTime, Pageable pager);
	
	public SocialGroup findByNameIgnoreCase(String name);
	
	public SocialGroup findByCreatorIdAndNameIgnoreCase(String creatorId, String name);

}
