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
	private Comment comment1, comment2, comment3;
	private static final String AUTHOR1 = "Pinco Pallino";
	private static final String ENTITYID1 = "1";
	private static final String ENTITYID2 = "2";
	private static final String COMMENTTEXT1 = "Comment 1";
	private static final String COMMENTTEXT2 = "Comment 2";
	private static final String COMMENTTEXT3 = "Comment 3";
	private static final String COMMENTTEXT4 = "Comment 4";
	
	@Before
	public void createComment(){
		comment = manager.create(COMMENTTEXT1, AUTHOR1, ENTITYID1);
		comment1 = manager.create(COMMENTTEXT2, AUTHOR1, ENTITYID1);
		comment2 = manager.create(COMMENTTEXT3, AUTHOR1, ENTITYID2);
		comment3 = manager.create(COMMENTTEXT4, AUTHOR1, ENTITYID1);
	}
	
	@Test
	public void readComment(){
		Comment commentReaded = manager.readComment(comment.getId());
		Assert.assertTrue(commentReaded.getAuthor().compareTo(AUTHOR1) == 0);
		
		commentReaded = manager.readComment(comment1.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT2) == 0);
		
		commentReaded = manager.readComment(comment2.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT3) == 0);
		
		commentReaded = manager.readComment(comment3.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT4) == 0);
	}
	
	@Test
	public void deleteComment(){
		Comment commentReaded = manager.delete(comment.getId(), comment.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
		
		commentReaded = manager.delete(comment1.getId(), comment1.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
		
		commentReaded = manager.delete(comment2.getId(), comment2.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
		
		commentReaded = manager.delete(comment3.getId(), comment3.getAuthor());
		Assert.assertTrue(commentReaded.isDeleted());
	}
	
	@Test
	public void removeByEntity(){
		manager.removeByEntity(ENTITYID1);
		
		Comment commentReaded = manager.readComment(comment2.getId());
		Assert.assertTrue(commentReaded.getText().compareTo(COMMENTTEXT3) == 0);
		
		commentReaded = manager.readComment(comment.getId());
		Assert.assertTrue(commentReaded == null);
	}
	
	@After
	public void removeComment(){
		manager.remove(comment.getId(), comment.getAuthor());
		manager.remove(comment1.getId(), comment1.getAuthor());
		manager.remove(comment2.getId(), comment2.getAuthor());
		manager.remove(comment3.getId(), comment3.getAuthor());
	}
	
}
