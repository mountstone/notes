package com.notes.api;

import com.notes.exception.InvalidRequestException;
import com.notes.exception.ResourceNotFoundException;
import com.notes.model.User;
import com.notes.repository.UserRepository;
import com.notes.request.LoginParam;
import com.notes.request.RegisterParam;
import com.notes.service.EncryptService;
import com.notes.service.JwtService;
import com.notes.service.UserService;
import com.notes.user.UserData;
import com.notes.user.UserWithToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UsersApi {

    private UserService userService;

    private EncryptService encryptService;

    private JwtService jwtService;

    private UserRepository userRepository;

    @Autowired
    public UsersApi(
            UserRepository userRepository,
            UserService userService,
            EncryptService encryptService,
            JwtService jwtService
    ){
        this.userRepository=userRepository;
        this.userService=userService;
        this.encryptService=encryptService;
        this.jwtService=jwtService;
    }
    @GetMapping(path = {"/{id}"})
    public ResponseEntity getUser(@PathVariable("id") String id){

        Optional<User> optional=userRepository.findById(id);
        if(!optional.isPresent()){
            throw new ResourceNotFoundException();
        }

        UserData userData=new UserData(optional.get());

        return ResponseEntity.status(200).body(
                new HashMap<String, Object>() {{
                    put("user", userData);
                }}
        );
    }

    @DeleteMapping(path = {"/{id}"})
    public ResponseEntity removeUser(@PathVariable("id") String id){
        Optional<User> optional=userRepository.findById(id);
        if(!optional.isPresent()){
            throw new ResourceNotFoundException();
        }
        this.userRepository.delete(optional.get());

        return ResponseEntity.status(200).body("");
    }

    @PostMapping(path={"/login"})
    public ResponseEntity login(@Valid @RequestBody LoginParam loginParam, BindingResult bindingResult){

        Optional<User> optional= userRepository.findByUsername(loginParam.getUsername());

        if(optional.isPresent() && encryptService.check(
                loginParam.getPassword(),optional.get().getPassword()
        )){
            User user= optional.get();

            String token=this.jwtService.toToken(user);
            return ResponseEntity.status(200).body(
                    userResponse(new UserWithToken(
                            user,token
                    ))
            );
        }else{
            bindingResult.rejectValue("password","INVALID", "invalid username or password");
            throw new InvalidRequestException(bindingResult);
        }
    }

    @PostMapping
    public ResponseEntity addUser(@Valid @RequestBody RegisterParam registerParam,
                                  BindingResult bindingResult){


        checkInput(registerParam, bindingResult);

        User user=new User();
        user.setId(UUID.randomUUID().toString());
        user.setUsername(registerParam.getUsername());
        user.setPassword(encryptService.encrypt(registerParam.getPassword()));

        this.userRepository.save(user);

        String token=this.jwtService.toToken(user);

        return ResponseEntity.status(201).body(
                userResponse(new UserWithToken(
                        user,token
                ))
        );
    }

    private void checkInput(@Valid @RequestBody RegisterParam registerParam, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
        Optional<User> optional=userRepository.findByUsername(registerParam.getUsername());

        if (optional.isPresent()) {
            bindingResult.rejectValue("username", "DUPLICATED", "duplicated username");
        }

        if (bindingResult.hasErrors()) {
            throw new InvalidRequestException(bindingResult);
        }
    }

    private Map<String, Object> userResponse(UserWithToken userWithToken) {
        return new HashMap<String, Object>() {{
            put("user", userWithToken);
        }};
    }
}
