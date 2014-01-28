package eu.trentorise.smartcampus.social.engine.repo;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialGroup;

public interface GroupRepository extends CrudRepository<SocialGroup, Long> {

	public List<SocialGroup> findByCreatorId(String creatorId);

}
