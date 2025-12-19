package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

@RestController
@CrossOrigin
public class ProfileController
{
    private final ProfileDao profileDao;
    private final UserDao userDao;

    public ProfileController(ProfileDao profileDao, UserDao userDao)
    {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @GetMapping("/profile")
    public Profile getProfile()
    {
        User user = getCurrentUser();
        Profile profile = profileDao.getByUserId(user.getId());

        if (profile == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        return profile;
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody Profile profile)
    {
        User user = getCurrentUser();

        // IMPORTANT: don’t trust client user_id — force it to the logged-in user
        profile.setUserId(user.getId());

        Profile existing = profileDao.getByUserId(user.getId());
        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        profileDao.update(profile);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private User getCurrentUser()
    {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userDao.getByUserName(username);
        if (user == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        return user;
    }
}
