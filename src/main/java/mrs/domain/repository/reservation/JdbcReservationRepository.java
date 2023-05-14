package mrs.domain.repository.reservation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.model.RoleName;
import mrs.domain.model.User;


@Repository
@RequiredArgsConstructor
public class JdbcReservationRepository implements ReservationRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(ReservableRoomId reservableRoomId) {
        String sql = "SELECT r.*, u.* FROM reservation r LEFT JOIN usr u ON r.user_id = u.user_id WHERE r.room_id = :roomId AND r.reserved_date = :reservedDate ORDER BY start_time ASC";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roomId", reservableRoomId.getRoomId());
        params.addValue("reservedDate", reservableRoomId.getReservedDate());
        return jdbcTemplate.query(sql, params, new ReservationRowMapper());
    }
    
    private static class ReservationRowMapper implements RowMapper<Reservation>{
        
        @Override
        public Reservation mapRow(ResultSet rs, int rowNum) throws SQLException {
            Reservation reservation = new Reservation();
            reservation.setReservationId(rs.getInt("reservation_id"));
            reservation.setStartTime(rs.getTime("start_time").toLocalTime());
            reservation.setEndTime(rs.getTime("end_time").toLocalTime());
            ReservableRoomId reservableRoomId = new ReservableRoomId(
                    rs.getInt("room_id"), 
                    rs.getDate("reserved_date").toLocalDate());
            reservation.setReservableRoom(new ReservableRoom(reservableRoomId));
            
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setRoleName(RoleName.USER);
            reservation.setUser(user);
            
            return reservation;
        }

    }
    
    // reservationテーブルにデータを1件insert.
    public int insertOne(Reservation reservation) throws DataAccessException {
        // 1件登録
        String sql = "INSERT INTO reservation(end_time,"
                +   " start_time,"
                +   " reserved_date,"
                +   " room_id,"
                +   " user_id)"
                + " VALUES(:endTime,"
                +   " :startTime,"
                +   " :reservedDate,"
                +   " :roomId,"
                +   " :userId)";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("endTime", reservation.getEndTime())
                .addValue("startTime", reservation.getStartTime())
                .addValue("reservedDate", reservation.getReservableRoom().getReservableRoomId().getReservedDate())
                .addValue("roomId", reservation.getReservableRoom().getReservableRoomId().getRoomId())
                .addValue("userId", reservation.getUser().getUserId());

        //SQL実行
        return jdbcTemplate.update(sql, params);
    }

    // reservationテーブルのデータを1件取得
    public Reservation selectOne(Integer reservationId) throws DataAccessException {
        //SQL文
        String sql = "SELECT * FROM reservation WHERE reservation_id = :reservationId";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("reservationId", reservationId);
        
        // 1件取得
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, params);
        // 結果返却用の変数
        Reservation reservation = new Reservation();
        // 取得したデータを結果返却用の変数にセットしていく
        reservation.setReservationId((Integer) map.get("reservation_id"));
        //reservation.setStartTime((LocalTime) map.get("start_time"));
        //reservation.setEndTime((LocalTime) map.get("end_time"));
        //reservation.setReservableRoom((ReservableRoom) map.get("reservableRoom"));
        //ReservableRoomId reservableRoomId = new ReservableRoomId(
        //        (Integer)map.get("room_id"),
        //        (LocalDate)map.get("reserved_date"));
        //reservation.setReservableRoom(new ReservableRoom(reservableRoomId));
        User user = new User();
        user.setUserId((String) map.get("user_id"));
        reservation.setUser(user);
        
        return reservation;
    }

    /*
    // reservationテーブルの全データを取得（未使用）
    @Override
    public List<Reservation> selectMany() throws DataAccessException {
        //SQL文
        String sql = "SELECT * FROM reservation";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource();

        //SQL実行
        List<Map<String, Object>> getList = jdbcTemplate.queryForList(sql, params);

        //結果返却用のList
        List<Reservation> reservationList = new ArrayList<>();

        //取得データ分loop
        for(Map<String, Object> map: getList) {

            //reservationインスタンスの生成
            Reservation reservation = new Reservation();

            //reservationインスタンスに取得したデータをセットする
            reservation.setReservationId((Integer) map.get("reservationId"));
            reservation.setStartTime((LocalTime) map.get("startTime"));
            reservation.setEndTime((LocalTime) map.get("endTime"));
            reservation.setReservableRoom((ReservableRoom) map.get("reservableRoom"));
            reservation.setUser((User) map.get("user"));

            //Listに追加
            reservationList.add(reservation);
        }

        return reservationList;
    }

    // reservationテーブルを1件更新（未使用）
    public int updateOne(Reservation reservation) throws DataAccessException {
        String sql = "UPDATE reservation SET end_time = :endTime,"
                +   " start_time = :startTime,"
                +   " reserved_date = :reservedDate,"
                +   " room_id = :roomId,"
                +   " user_id = :userId"
                +   " WHERE reservation_id = :reservationId";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("endTime", reservation.getEndTime())
                .addValue("startTime", reservation.getStartTime())
                .addValue("reservedDate", reservation.getReservableRoom().getReservableRoomId().getReservedDate())
                .addValue("roomId", reservation.getReservableRoom().getReservableRoomId().getRoomId())
                .addValue("userId", reservation.getUser().getUserId())
                .addValue("reservationId", reservation.getReservationId());

        //SQL実行
        return jdbcTemplate.update(sql, params);
    }
    */

    // reservationテーブルを1件削除
    public int deleteOne(Reservation reservation) throws DataAccessException {
        // 1件削除
        String sql = "DELETE FROM reservation WHERE reservation_id = :reservation_id";
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("reservation_id", reservation.getReservationId());
        return jdbcTemplate.update(sql, paramMap);
    }
}