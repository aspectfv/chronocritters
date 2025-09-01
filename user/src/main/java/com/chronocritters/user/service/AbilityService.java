package com.chronocritters.user.service;

import java.util.List;

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

    public Ability findById(String id) {
        return abilityRepository.findById(id).orElse(null);
    }

    public Ability save(Ability ability) {
        return abilityRepository.save(ability);
    }

    public void deleteById(String id) {
        abilityRepository.deleteById(id);
    }
}
