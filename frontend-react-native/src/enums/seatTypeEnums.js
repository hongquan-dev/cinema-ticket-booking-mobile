export const SeatType = {
  STANDARD: {
    value: "STANDARD",
    label: "Ghế thường",
    color: "bg-blue-500",
    textColor: "text-blue-500",
    borderColor: "border-blue-500/30",
    bgColor: "bg-blue-500/10",
  },
  VIP: {
    value: "VIP",
    label: "Ghế VIP",
    color: "bg-yellow-500",
    textColor: "text-yellow-500",
    borderColor: "border-yellow-500/30",
    bgColor: "bg-yellow-500/10",
  },
  SWEETBOX: {
    value: "SWEETBOX",
    label: "Ghế đôi (Sweetbox)",
    color: "bg-pink-500",
    textColor: "text-pink-500",
    borderColor: "border-pink-500/30",
    bgColor: "bg-pink-500/10",
  },
};

export const getSeatTypeInfo = (seatType) => {
  return SeatType[seatType] || SeatType.STANDARD;
};
