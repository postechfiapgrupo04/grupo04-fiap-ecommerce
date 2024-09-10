package br.com.fiap.item.controller;

import br.com.fiap.item.entity.Item;
import br.com.fiap.item.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Item> findById(@RequestParam("id") Long id) {
        return ResponseEntity.ok().body(itemService.findById(id));
    }

    @GetMapping("/name")
    public Item findByName(@RequestParam("name") String name) {
        return itemService.findByName(name);
    }

    @PostMapping
    public Item save(@RequestBody Item item) {
        return itemService.save(item);
    }

    @PutMapping("/{id}")
    public Item update(@PathVariable Long id, @RequestBody Item item) {
        return itemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        itemService.deleteById(id);
    }

}
