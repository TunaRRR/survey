package org.survey.user.security;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.survey.user.model.Role;
import org.survey.user.model.User;
import org.survey.user.repository.UserRepository;
import org.survey.user.security.UserRepositoryAuthenticationProvider;

/**
 * Test UserRepositoryAuthenticationProvider. Must use SpringJUnit4ClassRunner
 * to enable spring injection. Loaded Spring configuration is defined by
 * ContextConfiguration.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-config-test.xml")
public class UserRepositoryAuthenticationProviderTest {
	@Autowired
	UserRepository userRepository;

	@Before
	public void setUp() {
		userRepository
				.save(new User("test", "test", "test", Role.ROLE_ADMIN));
	}

	@After
	public void tearDown() {
		userRepository.deleteAll();
	}

	@Test
	public void authenticate() {
		UserRepositoryAuthenticationProvider authenticationProvider = new UserRepositoryAuthenticationProvider();
		authenticationProvider.userRepository = userRepository;
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"test", "test");
		Authentication authenticationFromProvider = authenticationProvider
				.authenticate(authentication);
		Assert.assertNotNull(authenticationFromProvider);
		Assert.assertNotNull(authenticationFromProvider.getAuthorities());
		Assert.assertEquals(1, authenticationFromProvider.getAuthorities()
				.size());
		Assert.assertTrue(authenticationFromProvider.getAuthorities().contains(
				new GrantedAuthorityImpl("ROLE_ADMIN")));
	}

	@Test
	public void authenticateWithInvalidUsername() {
		UserRepositoryAuthenticationProvider authenticationProvider = new UserRepositoryAuthenticationProvider();
		authenticationProvider.userRepository = userRepository;
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"invalid", "invalid");
		Authentication authenticationFromProvider = authenticationProvider
				.authenticate(authentication);
		Assert.assertNull(authenticationFromProvider);
	}

	@Test
	public void authenticateWithInvalidPassword() {
		UserRepositoryAuthenticationProvider authenticationProvider = new UserRepositoryAuthenticationProvider();
		authenticationProvider.userRepository = userRepository;
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				"test", "invalid");
		Authentication authenticationFromProvider = authenticationProvider
				.authenticate(authentication);
		Assert.assertNull(authenticationFromProvider);
	}
}