# Student Planner

## 1. Giới thiệu

Student Planner là ứng dụng Android hỗ trợ sinh viên quản lý kế hoạch cá nhân hằng ngày. Ứng dụng tập trung vào việc tạo lập, phân loại, theo dõi và nhắc lịch cho các kế hoạch như học tập, bài tập, đi học, làm thêm, đồ án/dự án và công việc cá nhân.

Dự án hoạt động offline, lưu dữ liệu cục bộ trên thiết bị và phù hợp với học phần Lập Trình Thiết Bị Di Động.

## 2. Công nghệ sử dụng

Các công nghệ và thư viện đang được sử dụng trong project:

* Java 17.
* Android XML Layout.
* Android Studio và Gradle.
* SQLite với `SQLiteOpenHelper`.
* `SharedPreferences` để lưu phiên đăng nhập và cấu hình giao diện.
* `RecyclerView` để hiển thị danh sách.
* Material Components: `BottomNavigationView`, `MaterialButton`, `MaterialCardView`, `FloatingActionButton`, `SwitchMaterial`.
* `AlarmManager`, `BroadcastReceiver` và Notification để nhắc lịch.
* PBKDF2 để băm mật khẩu người dùng cục bộ.

Dependency chính trong `app/build.gradle`:

```gradle
implementation 'androidx.appcompat:appcompat:1.7.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'com.google.android.material:material:1.12.0'
```

## 3. Chức năng chính

* Đăng ký, đăng nhập, lưu phiên đăng nhập và đăng xuất.
* Quản lý danh mục/nhóm kế hoạch.
* Thêm, sửa, xóa và xem chi tiết kế hoạch.
* Phân loại kế hoạch theo loại: học tập, bài tập, đi học, làm thêm, cá nhân, thi/kiểm tra, đồ án/dự án.
* Gán mức độ ưu tiên: cao, trung bình, thấp.
* Tìm kiếm và lọc danh sách kế hoạch.
* Kiểm tra trùng thời gian khi thêm hoặc sửa kế hoạch.
* Hiển thị Trang chủ với thống kê tổng kế hoạch, đang thực hiện, đã hoàn thành và quá hạn.
* Hiển thị danh sách kế hoạch hôm nay, sắp xếp theo ưu tiên và giờ bắt đầu.
* Xem kế hoạch theo lịch tháng.
* Nhắc lịch bằng notification trước hoặc đúng thời điểm bắt đầu kế hoạch.
* Hỗ trợ action nhắc lại 5 phút trên notification.
* Chia kế hoạch thành công việc nhỏ/sub-task và tính tiến độ.
* Đánh giá sau khi hoàn thành kế hoạch.
* Tính trạng thái quá hạn ở runtime, không lưu quá hạn như trạng thái cứng trong database.
* Cài đặt giao diện sáng/tối.

## 4. Cấu trúc thư mục

```text
app/
  src/main/
    AndroidManifest.xml
    java/com/example/personalplanner/
      activity/        Các màn hình Activity: đăng nhập, đăng ký, thêm/sửa, chi tiết, danh mục
      adapter/         Adapter cho RecyclerView
      data/local/      DatabaseHelper quản lý SQLite
      data/model/      Model dữ liệu: StudyPlan, PlanCategory, SubTask, PlanReminder...
      fragment/        Trang chủ, lịch, tổng quan, cài đặt, danh sách kế hoạch
      notification/    ReminderScheduler, ReminderReceiver, BootReceiver, ReminderType
      utils/           SessionManager, ThemeManager, PasswordUtils, PlanBusinessRules
    res/
      layout/          Giao diện XML
      drawable/        Icon, shape, background
      mipmap/          Tài nguyên launcher nếu có
      values/          strings, colors, dimens, styles
gradle/                Gradle wrapper files
build.gradle           Cấu hình Gradle cấp project
settings.gradle        Khai báo module project
gradlew, gradlew.bat   Script chạy Gradle
README.md              Tài liệu mô tả project
```

## 5. Hướng dẫn cài đặt và chạy dự án

1. Mở thư mục project bằng Android Studio.
2. Đảm bảo máy đã cài Android SDK. Nếu cần, cấu hình đường dẫn SDK trong `local.properties`.
3. Đồng bộ Gradle bằng Android Studio.
4. Chọn thiết bị thật hoặc Android Emulator.
5. Chạy ứng dụng từ Android Studio.

Lệnh build trên Windows:

```powershell
.\gradlew.bat clean
.\gradlew.bat :app:assembleDebug
```

Lệnh build trên macOS/Linux:

```bash
./gradlew clean
./gradlew :app:assembleDebug
```

APK debug sau khi build nằm tại:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 6. Tài khoản mẫu / dữ liệu mẫu

Dự án không sử dụng tài khoản mẫu cố định. Người dùng có thể đăng ký tài khoản mới trong ứng dụng.

Khi tài khoản mới được tạo, dữ liệu kế hoạch được lưu riêng theo người dùng đang đăng nhập.

## 7. Ghi chú kỹ thuật

* Ứng dụng không dùng backend, Firebase hoặc kết nối mạng để lưu dữ liệu.
* Dữ liệu chính được lưu trong SQLite database cục bộ: `personal_planner.db`.
* Phiên đăng nhập và cấu hình giao diện được lưu bằng `SharedPreferences`.
* Mật khẩu người dùng được băm bằng PBKDF2 trước khi lưu.
* Trạng thái kế hoạch được lưu bằng hằng số số nguyên trong model, ví dụ: chưa bắt đầu, đang thực hiện, hoàn thành, đã hủy.
* Trạng thái quá hạn được tính khi hiển thị dựa trên deadline/thời gian hiện tại và trạng thái hoàn thành.
* Nhắc lịch dùng `AlarmManager.setExactAndAllowWhileIdle()` thay vì `setRepeating()`.
* Android 13 trở lên cần quyền `POST_NOTIFICATIONS` để hiển thị thông báo.
* Android 12 trở lên có thể cần quyền/cấu hình liên quan đến exact alarm tùy thiết bị.
* `BootReceiver` được dùng để khôi phục nhắc lịch còn hiệu lực sau khi thiết bị khởi động lại.

## 8. Thành viên thực hiện

* Họ tên:
* MSSV:
* Lớp:

## 9. Trạng thái hoàn thành

Các chức năng cốt lõi đã hoàn thành ở mức phục vụ báo cáo/chấm điểm:

* Đăng ký, đăng nhập và quản lý phiên cục bộ.
* Quản lý danh mục kế hoạch.
* Thêm, sửa, xóa, xem chi tiết kế hoạch.
* Danh sách kế hoạch, lịch tháng, dashboard và thống kê.
* Kiểm tra trùng lịch.
* Nhắc lịch bằng notification.
* Sub-task, tiến độ và đánh giá sau hoàn thành.
* Tính quá hạn runtime.

Hạn chế và hướng phát triển:

* Chưa đồng bộ dữ liệu đa thiết bị.
* Chưa tích hợp Google Calendar hoặc Firebase.
* Có thể bổ sung biểu đồ thống kê nâng cao hơn.
* Có thể tối ưu thêm giao diện cho nhiều kích thước màn hình.
* Có thể bổ sung chức năng sao lưu/khôi phục dữ liệu.
