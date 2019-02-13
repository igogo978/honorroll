package app.honorrollrss.controller;

import app.honorrollrss.model.Honor;
import app.honorrollrss.service.HonorRollService;
import app.honorrollrss.service.Oauth2Service;
import app.honorrollrss.service.Oauth2client;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class IndexController {

    @Autowired
    HonorRollService honorrollservice;

    @Autowired
    Oauth2Service oauthservice;

    @Autowired
    Oauth2client csclient;


    @RequestMapping("/")
    public List<Honor> index() throws OAuthProblemException, OAuthSystemException, IOException, ParseException {

        List<Honor> honorlists = new ArrayList<>();
        //取得accesstoken
        csclient.setAccesstoken(oauthservice.getAccesstoken());
        honorlists = honorrollservice.getData(csclient.getAccesstoken());
        return honorlists;
    }
}
