package at.htl.mirrorhome;

import at.htl.mirrorhome.user.*;
import at.htl.mirrorhome.user.calendar.CalendarSource;
import at.htl.mirrorhome.user.email.EmailAccount;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;

@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = { MirrorMasterServiceApplication.class, RestTemplate.class }
)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@FixMethodOrder(MethodSorters.JVM)
public class UserControllerTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    private boolean isInitialized = false;

    private User createUserRequest;

    @Before
    public void before() {
        if (!isInitialized) {
            prepareTestData();
            isInitialized = true;
        }
    }

    private void prepareTestData() {

        createUserRequest = new User
                (new Long(-1), new Long(-1),"testuser", "testy.integ@mirror.at",
                        "secret", "d04c3abebd2f74af40840fc8cfc39bb7",
                        UserRole.SUPERUSER, "Test", "Tester", Instant.now(), new UserSettings(),
                        new HashSet<EmailAccount>(), new HashSet<CalendarSource>());

        userRepository.save(createUserRequest);

        MirrorMasterServiceContext context = MirrorMasterServiceApplication.Companion.getMainContext();
        context.setActiveUser(6L); // username: felix
        MirrorMasterServiceApplication.Companion.setMainContext(context);
    }

    /*
     * tests GET api/user/lastCreated
     */
    @Test
    public void getLastCreatedUserEntityTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
            .get( "/api/user/lastCreated")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(-1L)))
            .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.equalTo("testuser"))
        );
    }

    /*
     * tests GET api/user/loggedInUser
     */
    @Test
    public void getLoggedInUserEntityTest() throws Exception {
        mvc.perform(MockMvcRequestBuilders
                .get( "/api/user/loggedInUser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.not(6L)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.equalTo("felix"))
            );
    }





}
