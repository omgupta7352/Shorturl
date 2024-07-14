package com.Urlshort.Urlshort.controller;

import com.Urlshort.Urlshort.model.Url;
import com.Urlshort.Urlshort.model.UrlDto;
import com.Urlshort.Urlshort.model.UrlErrorResponseDto;
import com.Urlshort.Urlshort.service.UrlService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class UrlController
{
    @Autowired
    private UrlService urlService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/generate")
    public String generateShortLink(@RequestParam("url") String url,
                                    @RequestParam(value = "expiration", required = false) String expiration,
                                    Model model)
    {
        UrlDto urlDto = new UrlDto();
        urlDto.setUrl(url);
        urlDto.setExpirationDate(expiration);

        Url urlToRet = urlService.generateShortLink(urlDto);

        if(urlToRet != null)
        {
        	String shortUrl = "http://localhost:8080/" + urlToRet.getShortLink();
            model.addAttribute("shortUrl", shortUrl);
        }
        else
        {
            model.addAttribute("error", "There was an error processing your request. Please try again.");
        }
        return "index";
    }

    @GetMapping("/{shortLink}")
    public String redirectToOriginalUrl(@PathVariable String shortLink, HttpServletResponse response, Model model) throws IOException {

        if(StringUtils.isEmpty(shortLink))
        {
            model.addAttribute("error", "Invalid URL");
            return "index";
        }

        Url urlToRet = urlService.getEncodedUrl(shortLink);

        if(urlToRet == null)
        {
            model.addAttribute("error", "URL does not exist or it might have expired!");
            return "index";
        }

        if(urlToRet.getExpirationDate().isBefore(LocalDateTime.now()))
        {
            urlService.deleteShortLink(urlToRet);
            model.addAttribute("error", "URL Expired. Please try generating a fresh one.");
            return "index";
        }

        response.sendRedirect(urlToRet.getOriginalUrl());
        return null;
    }
}
