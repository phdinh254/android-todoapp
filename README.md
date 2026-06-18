# Student Planner - Android Java XML SQLite

Ung dung quan ly ke hoach ca nhan danh cho sinh vien tren Android.
De tai khong chi tap trung vao viec hoc, ma mo rong cho cac nhu cau hang ngay
cua sinh vien nhu lam bai tap, di hoc, lam them, on thi, do an, lich hen va
cac viec ca nhan can theo doi.

## Cong nghe

- Java 17
- XML Layout va Material Components
- SQLiteOpenHelper
- SharedPreferences
- RecyclerView
- BottomNavigationView
- AlarmManager va Notification
- ExecutorService cho truy van du lieu nen

## Cau truc source

```text
com.example.personalplanner
├── activity/       Activity dang nhap, ke hoach va nhom ke hoach
├── adapter/        RecyclerView adapter
├── data/
│   ├── local/      SQLite DatabaseHelper
│   └── model/      User, Course, StudyPlan, StudyStatistics
├── fragment/       Tong quan, ke hoach, lich va ho so
├── notification/   Lap lich va hien thi nhac viec
└── utils/          Session va ma hoa mat khau
```

## Thuc the du lieu

- `users`: tai khoan nguoi dung.
- `plan_categories`: nhom ke hoach nhu Hoc tap, Bai tap, Di hoc, Lam them,
  Ca nhan. Bang `courses` cu duoc migration sang bang nay.
- `tasks`: ke hoach sinh vien, gom noi dung, loai ke hoach, nhom, ngay gio,
  gio ket thuc, dia diem, phong/ca, mon/lop lien quan, muc uu tien, thoi
  luong, trang thai, lap lai, nhac truoc va cac truong rieng nhu tien cong
  hoac trang thai da nop bai.

Quan he:

- Mot nguoi dung co nhieu nhom ke hoach.
- Mot nguoi dung co nhieu ke hoach.
- Mot nhom ke hoach co nhieu ke hoach.

## Chuc nang

- Dang ky, dang nhap va luu phien.
- CRUD nhom ke hoach.
- CRUD ke hoach sinh vien.
- Phan loai ke hoach theo nhom: bai tap, di hoc, lam them, hoc tap, ca nhan...
- Gan loai ke hoach: Bai tap, Di hoc, Lam them, Ca nhan, Thi/Kiem tra, Do an.
- Luu thong tin dac thu: dia diem, phong/ca, mon/lop, tien cong, da nop bai.
- Ho tro lap lai hang ngay hoac hang tuan den mot ngay ket thuc.
- Tim kiem va loc theo trang thai, nhom ke hoach.
- Chon muc uu tien va thoi luong du kien.
- Trang thai mo rong: Sap toi, Dang thuc hien, Hoan thanh, Da huy.
- Xem ke hoach theo lich ngay.
- Canh bao trung gio khi them/sua ke hoach cung ngay.
- Nhac lich bang notification, ho tro nhac dung gio hoac truoc 5/15/30 phut.
- Thong ke so nhom, so ke hoach, tien do, tong gio du kien, bai tap, buoi hoc,
  ca lam them va ke hoach qua han.
- Dark mode theo he thong.

## Chay project

1. Mo thu muc project bang Android Studio.
2. Chon JDK 17 cho Gradle.
3. Sync Gradle.
4. Chay tren thiet bi hoac emulator API 23 tro len.
5. Tren Android 13 tro len, cho phep quyen thong bao de dung nhac lich.

Build bang terminal:

```powershell
.\gradlew.bat clean assembleDebug
```

APK debug duoc tao tai:

```text
app/build/outputs/apk/debug/app-debug.apk
```
