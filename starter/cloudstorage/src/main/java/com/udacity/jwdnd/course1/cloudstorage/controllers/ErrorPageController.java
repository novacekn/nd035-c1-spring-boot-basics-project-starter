package com.udacity.jwdnd.course1.cloudstorage.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorPageController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (statusCode != null) {
            Integer code = Integer.valueOf(statusCode.toString());

            if (code == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("statusCode", "404 NOT FOUND");
                model.addAttribute("errorMessage", "The requested resource could not be found.");
            } else if (code == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("statusCode", "500 INTERNAL SERVER ERROR");
                model.addAttribute("errorMessage", "The server has encountered an internal error and was unable to complete your request.");
            }
        }

        return "error";
    }

    @Override
    public String getErrorPath() {
        return null;
    }
}
