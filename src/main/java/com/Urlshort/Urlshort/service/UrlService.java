package com.Urlshort.Urlshort.service;

import com.Urlshort.Urlshort.model.Url;
import com.Urlshort.Urlshort.model.UrlDto;
import org.springframework.stereotype.Service;

@Service
public interface UrlService
{
    public Url generateShortLink(UrlDto urlDto);
    public Url persistShortLink(Url url);
    public Url getEncodedUrl(String url);
    public  void  deleteShortLink(Url url);
}