package mrs.domain.service.reservation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.repository.reservation.JdbcReservationRepository;
import mrs.domain.repository.room.JdbcReservableRoomRepository;

@SuppressWarnings("deprecation")
@Service
@Transactional
public class ReservationService {
    //@Autowired
    //ReservationRepository reservationRepository;
    //@Autowired
    //ReservableRoomRepository reservableRoomRepository;
    
    @Autowired
    JdbcReservationRepository jdbcReservationRepository;
    @Autowired
    JdbcReservableRoomRepository jdbcRservableRoomRepository;

    public List<Reservation> findReservations(ReservableRoomId reservableRoomId) {
        return jdbcReservationRepository.findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId);
    }

    public Reservation reserve(Reservation reservation) {
        ReservableRoomId reservableRoomId = reservation.getReservableRoom().getReservableRoomId();
        // 悲観ロック
        //ReservableRoom reservable = reservableRoomRepository.findOneForUpdateByReservableRoomId(reservableRoomId);
        ReservableRoom reservable = jdbcRservableRoomRepository.findOneForUpdateByReservableRoomId(reservableRoomId);
        if (reservable == null) {
            throw new UnavailableReservationException("入力の日付・部屋の組み合わせは予約できません。");
        }
        // 重複チェック
        boolean overlap = jdbcReservationRepository
                .findByReservableRoom_ReservableRoomIdOrderByStartTimeAsc(reservableRoomId).stream()
                .anyMatch(x -> x.overlap(reservation));
        if (overlap) {
            throw new AlreadyReservedException("入力の時間帯はすでに予約済みです。");
        }
        // 予約情報の登録
        //jdbcReservationRepository.save(reservation);
        jdbcReservationRepository.insertOne(reservation);
        return reservation;
    }

    @PreAuthorize("hasRole('ADMIN') or #reservation.user.userId == principal.user.userId")
    public void cancel(@P("reservation") Reservation reservation) {
        //jdbcReservationRepository.delete(reservation);
        jdbcReservationRepository.deleteOne(reservation);
    }

    public Reservation findOne(Integer reservationId) {
        //return reservationRepository.findById(reservationId);
        return jdbcReservationRepository.selectOne(reservationId);
    }
}