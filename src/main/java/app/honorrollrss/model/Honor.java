package app.honorrollrss.model;

public class Honor {

    private String name;
    private String classname;
    private String title;
    private String award;
    private String instructor;

    public Honor(String name, String classname, String title, String award, String instructor) {
        this.name = name;
        this.classname = classname;
        this.title = title;
        this.award = award;
        this.instructor = instructor;
    }

    public String getName() {
        return name;
    }

    public String getClassname() {
        return classname;
    }

    public String getTitle() {
        return title;
    }

    public String getAward() {
        return award;
    }

    public String getInstructor() {
        return instructor;
    }
}
