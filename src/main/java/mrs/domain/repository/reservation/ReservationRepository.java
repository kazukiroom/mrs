package mrs.domain.repository.reservation;

import java.util.List;

import org.springframework.dao.DataAccessException;

import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
//import org.springframework.data.jpa.repository.JpaRepository;

/*
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
    List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(ReservableRoomId reservableRoomId);
}
*/

public interface ReservationRepository {
    List<Reservation> findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(ReservableRoomId reservableRoomId);

    // reservationテーブルにデータを1件insert
    public int insertOne(Reservation reservation) throws DataAccessException;

    // reservationテーブルのデータを1件取得
    public Reservation selectOne(Integer reservationId) throws DataAccessException;

    // reservationテーブルの全データを取得
    // public List<Reservation> selectMany() throws DataAccessException;

    // reservationテーブルを1件更新
    // public int updateOne(Reservation reservation) throws DataAccessException;

    // reservationテーブルを1件削除
    public int deleteOne(Reservation reservation) throws DataAccessException;

}