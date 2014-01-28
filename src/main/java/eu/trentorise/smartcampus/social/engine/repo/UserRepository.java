package eu.trentorise.smartcampus.social.engine.repo;

import org.springframework.data.repository.CrudRepository;

import eu.trentorise.smartcampus.social.engine.model.SocialUser;

public interface UserRepository extends CrudRepository<SocialUser, String> {

}
