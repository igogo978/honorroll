package app.honorrollrss.service;


import app.honorrollrss.model.Sysconfig;
import app.honorrollrss.repository.SysconfigRepository;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class Oauth2Service {

    @Autowired
    SysconfigRepository repository;

    public String getAccesstoken() throws OAuthProblemException, OAuthSystemException {
        Sysconfig sysconfig = repository.findBySn(1);

        //取得token
        //取得資料 使用org.apache.http.client.HttpClient;
        HttpClient httpClient = HttpClientBuilder.create().build();

        ClientHttpRequestFactory requestFactory
                = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();

        OAuthClient client = new OAuthClient(new URLConnectionClient());
        OAuthClientRequest request =
                OAuthClientRequest.tokenLocation(sysconfig.getAccesstoken_endpoint())
                        .setGrantType(GrantType.CLIENT_CREDENTIALS)
                        .setClientId(sysconfig.getClientid())
                        .setClientSecret(sysconfig.getSecret())
                        .buildBodyMessage();

       return  client.accessToken(request, "POST", OAuthJSONAccessTokenResponse.class)
                .getAccessToken();
    }
}
