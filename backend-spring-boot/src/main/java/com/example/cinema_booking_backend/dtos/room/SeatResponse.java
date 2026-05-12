package com.example.cinema_booking_backend.dtos.room;

import java.math.BigDecimal;
import java.util.UUID;

public class SeatResponse {
    private UUID id;
    private Integer rowIndex;
    private Integer colIndex;
    private String seatNumber;
    private String seatType;
    private String colorCode;
    private BigDecimal extraPrice;
    private Boolean isActive;

    // Constructor to quickly map from Entity
    public SeatResponse(com.example.cinema_booking_backend.models.Seat seat) {
        this.id = seat.getId();
        this.rowIndex = seat.getRowIndex();
        this.colIndex = seat.getColIndex();
        this.seatNumber = seat.getSeatNumber();
        this.seatType = seat.getSeatType();
        this.colorCode = seat.getColorCode();
        this.extraPrice = seat.getExtraPrice();
        this.isActive = seat.getIsActive();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getRowIndex() { return rowIndex; }
    public void setRowIndex(Integer rowIndex) { this.rowIndex = rowIndex; }

    public Integer getColIndex() { return colIndex; }
    public void setColIndex(Integer colIndex) { this.colIndex = colIndex; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatType() { return seatType; }
    public void setSeatType(String seatType) { this.seatType = seatType; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public BigDecimal getExtraPrice() { return extraPrice; }
    public void setExtraPrice(BigDecimal extraPrice) { this.extraPrice = extraPrice; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}