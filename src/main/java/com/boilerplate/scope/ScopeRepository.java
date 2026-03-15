package com.boilerplate.scope;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ScopeRepository extends JpaRepository<Scope, Long> {

    Optional<Scope> findByName(String name);

    Set<Scope> findByNameIn(Set<String> names);
}
