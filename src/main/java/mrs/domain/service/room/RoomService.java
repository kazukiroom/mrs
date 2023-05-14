package mrs.domain.service.room;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.MeetingRoom;
import mrs.domain.model.ReservableRoom;
import mrs.domain.repository.room.JdbcMeetingRoomRepository;
import mrs.domain.repository.room.JdbcReservableRoomRepository;

@Service
@Transactional
public class RoomService {
    /*
    // リポジトリをフィールドインジェクションする場合
    @Autowired
    ReservableRoomRepository reservableRoomRepository;
    @Autowired
    MeetingRoomRepository meetingRoomRepository;
    */
    
    // サービスをコンストラクタインジェクションする場合
    private final JdbcReservableRoomRepository jdbcReservableRoomRepository;
    private final JdbcMeetingRoomRepository jdbcMeetingRoomRepository;
    
    public RoomService(JdbcReservableRoomRepository jdbcReservableRoomRepository,JdbcMeetingRoomRepository jdbcMeetingRoomRepository){
        this.jdbcReservableRoomRepository = jdbcReservableRoomRepository;
        this.jdbcMeetingRoomRepository = jdbcMeetingRoomRepository;
    }
    

    public List<ReservableRoom> findReservableRooms(LocalDate date) {
        // リポジトリから予約可能な会議室情報を取得し、コントローラーに返却する
        return jdbcReservableRoomRepository.findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(date);
    }

    public MeetingRoom findMeetingRoom(Integer roomId) {
        //return meetingRoomRepository.findById(roomId);
        return jdbcMeetingRoomRepository.selectOne(roomId);
    }
}