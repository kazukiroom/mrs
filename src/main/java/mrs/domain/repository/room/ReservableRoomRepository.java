package mrs.domain.repository.room;

import java.time.LocalDate;
import java.util.List;

import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;

/*
public interface ReservableRoomRepository extends JpaRepository<ReservableRoom, ReservableRoomId> {
    @Query("SELECT DISTINCT x FROM ReservableRoom x LEFT JOIN FETCH x.meetingRoom WHERE x.reservableRoomId.reservedDate = :date ORDER BY x.reservableRoomId.roomId ASC")
    List<ReservableRoom> findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(@Param("date") LocalDate reservedDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    ReservableRoom findOneForUpdateByReservableRoomId(ReservableRoomId reservableRoomId);
}
*/

public interface ReservableRoomRepository {
    List<ReservableRoom> findByReservableRoomId_reservedDateOrderByReservableRoomId_roomIdAsc(LocalDate reservedDate);
    
    ReservableRoom findOneForUpdateByReservableRoomId(ReservableRoomId reservableRoomId);
}