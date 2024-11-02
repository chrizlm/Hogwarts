package com.hogwats.online.hogwartsUser;

import com.hogwats.online.system.exception.ObjectNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    //findAllUsers
    public List<HogwartsUser> findAllUsers(){
        return this.userRepository.findAll();
    }

    //findUserById
    public HogwartsUser findUserById(Long userId){
        return this.userRepository.findById(userId)
                .orElseThrow(()->new ObjectNotFoundException("User",userId));
    }

    //addUser
    public HogwartsUser addUser(HogwartsUser hogwartsUser){
        return this.userRepository.save(hogwartsUser);
    }

    //updateUser
    public HogwartsUser updateUser(Long userId, HogwartsUser hogwartsUser){
        return this.userRepository.findById(userId)
                .map(foundUser ->{
                    foundUser.setUsername(hogwartsUser.getUsername());
                    foundUser.setEnabled(hogwartsUser.isEnabled());
                    foundUser.setRoles(hogwartsUser.getRoles());
                    return this.userRepository.save(foundUser);
                })
                .orElseThrow(()-> new ObjectNotFoundException("User",userId));
    }

    //deleteUser
    public void deleteUser(Long userId){
        this.userRepository.findById(userId).orElseThrow(
                () -> new ObjectNotFoundException("User", userId)
        );
        this.userRepository.deleteById(userId);
    }

    //changePassword
    public void changePassword(Long userId){

    }
}
