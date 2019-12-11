package mobile.learning.model;

import javax.xml.namespace.NamespaceContext;

public class DataModel {
    private String id_class,username, nameclass, code;
    private String id_join,idclass,username_host,username_client;
    private String id,name,photo,path;
    private String name_class,name_task_dosen,name_task_mahasiswa,url_task,score,absen;
    private String tanggal;

    public DataModel() {
    }

    public DataModel(String id_join,String idclass,String username_host, String username_client, String id_class,String username, String nameclass, String code, String id,String name, String photo, String name_class,String name_task_dosen,String name_task_mahasiswa,String url_task,String score,String absen,String path,String tanggal) {
        this.id_class = id_class;
        this.username = username;
        this.nameclass = nameclass;
        this.code = code;

        this.id_join = id_join;
        this.idclass = idclass;
        this.username_host = username_host;
        this.username_client = username_client;

        this.id = id;
        this.name = name;
        this.photo = photo;
        this.path = path;

        this.name_class = name_class;
        this.name_task_dosen = name_task_dosen;
        this.name_task_mahasiswa = name_task_mahasiswa;
        this.url_task = url_task;
        this.score = score;
        this.absen = absen;

        this.tanggal = tanggal;
    }

    public  String getId_class(){return id_class;}

    public void setId_class(String id_class){this.id_class = id_class;}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNameclass() {
        return nameclass;
    }

    public void setNameclass(String nameclass) {
        this.nameclass = nameclass;
    }

    public  String getCode() {return code;}

    public void setCode(String code){this.code = code;}

    public  String getId_join(){return id_join;}

    public void setId_join(String id_join){this.id_join = id_join;}

    public  String getIdclass(){return idclass;}

    public void setIdclass(String idclass){this.idclass = idclass;}

    public String getUsername_host() {
        return username_host;
    }

    public void setUsername_host(String username_host) {
        this.username_host = username_host;
    }

    public String getUsername_client() {
        return username_client;
    }

    public void setUsername_client(String username_client) { this.username_client = username_client;}

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id;}

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name;}

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) { this.photo = photo;}

    public String getName_class() {
        return name_class;
    }

    public void setName_class(String name_class) { this.name_class = name_class;}

    public String getName_task_dosen() {
        return name_task_dosen;
    }

    public void setName_task_dosen(String name_task_dosen) { this.name_task_dosen = name_task_dosen;}

    public String getName_task_mahasiswa() {
        return name_task_mahasiswa;
    }

    public void setName_task_mahasiswa(String name_task_mahasiswa) { this.name_task_mahasiswa = name_task_mahasiswa;}

    public String getUrl_task() {
        return url_task;
    }

    public void setUrl_task(String url_task) { this.url_task = url_task;}

    public String getScore() {
        return score;
    }

    public void setScore(String score) { this.score = score;}

    public String getAbsen() {
        return absen;
    }

    public void setAbsen(String absen) { this.absen = absen;}

    public String getPath() {return  path;}

    public void setPath(String path) {this.path = path;}

    public String getTanggal() {return  tanggal;}

    public void setTanggal(String tanggal) {this.tanggal = tanggal;}
}
