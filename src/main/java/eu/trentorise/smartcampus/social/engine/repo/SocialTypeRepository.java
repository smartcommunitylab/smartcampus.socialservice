package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialType;

public interface SocialTypeRepository extends PagingAndSortingRepository<SocialType, Long>{
	
	public List<SocialType> findByName(String name, Pageable pager);

	public List<SocialType> findByMimeType(String mimeType, Pageable pager);
	
	public List<SocialType> findByNameAndMimeType(String name, String mimeType);
	
}
