package Funssion.Inforum.swagger.mypage.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
public class Memo{
    private int memoId;
    private String memoTitle;
    private String memoText;
    private String memoColor;
}
