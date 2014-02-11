package eu.trentorise.smartcampus.social.engine.repo;

import org.springframework.data.repository.PagingAndSortingRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialEntity;

public interface EntityRepository extends
		PagingAndSortingRepository<SocialEntity, String> {

}
