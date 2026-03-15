package com.boilerplate.scope;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScopeService {

    private final ScopeRepository scopeRepository;

    public List<Scope> findAll() {
        return scopeRepository.findAll();
    }

    public Optional<Scope> findByName(String name) {
        return scopeRepository.findByName(name);
    }

    public Set<Scope> findByNames(Set<String> names) {
        return scopeRepository.findByNameIn(names);
    }

    public Scope save(Scope scope) {
        return scopeRepository.save(scope);
    }
}
