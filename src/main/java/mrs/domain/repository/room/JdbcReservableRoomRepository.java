package mrs.domain.repository.room;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import mrs.domain.model.MeetingRoom;
import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;


@Repository
public class JdbcReservableRoomRepository implements ReservableRoomRepository {
    
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<ReservableRoom> findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(LocalDate reservedDate) {
        String sql = "SELECT DISTINCT r.*, m.* FROM Reservable_Room r LEFT JOIN Meeting_Room m ON r.room_id = m.room_id WHERE r.reserved_date = :date ORDER BY r.room_id ASC";
        MapSqlParameterSource params = new MapSqlParameterSource("date", reservedDate);
        return jdbcTemplate.query(sql, params, new ReservableRoomRowMapper());
    }

    @Override
    public ReservableRoom findOneForUpdateByReservableRoomId(ReservableRoomId reservableRoomId) {
        String sql = "SELECT r.*, m.* FROM Reservable_Room r INNER JOIN Meeting_Room m ON r.room_id = m.room_id WHERE r.room_id = :roomId AND r.reserved_date = :reservedDate FOR UPDATE";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("roomId", reservableRoomId.getRoomId());
        params.addValue("reservedDate", reservableRoomId.getReservedDate());
        List<ReservableRoom> list = jdbcTemplate.query(sql, params, new ReservableRoomRowMapper());
        return list.isEmpty() ? null : list.get(0);
    }
}

class ReservableRoomRowMapper implements RowMapper<ReservableRoom>{
    
    @Override
    public ReservableRoom mapRow(ResultSet rs, int rowNum) throws SQLException {
        ReservableRoomId reservableRoomId = new ReservableRoomId(
                rs.getInt("room_id"), 
                rs.getDate("reserved_date").toLocalDate());
        
        ReservableRoom reservableRoom = new ReservableRoom(reservableRoomId);
        MeetingRoom meetingRoom = new MeetingRoom();
        meetingRoom.setRoomId(rs.getInt("room_id"));
        meetingRoom.setRoomName(rs.getString("room_name"));
        reservableRoom.setMeetingRoom(meetingRoom);
        
        return reservableRoom;
    }
}