package br.com.fiap.item.service;

import br.com.fiap.item.entity.Item;
import br.com.fiap.item.exceptions.EntityNotFoundException;
import br.com.fiap.item.repository.ItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Item update(Long id, Item item) {
        return (Item) updateHelper(
                itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item not found: " + id)),
                item
        );
    }

    public Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(Item.class, id));
    }

    public Item findByName(String name) {
        return itemRepository.findByName(name).orElseThrow(() -> new EntityNotFoundException("Item not found: " + name));
    }

    public void deleteById(Long id) {
        itemRepository.delete(
                itemRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Item not found: " + id))
        );
    }
}
