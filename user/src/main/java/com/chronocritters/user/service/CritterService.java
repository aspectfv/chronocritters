package com.chronocritters.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.chronocritters.lib.model.Critter;
import com.chronocritters.user.repository.CritterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CritterService {
    private final CritterRepository critterRepository;

    public List<Critter> findAll() {
        return critterRepository.findAll();
    }

    public Optional<Critter> findById(String id) {
        return Optional.ofNullable(critterRepository.findById(id).orElse(null));
    }

    public Critter save(Critter critter) {
        return critterRepository.save(critter);
    }

    public void deleteById(String id) {
        critterRepository.deleteById(id);
    }
}
