package com.chronocritters.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.Ability;
import com.chronocritters.user.repository.AbilityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AbilityService {
    private final AbilityRepository abilityRepository;

    public List<Ability> findAll() {
        return abilityRepository.findAll();
    }

    public Optional<Ability> findById(String id) {
        return Optional.ofNullable(abilityRepository.findById(id).orElse(null));
    }

    public Ability save(Ability ability) {
        return abilityRepository.save(ability);
    }

    public void deleteById(String id) {
        abilityRepository.deleteById(id);
    }
}
