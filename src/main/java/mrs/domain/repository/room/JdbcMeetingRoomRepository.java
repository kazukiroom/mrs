package mrs.domain.repository.room;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import mrs.domain.model.MeetingRoom;


@Repository
@RequiredArgsConstructor
public class JdbcMeetingRoomRepository implements MeetingRoomRepository {
    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MeetingRoom selectOne(Integer roomId) throws DataAccessException {
        //SQL文
        String sql = "SELECT * FROM Meeting_Room WHERE room_id = :roomId";

        //パラメーター
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("roomId", roomId);
        
        // 1件取得
        Map<String, Object> map = jdbcTemplate.queryForMap(sql, params);
        // 結果返却用の変数
        MeetingRoom meetingRoom = new MeetingRoom();
        // 取得したデータを結果返却用の変数にセットしていく
        meetingRoom.setRoomId((Integer) map.get("room_id"));
        meetingRoom.setRoomName((String) map.get("room_name"));
        return meetingRoom;
    }
}