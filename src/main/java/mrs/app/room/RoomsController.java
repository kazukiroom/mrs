package mrs.app.room;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import mrs.domain.model.ReservableRoom;
import mrs.domain.service.room.RoomService;

// WebSecurityConfigで認証成功したらroomsが呼ばれる
@Controller
@RequestMapping("rooms")
public class RoomsController {
    /*
    // サービスをフィールドインジェクションする場合
    @Autowired
    RoomService roomService;
    */
    
    // サービスをコンストラクタインジェクションする場合
    private final RoomService roomService;
    
    public RoomsController(RoomService roomService){
        this.roomService = roomService;
    }
    
    // リクエストの日付情報を取得して予約可能な会議室情報を取得する
    // http://localhost:8080/rooms/2023-05-10
    @RequestMapping(value = "{date}", method = RequestMethod.GET)
    String listRooms(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @PathVariable("date") LocalDate date, Model model) {
        
        // 日付を渡して予約可能な会議室情報を取得する
        List<ReservableRoom> rooms = roomService.findReservableRooms(date);
        
        // 予約可能会議室情報をmodelにセットしてViewに渡す
        model.addAttribute("roomsList", rooms);
        
        // listRooms.htmlを呼ぶ
        return "room/listRooms";
    }
    
    // ログイン直後は日付情報なし
    // http://localhost:8080/rooms
    @RequestMapping(method = RequestMethod.GET)
    String listRooms(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("date", today);
        return listRooms(today, model);
    }
}