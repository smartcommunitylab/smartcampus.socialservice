package eu.trentorise.smartcampus.social.managers;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.social.engine.beans.Comment;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/spring/applicationContext.xml")
public class SocialCommentManagerTest {
	
	@Autowired
	SocialCommentManager manager;
	
	private Comment comment = null;
	private static final String AUTHOR1 = "Pinco Pallino";
	
	@Before
	public void createComment(){
		comment = manager.create("Commento di prova", AUTHOR1, "1");
	}
	
	@Test
	public void readGroup(){
		Comment commentReaded = manager.readComment(comment.getId());
		Assert.assertTrue(commentReaded.getAuthor().compareTo(AUTHOR1) == 0);
	}
	
	@Test
	public void deleteComment(){
		Comment commentReaded = manager.delete(comment.getId(), comment.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
	}
	
	@After
	public void removeComment(){
		manager.remove(comment.getId(), comment.getAuthor());
	}
	
}
