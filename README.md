# PROFESSIONAL DICTIONARY (Từ điển Anh Việt chuyên nghiệp)

Bài tập lớn số 1 - Lập trình hướng đối tượng - Đại học Công Nghệ

------------------------------------------------------------------------------
## Tác giả
Từ điển được xây dựng và phát triển bởi ***Trần Bá Hòa*** và ***Ngô Minh Hoàng***
Nhóm: *N1 K62CACLC1*

------------------------------------------------------------------------------
## Phiên bản
Gồm hai phiên bản : **Commandline Dictionary** và **Application Dictionary**

------------------------------------------------------------------------------
## Tính năng
### 1. Commandline version
    + Thêm và xóa từ
    + Hiện danh sách các từ
    + Tra từ điển
    + Gợi ý từ nhập sai (với tỉ lệ tương đương 50%)
    + Dữ liệu từ điển là các từ thông dụng khoảng 4k3 từ
    + Xuất dữ liệu từ điển
    + Dịch văn bản Anh - Việt
    + Scan và dịch ảnh sang tiếng Việt
    + Hoạt động như một ứng dụng chạy theo vòng lặp cơ bản tương tác với người dùng
    + Thể hiện trên console dễ nhìn

### 2. Application version (GUI)
    + Giao diện đẹp mắt, dễ dùng, thân thiện
    + Tra từ, hiện các từ nhanh chóng
    + Ứng dụng chạy nhiều tác vụ đa luồng, mượt mà không giật lag
    + Dữ liệu từ điển database 59k từ
    + Thông tin từ chi tiết, có ví dụ, có cách phát âm
    + Chế độ phát âm từ và lưu trữ phát âm offline, tiết kiệm vùng nhớ
    + Thêm và xóa từ nhanh chóng dễ dàng (Chỉ dành cho user - tài khoản phía dưới)
    + Gợi ý từ nhập sai (với tỉ lệ tương đương 50%)
    + Lịch sử hoạt động, thu hồi từ đã thên hoặc phục hồi từ bị xóa
    + Xuất dữ liệu mới
    + Đánh dấu từ vựng vào danh sách ưa thích
    + Dịch văn bản Anh - Việt và Việt - Anh, chế độ tự động, hoán đồi văn bản
    + Scan ảnh, preview ảnh, dịch ảnh sang tiếng Việt
    + Thông tin tác giả, liên kết tới mạng xã hội
    + Chế độ đăng nhập tài khoản sử dụng tính năng cao cấp, và chế độ dùng thử
    + Cung cấp tiếp cận sử dụng dễ dàng với cả bàn phím và chuột máy tính
    + Và còn một số tính năng khác...
    
------------------------------------------------------------------------------
## Frameworks và thư viện sử dụng
    1. Gradle 
    2. JavaFX + JFoenix
    3. Voicerss_tts - Word's speech 
    4. Google API Translate
    5. JL libraries - Sound converter and player 
    6. Tess4j ORC - Image scanner

------------------------------------------------------------------------------
## Cấu trúc project
    + Được thiết kế theo cấu trúc rõ ràng và phân nhánh cụ thể, dễ sử đổi và tái sử dụng...
    + Sử dụng Git + Bitbucket để lưu trữ và làm việc với dự án
    + Design pattern chính sử dụng: Template Pattern, States Pattern, Singleton
    + Một số thuật toán sử dụng: binary search, filter list, fuzzy search,...
    
Dưới đây là cấu trúc của source code project

```
└── main
├── java
│   └── app
│       ├── AppMain.java
│       ├── CommandMain.java
│       └── dictionaries
│           ├── DictionaryApplication.java
│           ├── DictionaryCommandLine.java
│           ├── StateManager.java
│           ├── states
│           │   ├── BaseState.java
│           │   ├── ContactState.java
│           │   ├── HistoryState.java
│           │   ├── ImgReaderState.java
│           │   ├── LoginState.java
│           │   ├── MainState.java
│           │   ├── MarkedState.java
│           │   ├── TranslateState.java
│           │   └── general
│           │       ├── AddDialog.java
│           │       └── WordDialog.java
│           └── utilities
│               ├── Dictionary.java
│               ├── DictionaryManagement.java
│               └── elements
│                   ├── Algorithms.java
│                   ├── AudioPlayer.java
│                   ├── Effects.java
│                   ├── RelatedWord.java
│                   └── Word.java
├── libraries... (thư viện sử dụng)
└── resources... (tài nguyên application)
```

------------------------------------------------------------------------------
## Cách sử dụng và yêu cầu cài đặt (hiện chỉ dành cho developer)

### Yêu cầu cài đặt 

1. Bạn có thể copy folder Released tới bất kì đâu, nhưng đường dẫn tuyệt đối tới folder phải không chứa kí tự khoảng trắng (Space), nếu không chương trình sẽ báo lỗi! 

2. Vui lòng cái đặt Java JDK 1.8 và set path cũng như JAVA_HOME trước khi chạy Application Version...

**Tài khoản login cho Application version**
```
Username : double_h
Password: double_h
```

**Cách 1: Build main theo phiên bản sử dụng với IDE**
Sử dụng bất kì IDE nào import Gradle project và chọn build với hàm main *(entry point)* của một trong hai phiên bản
```
├── java
│   └── app
│       ├── AppMain.java
│       ├── CommandMain.java
...

Class AppMain -> public static void main(String[] args)
Class CommandMain -> public static void main(String[] args)
```
**Cách 2: Sử dụng file nén Jar có sẵn**
```
Truy cập vào folder ReleasedVersions trong mục chính project...

.
├── ReleasedVersions
│   ├── ApplicationVersion
│   │   ├── ApplicationVersion-1.0-SNAPSHOT.jar
│   │   └── src... (resources of application version)
│   └── CommandlineVersion
│       ├── CommandlineVersion-1.0-SNAPSHOT.jar
│       └── src... (resources of commandline version)

1. Chọn phiên bản bạn muốn chạy nằm trong folder cùng tên
2. Mở Terminal hoặc CMD:
 -> cd "thư mục chứa file jar của phiên bản tương ứng"
 -> java -jar "Tên phiên bản".jar
 
 - Ví dụ: 
    -> cd ApplicationVersion/
    -> java -jar ApplicationVersion-1.0-SNAPSHOT.jar
```

**Cách 3: File cài đặt và sử dụng như ứng dụng thông thường**
```
Đang trong quá trình phát triển và hoàn thiện
```

------------------------------------------------------------------------------
## License
Dự án này thuộc quyền sở hữu của Trần Bá Hòa và Ngô Minh Hoàng

------------------------------------------------------------------------------
## Liên hệ
**Facebook:**
https://www.facebook.com/hoatrana3
và https://www.facebook.com/ngohoang34
