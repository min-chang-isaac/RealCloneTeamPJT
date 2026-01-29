package com.example.join.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.join.entity.User;
import com.example.join.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * returnUrl의 유효성을 검증합니다. Open redirect 공격을 방지하기 위해
     * 상대 경로만 허용하고 외부 URL은 거부합니다.
     * 
     * @param returnUrl 검증할 URL
     * @return 유효한 경우 true, 그렇지 않으면 false
     */
    private boolean isValidReturnUrl(String returnUrl) {
        if (returnUrl == null || returnUrl.isEmpty()) {
            return false;
        }
        
        // URL 길이 제한 (DOS 공격 방지)
        if (returnUrl.length() > 2000) {
            return false;
        }
        
        // 상대 경로만 허용 (/)로 시작해야 함
        if (!returnUrl.startsWith("/")) {
            return false;
        }
        
        // 프로토콜이 포함된 절대 URL 거부
        String lowerUrl = returnUrl.toLowerCase();
        if (lowerUrl.contains("://") || lowerUrl.startsWith("//")) {
            return false;
        }
        
        // 백슬래시를 사용한 우회 시도 차단
        if (returnUrl.contains("\\")) {
            return false;
        }
        
        // URL 인코딩된 슬래시나 백슬래시 차단
        String decodedUrl = returnUrl.toLowerCase();
        if (decodedUrl.contains("%2f") || decodedUrl.contains("%5c")) {
            return false;
        }
        
        return true;
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String returnUrl, Model model) {
        model.addAttribute("returnUrl", returnUrl);
        return "user-login";
    }

    @GetMapping("/signup")
    public String signup(
         @RequestParam(required = false) String returnUrl,  // ✅ 추가
         Model model) {
     model.addAttribute("message", "태형 AI 👍");
     model.addAttribute("returnUrl", returnUrl);  // ✅ 추가
     return "user-signup";
 }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    // ✅ 수정: 회원가입 후 returnUrl 처리
    @PostMapping("/signup")
    public String signupSubmit(
        @RequestParam String username,
        @RequestParam String name,
        @RequestParam String password,
        @RequestParam String passwordConfirm,
        @RequestParam String region,
        @RequestParam String prefecture,
        @RequestParam(required = false) String returnUrl,
        Model model
    ) {
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error","パスワードが一致しません。");
            model.addAttribute("returnUrl", returnUrl);
            return "user-signup";
        }
        userService.registerUser(username, name, password, region, prefecture);
        
        // returnUrl이 유효하면 login 페이지로 리다이렉트할 때 함께 전달
        if (isValidReturnUrl(returnUrl)) {
            String redirectUrl = UriComponentsBuilder.fromPath("/login")
                .queryParam("returnUrl", returnUrl)
                .build()
                .toUriString();
            return "redirect:" + redirectUrl;
        }
        return "redirect:/login";
    }
    
    // ✅ 이 메서드 추가 (빠져있었어요!)
    @PostMapping("/login")
    public String loginSubmit(
        @RequestParam String username,
        @RequestParam String password,
        @RequestParam(required = false) String returnUrl,
        HttpSession session,
        Model model
    ) {
        System.out.println("=== 로그인 시도 ===");
        System.out.println("username: " + username);
        
        User user = userService.login(username, password);
        
        if(user != null) {
            System.out.println("✅ 로그인 성공!");
            session.setAttribute("loginUser", user);
            
            // returnUrl이 유효하면 해당 페이지로 리다이렉트
            if(isValidReturnUrl(returnUrl)) {
                return "redirect:" + returnUrl;
            }
            return "redirect:/board";
        } else {
            System.out.println("❌ 로그인 실패!");
            model.addAttribute("error", "IDまたはパスワードが一致しません");
            model.addAttribute("returnUrl", returnUrl);
            return "user-login";
        }
    }
    
    @PostMapping("/logout")
    public String processLogout(HttpSession session) {
    	if (session.getAttribute("loginUser") == null) {
    		return "redirect:/login";
    	}
    	userService.logout(session);
    	return "redirect:/login";
    }
}
