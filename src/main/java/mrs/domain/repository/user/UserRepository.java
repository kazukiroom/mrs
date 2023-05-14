package mrs.domain.repository.user;

import org.springframework.dao.DataAccessException;

import mrs.domain.model.User;

//public interface UserRepository extends JpaRepository<User, String> {
//}

public interface UserRepository {
    public User selectOne(String username) throws DataAccessException;
}