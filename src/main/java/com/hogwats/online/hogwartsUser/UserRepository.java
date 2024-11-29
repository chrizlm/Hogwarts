package com.hogwats.online.hogwartsUser;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<HogwartsUser, Long> {

    Optional<HogwartsUser> findByUsername(String username);

}
