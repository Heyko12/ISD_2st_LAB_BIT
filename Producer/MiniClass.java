/* Описание файла см. в Producer/MiniClass.java
 */

import java.util.Date;

public class MiniClass implements java.io.Serializable {
    private Date date;
    private String userName;
    private String password;

    public MiniClass(Date date, String userName, String password) {
        this.date = date;
        this.userName = userName;
        this.password = password;
    }

    public String getJsoned() {
        return "{\n    \"date\": " + date.toString() + ",\n    \"userName\": " + userName + ",\n    \"password\": " + password + ",\n}";
    }
}