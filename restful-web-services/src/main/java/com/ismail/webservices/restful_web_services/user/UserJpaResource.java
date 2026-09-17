package com.ismail.webservices.restful_web_services.user;

import com.ismail.webservices.restful_web_services.jpa.UserRepository;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

//A class that exposes User-related REST endpoints.
@RestController
public class UserJpaResource {

    private UserRepository repository;

    public UserJpaResource(UserRepository repository){
        this.repository = repository;
    }

    @GetMapping("jpa/users")
    public List<User> retrieveAllUsers(){
        return repository.findAll();
    }

    @GetMapping("jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id){
        Optional<User> user = repository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("id:" + id);
        }

        WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retrieveAllUsers());

        EntityModel<User> entityModel = EntityModel.of(user.get());
        entityModel.add(link.withRel("all-users"));
        return entityModel;
    }

    @DeleteMapping("jpa/users/{id}")
    public void deleteUserUser(@PathVariable int id){
        repository.deleteById(id);
    }

    @PostMapping("jpa/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        User savedUser = repository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }
}
