package app.honorrollrss;

import app.honorrollrss.model.Honor;
import app.honorrollrss.model.Sysconfig;
import app.honorrollrss.repository.SysconfigRepository;
import app.honorrollrss.service.HonorRollService;
import app.honorrollrss.service.Oauth2Service;
import app.honorrollrss.service.Oauth2client;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;

@SpringBootApplication
public class HonorrollRssApplication implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${config}")
    private String configfile;

    @Autowired
    SysconfigRepository repository;

    @Autowired
    HonorRollService honorroll;

    @Autowired
    Oauth2Service oauthservice;

    @Autowired
    Oauth2client csclient;

    public static void main(String[] args) {

        ConfigurableApplicationContext ctx = SpringApplication.run(HonorrollRssApplication.class, args);
        ctx.close();
    }

    @Override
    public void run(String... args) throws Exception {

        //確認設定檔
        if (new File(String.format("%s/%s", System.getProperty("user.dir"), configfile)).isFile()) {
            //create ObjectMapper instance
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(new File(String.format("%s/%s", System.getProperty("user.dir"), configfile)));
            String clientid = node.get("clientid").asText();
            String secret = node.get("secret").asText();
            String honor_endpoint = node.get("honor_endpoint").asText();
            String accesstoken_endpoint = node.get("accesstoken_endpoint").asText();

            Integer honor_items = node.get("honor_items").asInt();
            Integer screen_lines = node.get("screen_lines").asInt();
            Integer line_length = node.get("line_length").asInt();
            String exclusion = node.get("exclusion").asText();

            repository.save(new Sysconfig(1, clientid, secret, honor_endpoint, accesstoken_endpoint, honor_items, screen_lines, line_length,exclusion));
//            logger.info(repository.findBySn(1).getClientid());


            Sysconfig sysconfig = repository.findBySn(1);

            String listaction = new String();
            String accesstoken = oauthservice.getAccesstoken();

            //抓取資料回來
            List<Honor> honorlist = honorroll.getData(accesstoken);


            String honorlistTxt = String.format("%s/honorlist.txt", System.getProperty("user.dir"));

            PrintWriter writer = new PrintWriter(honorlistTxt);

            System.out.println("寫入檔案");
            honorlist.forEach(honor -> {
                for (int i = 0; i < 3; i++) {
                    if(honor.getTitle().length()> line_length){
//                        System.out.println("超過每行長度");
                        i++;
                    }
                    writer.println(" ");
                }
                writer.println(honor.getTitle());
//                System.out.println(honor.getTitle());
//                System.out.println(honor.getTitle().length());
                writer.println(honor.getAward());
                writer.println(String.format("%s %s", honor.getClassname(), honor.getName()));
                for (int i = 0; i < (screen_lines-3-3); i++) {
                    writer.println(" ");
                }


            });


            writer.close();


        }


    }
}
