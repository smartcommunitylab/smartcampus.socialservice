package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialEntityType;

public interface EntityTypeRepository extends PagingAndSortingRepository<SocialEntityType, Long>{
	
	public List<SocialEntityType> findByNameIgnoreCase(String name, Pageable pager);

	public List<SocialEntityType> findByMimeType(String mimeType, Pageable pager);
	
	public List<SocialEntityType> findByNameIgnoreCaseAndMimeType(String name, String mimeType);
	
}
