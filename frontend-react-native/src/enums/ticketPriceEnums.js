export const SeatType = {
    STANDARD: { value: "STANDARD", label: "Ghế thường" },
    VIP: { value: "VIP", label: "Ghế VIP" },
    SWEETBOX: { value: "SWEETBOX", label: "Ghế đôi (Sweetbox)" },
    // TEST: { value: "TEST", label: "Ghế thử nghiệm" },
};

export const MovieFormat = {
    _2D: { value: "2D", label: "Phim 2D" },
    _3D: { value: "3D", label: "Phim 3D" },
    IMAX: { value: "IMAX", label: "Phim IMAX" },
    SCREENX: { value: "SCREENX", label: "Phim SCREENX" },
    LASER_CINEMA: { value: "LASER_CINEMA", label: "Phim LASER CINEMA" },
    GOLD_CLASS: { value: "GOLD_CLASS", label: "Hạng thương gia" },
    // TEST: { value: "TEST", label: "Định dạng thử nghiệm" },
};

export const DayType = {
    WEEKDAY: { value: "WEEKDAY", label: "Ngày trong tuần (T2-T5)" },
    WEEKEND: { value: "WEEKEND", label: "Cuối tuần (T6-CN)" },
    HOLIDAY: { value: "HOLIDAY", label: "Ngày lễ" },
    // TEST: { value: "TEST", label: "Ngày thử nghiệm" }
};

export const CustomerType = {
    ADULT: { value: "ADULT", label: "Người lớn" },
    STUDENT: { value: "STUDENT", label: "Học sinh/ Sinh viên" },
    CHILD: { value: "CHILD", label: "Trẻ em (Dưới 12 tuổi)" },
    ELDER: { value: "ELDER", label: "Người cao tuổi (Trên 60 tuổi)" },
    // TEST: { value: "TEST", label: "Khách hàng thử nghiệm" }
};

// Add style configurations for consistency
export const DAY_TYPE_CONFIG = {
    WEEKDAY: { label: "Ngày trong tuần", style: "bg-slate-50 text-slate-600 border-slate-200" },
    WEEKEND: { label: "Cuối tuần", style: "bg-indigo-50 text-indigo-600 border-indigo-200" },
    HOLIDAY: { label: "Ngày lễ", style: "bg-red-50 text-red-600 border-red-200" },
};

export const CUSTOMER_TYPE_CONFIG = {
    ADULT: { label: "Người lớn", style: "bg-gray-100 text-gray-700 border-gray-300" },
    STUDENT: { label: "Sinh viên", style: "bg-green-50 text-green-600 border-green-200" },
    CHILD: { label: "Trẻ em", style: "bg-cyan-50 text-cyan-600 border-cyan-200" },
    ELDER: { label: "Người cao tuổi", style: "bg-orange-50 text-orange-600 border-orange-200" },
};
