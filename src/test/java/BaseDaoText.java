import cn.smbms.dao.BaseDao;
import org.junit.Test;

import java.sql.Connection;

public class BaseDaoText {

    @Test
    public void test(){
        Connection connection=BaseDao.getConnection();
        if (connection!=null){}
        System.out.println("链接上了");
    }
}
