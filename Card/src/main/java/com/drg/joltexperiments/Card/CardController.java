package com.drg.joltexperiments.Card;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private CardRepository cardRepository;

    @GetMapping
    public List<Card> getAllCards() {
        return cardRepository.findAll();
    }

    @GetMapping("/{id}")
    public Card getCardById(@PathVariable Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id " + id));
    }

    @PostMapping
    public Card createCard(@RequestBody Card card) {
        return cardRepository.save(card);
    }

    @PutMapping("/{id}")
    public Card updateCard(@PathVariable Long id, @RequestBody Card updatedCard) {
        Card existingCard = cardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Card not found with id " + id));
        existingCard.setCardNumber(updatedCard.getCardNumber());
        existingCard.setCardHolderName(updatedCard.getCardHolderName());
        existingCard.setExpiryDate(updatedCard.getExpiryDate());
        existingCard.setCvv(updatedCard.getCvv());
        existingCard.setCustomerId(updatedCard.getCustomerId()); // Update customer ID reference
        return cardRepository.save(existingCard);
    }

    @DeleteMapping("/{id}")
    public void deleteCard(@PathVariable Long id) {
        cardRepository.deleteById(id);
    }
}
