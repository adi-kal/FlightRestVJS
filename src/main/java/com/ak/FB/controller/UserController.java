package com.ak.FB.controller;
import com.ak.FB.model.Member; // ✅ your own model
//import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ak.FB.service.UserManager;

@RestController
//@CrossOrigin("*")
@RequestMapping("/v1/api")
public class UserController {
    
    private UserManager uManager;
    
    public UserController(UserManager um){
        this.uManager = um;
    }

    @GetMapping("/user/{email}/fetch")
    public Map<String,Object> fetchUser_data(@PathVariable(name = "email") String email){
        return uManager.fetchUsr_data(email);
    }
    
    @PostMapping("/user/signup")
    public Map<String,Object> newUserSign_up(@RequestBody Map<Object,Object> data){

        Map<String,Object> returnObj = new HashMap<>();

        if(uManager.newSign_up(data) == 200){
            returnObj.put("status", 200);
            returnObj.put("created", true);
            return returnObj;
        }
        
        returnObj.put("status", 501);
        returnObj.put("created", false);
        return returnObj;

    }

@PostMapping("/user/login")
public Map<String, Object> userLog_id(@RequestBody Map<Object, Object> data) {
    Map<String, Object> returnObj = new HashMap<>();

    Member user = uManager.loginUser(data);

    if (user == null) {
        returnObj.put("exist", false);
        returnObj.put("status", 403);
        returnObj.put("message", "Invalid credentials");
        return returnObj;
    }

    returnObj.put("exist", true);
    returnObj.put("status", 200);
    returnObj.put("message", "Login successful");
    returnObj.put("username", user.getUsername()); // ✅ this comes from member table
    return returnObj;
}
   

    @PostMapping("/user/delete")
    public Map<String,Object> adios_user(@RequestBody Map<Object,Object> data){

        Map<String,Object> returnObj = new HashMap<>();

        switch(uManager.deleteUser(data)){
            case 1 -> {
                returnObj.put("status", 1);
                returnObj.put("message", "can not delete user may be user not exist");
                returnObj.put("deleted", false);
                return returnObj;
            }
        }

        returnObj.put("status", 200);
        returnObj.put("message", "user deleted successfully");
        returnObj.put("deleted", true);
        return returnObj;

    }

}
