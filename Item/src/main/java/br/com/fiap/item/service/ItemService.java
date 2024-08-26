package br.com.fiap.item.service;

import br.com.fiap.item.entity.Item;
import br.com.fiap.item.exceptions.BusinessException;
import br.com.fiap.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static br.com.fiap.item.helpers.UpdateHelper.updateHelper;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public Optional<Item> findById(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return Optional.ofNullable(item.orElseThrow(() -> new BusinessException("Item não encontrado")));
    }

    public Optional<Item> findByName(String name) {
        Optional<Item> item = Optional.ofNullable(itemRepository.findByName(name));
        return Optional.ofNullable(item.orElseThrow(() -> new BusinessException("Item não encontrado")));
    }

    public void deleteById(Long id) {
        Optional<Item> item = Optional.ofNullable(itemRepository.findById(id).orElseThrow(() -> new BusinessException("Item não encontrado")));
        item.ifPresent(itemRepository::delete);
    }


    public Item update(Long id, Item item) throws IllegalAccessException {
        Optional<Item> itemOptional = Optional.ofNullable(itemRepository.findById(id).orElseThrow(() -> new BusinessException("Item não encontrado")));
            Item item1 = (Item) updateHelper(itemOptional.get(), item);
            return itemRepository.save(item1);
    }
}
