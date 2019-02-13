package app.honorrollrss.service;

import app.honorrollrss.model.Honor;
import app.honorrollrss.model.Parameters;
import app.honorrollrss.model.Sysconfig;
import app.honorrollrss.repository.SysconfigRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class HonorRollService {


    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    SysconfigRepository repository;


    ObjectMapper mapper;

    @Autowired
    Oauth2client csclient;

    //取得資料 使用org.apache.http.client.HttpClient;
    HttpClient httpClient = HttpClientBuilder.create().build();

    ClientHttpRequestFactory requestFactory
            = new HttpComponentsClientHttpRequestFactory(httpClient);

    RestTemplate restTemplate = new RestTemplate(requestFactory);
    HttpHeaders headers = new HttpHeaders();


    String listaction = new String();

    public List<Honor> getData(String accesstoken) throws OAuthSystemException, OAuthProblemException, IOException, ParseException {

        List<Honor> honorlists = new ArrayList<>();


        Sysconfig sysconfig = repository.findBySn(1);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accesstoken);


        Parameters param = new Parameters();
//        param.setAction("get");
        param.setAction("list-all");
        param.setId(0);

        mapper = new ObjectMapper();
        listaction = mapper.writeValueAsString(param);


//        logger.info(mapper.writeValueAsString(param));
        HttpEntity<String> entity = new HttpEntity<String>(listaction, headers);

        ResponseEntity<String> response = restTemplate.exchange(sysconfig.getHonor_endpoint(), HttpMethod.POST, entity, String.class);

        List<Integer> honorids = getHonorID(response.getBody());

        if (honorids.size() > 6) {
            honorids = honorids.subList(0, 6);
        }
        Collections.shuffle(honorids);
        //從每篇id去找學生
        Map<Integer, List<Honor>> map = new HashMap<>();

        honorids.subList(0, sysconfig.getHonor_items()).forEach(honorid -> {
            List<Honor> honorlist = new ArrayList<>();
            try {
                honorlist = getIDData(sysconfig.getHonor_endpoint(), accesstoken, honorid);
            } catch (IOException e) {
                e.printStackTrace();
            }
            map.put(honorid, honorlist);
        });

        map.forEach((k, idhonorlist) -> {
            idhonorlist.forEach(honor -> honorlists.add(honor));
        });


//        return StringEscapeUtils.unescapeJava(response.getBody());
//        return StringEscapeUtils.unescapeJava(data);
        return honorlists;
    }

    public String getHonorIndex() throws JsonProcessingException {
        Sysconfig sysconfig = repository.findBySn(1);
        Parameters param = new Parameters();
//        param.setAction("get");
        param.setAction("list-all");
        param.setId(0);

        mapper = new ObjectMapper();
        listaction = mapper.writeValueAsString(param);

        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + csclient.getAccesstoken());


//        logger.info(mapper.writeValueAsString(param));
        HttpEntity<String> entity = new HttpEntity<String>(listaction, headers);
        entity = new HttpEntity<String>(listaction, headers);

        ResponseEntity<String> response = restTemplate.exchange(sysconfig.getHonor_endpoint(), HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    //getHonorID 抓取填報日期已截止的, level, title 排除的
    //        {
    //            "id": 1205,
    //                "title": "107學年度第一學期期末讀經會考",
    //                "beg_date": "2019-01-16",
    //                "end_date": "2019-01-17",
    //                "level": "校內活動"
    //        }
    public List<Integer> getHonorID(String data) throws IOException, ParseException {
        Sysconfig sysconfig = repository.findBySn(1);
        List<String> exclusions = Arrays.asList(sysconfig.getExclusion().split(","));

        JsonNode root = mapper.readTree(data);
        JsonNode node = root.get("_embedded").get("honors");
//        node.forEach(honor->logger.info(honor.get("id").asText()));
        List<Integer> honorids = new ArrayList<>();
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        instant = Instant.ofEpochSecond(timestamp);
        ZonedDateTime taipeitime = instant.atZone(ZoneId.of("Asia/Taipei"));  //taipei時區
        String now = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(taipeitime);
        Date today = new SimpleDateFormat("yyyy-MM-dd").parse(now);
//        System.out.println("現在日期:" + DateTimeFormatter.ofPattern("yyyy-MM-dd").format(taipeitime));

        node.forEach(honor -> {

            String enddate = honor.get("end_date").asText();
//            Boolean isTitleExclude = exclusions.stream().anyMatch(keyword -> keyword.equals(honor.get("title").asText()));
            Boolean isLevelExclude = exclusions.stream().anyMatch(keyword -> keyword.equals(honor.get("level").asText()));
            try {
                //找出今天之前填報已截止的榮譽榜
                if (today.after(new SimpleDateFormat("yyyy-MM-dd").parse(enddate))) {
//                    System.out.println(exclusions.stream().anyMatch(keyword->keyword.equals(honor.get("level").asText())));
                    if (!isLevelExclude) {
//                        System.out.println(honor.get("id").asInt());
                        honorids.add(honor.get("id").asInt());
                    }

//                    System.out.println("結束日期" + enddate);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        });

        return honorids;
    }

    //for web, session only used in web
    public String getIDContent(Integer id) throws IOException {
        List<Honor> honorlist = new ArrayList<>();
        Sysconfig sysconfig = repository.findBySn(1);
        headers.set("Authorization", "Bearer " + csclient.getAccesstoken());

        Parameters param = new Parameters();
        param.setAction("get");
        param.setId(id);
        listaction = mapper.writeValueAsString(param);

        HttpEntity<String> entity = new HttpEntity<String>(listaction, headers);

        ResponseEntity<String> response = restTemplate.exchange(sysconfig.getHonor_endpoint(), HttpMethod.POST, entity, String.class);


        return response.getBody();

    }


    public List<Honor> getIDData(String endpoint, String accesstoken, Integer id) throws IOException {

        List<Honor> honorlist = new ArrayList<>();

        headers.set("Authorization", "Bearer " + accesstoken);

        Parameters param = new Parameters();
        param.setAction("get");
        param.setId(id);
        listaction = mapper.writeValueAsString(param);

        HttpEntity<String> entity = new HttpEntity<String>(listaction, headers);

        ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, entity, String.class);
        JsonNode root = mapper.readTree(response.getBody());

        //榮譽榜項目-107年全國客語能力初級認證合格
        String title = root.get("title").asText();
        JsonNode node = root.get("kind");

        Iterator<String> itnode = root.get("kind").fieldNames();
        List<String> kindid = new ArrayList<>();
        while (itnode.hasNext()) {
            kindid.add(itnode.next());
        }

        //範例中 kind的下一層 460, 值未知 所以用以下語法找出
        Optional<String> itemid = kindid.stream().findFirst();
//        logger.info(itemid.get());
        //award 初級認證合格
        String award = node.get(itemid.get()).get("name").asText();
//        logger.info(award);


        //從460往下找到node: data, 以data為node 找出所有stu_honor_id
        Iterator<String> itstunode = node.get(itemid.get()).get("data").fieldNames();
        List<String> stuhonorids = new ArrayList<>();
        while (itstunode.hasNext()) {
            stuhonorids.add(itstunode.next());
//            logger.info(itstunode.next());
        }

        //整理每篇資料
        stuhonorids.forEach(stuhonorid -> {
            JsonNode stuidnode = node.get(itemid.get()).get("data").get(stuhonorid);
//            logger.info(stuidnode.get("name").asText());
            String stuname = stuidnode.get("name").asText();
            String classname = stuidnode.get("class_long_name").asText();
            String instructor = stuidnode.get("teacher_name").asText();
//            Honor(String name, String classname, String item, String award, String instructor)
            honorlist.add(new Honor(stuname, classname, title, award, instructor));
        });


        return honorlist;
    }
}
