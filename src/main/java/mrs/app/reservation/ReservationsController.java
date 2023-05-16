package mrs.app.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import mrs.domain.model.ReservableRoom;
import mrs.domain.model.ReservableRoomId;
import mrs.domain.model.Reservation;
import mrs.domain.model.RoleName;
import mrs.domain.model.User;
import mrs.domain.service.reservation.AlreadyReservedException;
import mrs.domain.service.reservation.ReservationService;
import mrs.domain.service.reservation.UnavailableReservationException;
import mrs.domain.service.room.RoomService;
import mrs.domain.service.user.ReservationUserDetails;

@Controller
@RequestMapping("reservations/{date}/{roomId}")
public class ReservationsController {
    @Autowired
    RoomService roomService;
    @Autowired
    ReservationService reservationService;

    @ModelAttribute
    ReservationForm setUpForm() {
        ReservationForm form = new ReservationForm();
        // デフォルト値
        form.setStartTime(LocalTime.of(9, 0));
        form.setEndTime(LocalTime.of(10, 0));
        return form;
    }
    
    // 予約ページ
    // http://localhost:8080/reservations/2023-05-16/1
    @GetMapping
    String reserveForm(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date,
            @PathVariable("roomId") Integer roomId, Model model) {
        
        // 予約可能な部屋IDを取得
        ReservableRoomId reservableRoomId = new ReservableRoomId(roomId, date);
        // 予約を探す
        List<Reservation> reservations = reservationService.findReservations(reservableRoomId);
        // 予約時間のリスト
        List<LocalTime> timeList = Stream.iterate(LocalTime.of(0, 0), t -> t.plusMinutes(30)).limit(24 * 2)
                .collect(Collectors.toList());
        
        // Viewに渡すためにmodelに追加する
        model.addAttribute("room", roomService.findMeetingRoom(roomId));
        model.addAttribute("reservations", reservations);
        model.addAttribute("timeList", timeList);
        
        // 予約ページで表示する
        return "reservation/reserveForm";
    }

    private User dummyUser() {
        User user = new User();
        user.setUserId("taro-yamada");
        user.setFirstName("太郎");
        user.setLastName("山田");
        user.setRoleName(RoleName.USER);
        return user;
    }
    
    // 予約ボタンを押下
    @PostMapping
    String reserve(@Validated ReservationForm form, BindingResult bindingResult, // フォームクラスのルールに沿って入力チェックが行われる
            @AuthenticationPrincipal ReservationUserDetails userDetails, // 認証ユーザ情報を取得する
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, // URLのパラメータを取得して日付型に変換する
            @PathVariable("roomId") Integer roomId, Model model) {
        
        if (bindingResult.hasErrors()) {
            // 入力チェックで何かしらのエラーが発生している場合は予約をせず、元の画面を表示する。
            return reserveForm(date, roomId, model);
        }
        // 予約部屋インスタンスを生成
        ReservableRoom reservableRoom = new ReservableRoom(new ReservableRoomId(roomId, date));
        
        // 予約情報を作る
        Reservation reservation = new Reservation();
        reservation.setStartTime(form.getStartTime());
        reservation.setEndTime(form.getEndTime());
        reservation.setReservableRoom(reservableRoom);
        reservation.setUser(userDetails.getUser());
        
        try {
            // サービス経由で予約情報を登録する
            reservationService.reserve(reservation);
        } catch (UnavailableReservationException | AlreadyReservedException e) {
            // エラーメッセージを取得して表示する
            model.addAttribute("error", e.getMessage());
            return reserveForm(date, roomId, model);
        }
        return "redirect:/reservations/{date}/{roomId}";
    }
    
    // 取消ボタンを押下
    @PostMapping(params = "cancel")
    String cancel(@RequestParam("reservationId") Integer reservationId, @PathVariable("roomId") Integer roomId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, Model model) {
        try {
            Reservation reservation = reservationService.findOne(reservationId);
            reservationService.cancel(reservation);
        } catch (AccessDeniedException e) {
            model.addAttribute("error", e.getMessage());
            return reserveForm(date, roomId, model);
        }
        return "redirect:/reservations/{date}/{roomId}";
    }
}