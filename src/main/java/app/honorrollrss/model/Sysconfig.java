package app.honorrollrss.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Sysconfig {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer sn;
    private String clientid;
    private String secret;
    private String honor_endpoint;
    private String accesstoken_endpoint;
    private Integer honor_items;
    private Integer screen_lines;
    private Integer line_length;
    private String exclusion;

//    public Sysconfig(Integer sn, String clientid, String secret, String honor_endpoint, String accesstoken_endpoint, Integer honor_items, Integer screen_lines, Integer line_length) {
//        this.sn = sn;
//        this.clientid = clientid;
//        this.secret = secret;
//        this.honor_endpoint = honor_endpoint;
//        this.accesstoken_endpoint = accesstoken_endpoint;
//        this.honor_items = honor_items;
//        this.screen_lines = screen_lines;
//        this.line_length = line_length;
//    }

    public Sysconfig(Integer sn, String clientid, String secret, String honor_endpoint, String accesstoken_endpoint, Integer honor_items, Integer screen_lines, Integer line_length, String exclusion) {
        this.sn = sn;
        this.clientid = clientid;
        this.secret = secret;
        this.honor_endpoint = honor_endpoint;
        this.accesstoken_endpoint = accesstoken_endpoint;
        this.honor_items = honor_items;
        this.screen_lines = screen_lines;
        this.line_length = line_length;
        this.exclusion = exclusion;
    }

    public String getAccesstoken_endpoint() {
        return accesstoken_endpoint;
    }

    public Sysconfig() {
    }

    public Integer getScreen_lines() {
        return screen_lines;
    }

    public Integer getHonor_items() {
        return honor_items;
    }

    public String getClientid() {
        return clientid;
    }

    public String getSecret() {
        return secret;
    }

    public String getHonor_endpoint() {
        return honor_endpoint;
    }

    public Integer getLine_length() {
        return line_length;
    }

    public String getExclusion() {
        return exclusion;
    }
}
