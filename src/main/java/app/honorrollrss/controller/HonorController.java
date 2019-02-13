package app.honorrollrss.controller;

import app.honorrollrss.model.Honor;
import app.honorrollrss.service.HonorRollService;
import app.honorrollrss.service.Oauth2Service;
import app.honorrollrss.service.Oauth2client;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class HonorController {

    @Autowired
    Oauth2client csclient;

    @Autowired
    Oauth2Service oauthservice;

    @Autowired
    HonorRollService honorrollservice;

    @RequestMapping("/honor")
    public String getHonor() throws OAuthProblemException, OAuthSystemException, IOException {
        if (StringUtils.isBlank(csclient.getAccesstoken())) {
            csclient.setAccesstoken(oauthservice.getAccesstoken());
        }
        return StringEscapeUtils.unescapeJava(honorrollservice.getHonorIndex());


    }


    @RequestMapping("/honor/{id}")
    public String getHonorId(@PathVariable("id") Integer id) throws OAuthProblemException, OAuthSystemException, IOException {
        if (StringUtils.isBlank(csclient.getAccesstoken())) {
            csclient.setAccesstoken(oauthservice.getAccesstoken());
        }

//        getIDData(String endpoint, String accesstoken, Integer id)
       return  StringEscapeUtils.unescapeJava(honorrollservice.getIDContent(id));
    }


}
