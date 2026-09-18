package com.ismail.webservices.restful_web_services.user;

import com.ismail.webservices.restful_web_services.jpa.PostRepository;
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

    private UserRepository userRepository;
    private PostRepository postRepository;

    public UserJpaResource(UserRepository userRepository, PostRepository postRepository){
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping("jpa/users")
    public List<User> retrieveAllUsers(){
        return userRepository.findAll();
    }

    @GetMapping("jpa/users/{id}")
    public EntityModel<User> retrieveUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
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
        userRepository.deleteById(id);
    }

    @PostMapping("jpa/users")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user){
        User savedUser = userRepository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("jpa/users/{id}/posts")
    public List<Post> retrievePostsForUser(@PathVariable int id){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("id:" + id);
        }
        return user.get().getPosts();
    }

    @PostMapping("jpa/users/{id}/posts")
    public ResponseEntity<Post> createPostForUser(@PathVariable int id, @Valid @RequestBody Post post){
        Optional<User> user = userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("id:" + id);
        }
        post.setUser(user.get());
        Post savedPost = postRepository.save(post);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedPost.getId()).toUri();
        return ResponseEntity.created(location).build();

    }

    @GetMapping("jpa/users/{user_id}/posts/{post_id}")
    public Post retrievePostOfUser(@PathVariable int user_id, @PathVariable int post_id){
        Optional<User> user = userRepository.findById(user_id);
        if(user.isEmpty()){
            throw new UserNotFoundException( "id:" + user_id);
        }
        Optional<Post> post = postRepository.findById(post_id);
        if(post.isEmpty() || post.get().getUser().getId() != user_id){
            throw new PostNotFoundException( "id:" + post_id);
        }
        return post.get();
    }
}
