package com.verifyMe.service;


import com.verifyMe.Entity.IdentityTriggers;
import com.verifyMe.Entity.User;
import com.verifyMe.Repository.IdentityTriggersRepository;
import com.verifyMe.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IdentityTriggersService implements IdentityTriggerServiceI{

    private final IdentityTriggersRepository triggersRepository;
    private final UserRepository userRepository;

    public IdentityTriggersService(IdentityTriggersRepository triggersRepository, UserRepository userRepository) {
        this.triggersRepository = triggersRepository;
        this.userRepository = userRepository;
    }

    // 🔹 Trova tutti gli Identity Triggers di un utente
    @Override
    public List<IdentityTriggers> getTriggersByUserId(Long userId) {
        return triggersRepository.findByUserId(userId);
    }

    // 🔹 Crea un nuovo Identity Trigger per un utente
    @Override
    public IdentityTriggers createTrigger(Long userId, IdentityTriggers trigger) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            trigger.setUser(userOptional.get());
            return triggersRepository.save(trigger);
        } else {
            throw new RuntimeException("User not found!");
        }
    }

    // 🔹 Aggiorna un Identity Trigger
    @Override
    public IdentityTriggers updateTrigger(Long triggerId, IdentityTriggers updatedTrigger) {
        return triggersRepository.findById(triggerId)
                .map(trigger -> {
                    trigger.setName(updatedTrigger.getName());
                    trigger.setAliases(updatedTrigger.getAliases());
                    trigger.setKeywords(updatedTrigger.getKeywords());
                    trigger.setTags(updatedTrigger.getTags());
                    trigger.setSuspectChannels(updatedTrigger.getSuspectChannels());
                    return triggersRepository.save(trigger);
                }).orElseThrow(() -> new RuntimeException("Trigger not found!"));
    }

    // 🔹 Elimina un Identity Trigger
    @Override
    public void deleteTrigger(Long triggerId) {
        triggersRepository.deleteById(triggerId);
    }
}
