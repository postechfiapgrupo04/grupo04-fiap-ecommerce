package br.com.fiap.item.controller;

import br.com.fiap.item.entity.Item;
import br.com.fiap.item.exceptions.BusinessException;
import br.com.fiap.item.service.ItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/item")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> findAll() {
        return itemService.findAll();
    }

    @GetMapping("/id")
    public Optional<Item> findById(@RequestParam("id") Long id) {
        return itemService.findById(id);
    }

    @GetMapping("/name")
    public Optional<Item> findByName(@RequestParam("name") String name) {
        return itemService.findByName(name);
    }

    @PostMapping
    public Item save(@RequestBody Item item) {
        return itemService.save(item);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item item) throws IllegalAccessException {
        return itemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        itemService.deleteById(id);
    }

}
