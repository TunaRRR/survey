package org.survey.poll.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.survey.poll.model.Poll;
import org.survey.poll.model.PollComparator;
import org.survey.poll.model.PollFactory;
import org.survey.user.model.User;
import org.survey.user.model.UserFactory;
import org.survey.user.service.UserService;

/**
 * Test UserService. Must use SpringJUnit4ClassRunner to enable spring
 * injection. Loaded Spring configuration is defined by ContextConfiguration.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:poll-service-test.xml")
public class PollServiceImplTest {
    protected static int ENTITY_COUNT = 2;
    protected List<Poll> orginalEntities = new ArrayList<Poll>();
    protected List<Poll> savedEntities = new ArrayList<Poll>();
    @Autowired
    @Qualifier("pollService")
    protected PollService entityService;
    protected PollFactory entityFactory;
    protected PollComparator entityComparator = new PollComparator();
//    @Autowired
//    @Qualifier("userRepository")
//    protected UserRepository userRepository;
    @Autowired
    protected UserService userService;
    private User user;

    @Before
    public void setUp() {
        UserFactory userFactory = new UserFactory();
        user = userFactory.getEntities(1).get(0);
//        user = userRepository.save(user);
        user = userService.create(user);
        entityFactory = new PollFactory(user);
    }

    @After
    public void tearDown() throws SQLException {
        Poll[] users = entityService.findAll();
        if (users != null) {
            for (Poll pollToDelete : users) {
                entityService.delete(pollToDelete.getName());
            }
        }
        userService.delete(user.getUsername());
    }

    @Test
    public void create() {
        orginalEntities = entityFactory.getEntities(ENTITY_COUNT);
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Poll originalEntity = orginalEntities.get(i);
            Poll savedEntity = entityService.create(originalEntity);
            savedEntities.add(savedEntity);
            assertEntity(originalEntity, savedEntity);
        }
    }

    @Test
    public void createWithError() {
        try {
            create();
            entityService.create(orginalEntities.get(0));
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage()
                    .startsWith("Poll already exists: "));
        }
    }

    @Test
    public void update() {
        create();
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Poll foundEntity = entityService.findOne(savedEntities.get(i)
                    .getName());
            Poll updatedEntity = entityFactory.getUpdatedEntity(foundEntity);
            updatedEntity.setId(foundEntity.getId());
            entityService.update(updatedEntity);
            foundEntity = entityService.findOne(savedEntities.get(i).getName());
            assertEntity(updatedEntity, foundEntity);
        }
    }

    @Test
    public void findAll() {
        create();
        Assert.assertEquals(ENTITY_COUNT, entityService.findAll().length);
    }

    @Test
    public void findByOwner() {
        create();
        Assert.assertEquals(
                ENTITY_COUNT,
                entityService.findByOwner(savedEntities.get(0).getOwner()
                        .getUsername()).length);
    }

    @Test
    public void findOne() {
        create();
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Poll originalEntity = orginalEntities.get(i);
            Poll foundEntity = entityService.findOne(originalEntity.getName());
            assertEntity(orginalEntities.get(i), foundEntity);
        }
        // TODO how to test a non-existent entity?
    }

    @Test
    public void exists() {
        create();
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Poll entity = orginalEntities.get(i);
            entityService.exists(entity.getName());
        }
        // TODO how to test if exists fails?
        // Assert.assertFalse(entityRepository.exists((ID) new Object()));
    }

    @Test
    public void existsWithNull() {
        // try with non-existent id
        Assert.assertFalse(entityService.exists("nonexistent"));
    }

    @Test
    public void count() {
        create();
        Assert.assertEquals(ENTITY_COUNT, entityService.count());
    }

    @Test
    public void delete() {
        create();
        for (int i = 0; i < ENTITY_COUNT; i++) {
            Poll entity = orginalEntities.get(i);
            entityService.delete(entity.getName());
            Assert.assertFalse(entityService.exists(entity.getName()));
        }
        Assert.assertEquals(0, entityService.count());
    }

    @Test
    public void deleteNonexistent() {
        create();
        entityService.delete("nonexistent");
    }

    public void assertEntity(Poll originalEntity, Poll entity) {
        Assert.assertEquals("originalEntity: " + originalEntity + " entity: "
                + entity, 0, entityComparator.compare(originalEntity, entity));
        Assert.assertNotNull(originalEntity.getQuestions());
        Assert.assertNotNull(entity.getQuestions());
        Assert.assertEquals(originalEntity.getQuestions().size(),
                originalEntity.getQuestions().size());
    }
}