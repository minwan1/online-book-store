package com.book.member;


import com.book.member.dto.MemberResponse;
import com.book.member.dto.MemberSignupRequest;
import com.book.member.service.MemberHelperService;
import com.book.member.service.MemberSignUpService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberSignUpService memberSignUpService;
    private final MemberHelperService memberHelperService;

    @PostMapping
    public ResponseEntity signUpMember(@RequestBody @Valid final MemberSignupRequest request, final BindingResult errors){

        if(errors.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        final MemberResponse member = new MemberResponse(memberSignUpService.signUp(request));
        return new ResponseEntity<>(member, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity findMember(@PathVariable final long id){
        return new ResponseEntity<>(memberHelperService.findById(id),HttpStatus.OK);
    }

    @GetMapping
    public List<MemberResponse> findMembers(){
        return MemberResponse.of(memberHelperService.findByAll());
    }



}
