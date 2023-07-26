package com.scrooge.scrooge.repository;

import com.scrooge.scrooge.domain.Avatar;
import com.scrooge.scrooge.domain.Level;
import com.scrooge.scrooge.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LevelRepository levelRepository;

    @Autowired
    AvatarRepository avatarRepository;

    @Test
    public void createUser() {
        User user = new User();
        user.setName("a");
        user.setNickname("a");
        user.setEmail("test@test.com");
        user.setPassword("test");
        user.setExp(10);
        user.setStreak(1);
        user.setWeeklyGoal(5);
        user.setWeeklyConsum(2);

        Level level = new Level();
        level.setLevel(1);
        level.setRequiredExp(100);
        level.setGacha(1);
        level = levelRepository.save(level);
        user.setLevel(level);

        Avatar avatar = new Avatar();
        avatar.setName("test1");
        avatar.setImgAddress("C:\\Users\\SSAFY\\Desktop\\S09P12E106\\Server\\scrooge\\src\\main\\resources\\static\\assets\\sample_avatar.png");
        avatar = avatarRepository.save(avatar);
        user.setMainAvatar(avatar);

        userRepository.save(user);

        User foundUser = userRepository.findById(user.getId()).orElse(null);
        assertNotNull(foundUser);
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getNickname(), foundUser.getNickname());
        assertEquals(user.getEmail(), foundUser.getEmail());
        assertEquals(user.getPassword(), foundUser.getPassword());
        assertEquals(user.getExp(), foundUser.getExp());
        assertEquals(user.getStreak(), foundUser.getStreak());
        assertEquals(user.getWeeklyGoal(), foundUser.getWeeklyGoal());
        assertEquals(user.getWeeklyConsum(), foundUser.getWeeklyConsum());
    }
}
