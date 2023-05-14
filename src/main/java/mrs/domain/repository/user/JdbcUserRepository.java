package mrs.domain.repository.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mrs.domain.model.RoleName;
import mrs.domain.model.User;

//public interface UserRepository extends JpaRepository<User, String> {
//}

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
 // Userテーブルのデータを1件取得
    public User selectOne(String username) throws DataAccessException {
        //SQL文
        String sql = "SELECT user_id, first_name, last_name, role_name, password FROM usr WHERE user_id = :userId";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", username);
        
        // 1件取得
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, params);
        // 結果返却用の変数
        User usr = new User();
        // 取得したデータを結果返却用の変数にセットしていく
        usr.setUserId((String) map.get("user_id"));
        usr.setPassword((String) map.get("password"));
        usr.setFirstName((String) map.get("first_name"));
        usr.setLastName((String) map.get("last_name"));
        //usr.setRoleName((RoleName)map.get("role_name"));
        usr.setRoleName(RoleName.USER);
        return usr;
    }
}
