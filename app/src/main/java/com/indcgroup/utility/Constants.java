package com.indcgroup.utility;

import java.util.ArrayList;

/**
 * Created by thefa on 05/08/2017.
 */

public class Constants {
    public static final String CRYPT_KEY = "Fallen.Angel";
    public static final String TAG = "FA_TAG";

    //Result
    public static final String Error_NoGrantPermission = "Bạn cần cấp quyền để ứng dụng có thể chạy bình thường!";
    public static final String Error_NoInternetConnection = "Không có kết nối mạng hoặc kết nối không ổn định.";
    public static final String Error_MissingRequiredField = "Các trường dữ liệu không được bỏ trống.";
    public static final String Error_CanNotEstimateDistance = "Không xác định.";
    public static final String Error_BigImage = "Dung lượng ảnh quá lớn! Vui lòng chọn ảnh dưới 4 MB làm ảnh đại diện!";
    public static final String Success_SendRecruitment = "Gửi bài đăng thành công!";
    public static final String Success_SignIn = "Đăng nhập thành công!";
    public static final String Success_SignUp = "Đăng ký thành công!";
    public static final String Success_UpdateInfo = "Cập nhật thành công!";

    //Alert
    public static final String Alert_PleaseWait = "Vui lòng chờ...";
    public static final String Alert_GetAddress = "Đang xác định vị trí...";
    public static final String Alert_DownloadArticle = "Đang tải bài viết...";
    public static final String Alert_DownloadUser = "Đang tải thông tin gia sư...";
    public static final String Alert_DownloadRecruitment = "Đang tải bài tuyển dụng...";
    public static final String Alert_DownloadTransaction = "Đang tải thông tin điểm...";
    public static final String Alert_SendRating = "Đang gửi đánh giá...";

    //Confirm
    public static final String Confirm_SendArticle = "Hãy kiểm tra lại nội dung trước khi gửi! Nội dung nên giới thiệu thật chi tiết về kinh nghiệm bản thân. Mỗi lần gửi thành công sẽ sử dụng điểm của bạn.\nBạn chắc chắn muốn gửi?";
    public static final String Confirm_SendRecruitment = "Hãy kiểm tra lại nội dung trước khi gửi! Nội dung nên có lịch, thời gian làm việc cụ thể, giá cả,... cũng như yêu cầu riêng của cá nhân.\nBạn chắc chắn muốn gửi?";
    public static final String Confirm_Exit = "Bạn chắc chắn muốn thoát ứng dụng?";

    //Array
    public static final String[] My_Gender = new String[]{"Nam", "Nữ"};
    public static final String[] My_Position = new String[]{"Sinh viên", "Đã tốt nghiệp", "Giáo viên - Giảng viên"};
    public static final String[] My_Grade = new String[]{"Lớp 1", "Lớp 2", "Lớp 3", "Lớp 4", "Lớp 5", "Lớp 6", "Lớp 7", "Lớp 8", "Lớp 9", "Lớp 10", "Lớp 11", "Lớp 12",
            "Lớp tiếng Anh", "Lớp tiếng Trung", "Lớp tiếng Nhật", "Lớp tiếng Hàn", "Lớp tiếng Pháp", "Lớp tiếng Đức", "Lớp tiếng Nga", "Lớp tiếng Thái",
            "Lớp bổ túc lái xe", "Lớp bơi lội", "Lớp võ thuật", "Lớp âm nhạc", "Lớp hội họa", "Lớp tennis", "Lớp khác"};
    public static final String[] My_Subject = new String[]{"Toán", "Tin học", "Vật lý", "Hóa học", "Sinh học", "Ngữ văn", "Lịch sử", "Địa lý",
            "Tiếng Anh", "Tiếng Trung", "Tiếng Nhật", "Tiếng Hàn", "Tiếng Pháp", "Tiếng Đức", "Tiếng Nga",
            "Võ thuật", "Bơi lội", "Âm nhạc", "Hội họa", "Bóng chuyền", "Bóng rổ", "Môn khác"};

    //ArrayList
    public static ArrayList<String> generateGender() {
        ArrayList<String> result = new ArrayList<>();
        result.add("-- Tất cả --");
        result.add("Nam");
        result.add("Nữ");

        return result;
    }

    public static ArrayList<String> generatePosition() {
        ArrayList<String> result = new ArrayList<>();
        result.add("-- Tất cả --");
        result.add("Sinh viên");
        result.add("Đã tốt nghiệp");
        result.add("Giáo viên - Giảng viên");

        return result;
    }
}
