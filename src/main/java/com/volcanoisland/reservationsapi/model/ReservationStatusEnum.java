package com.volcanoisland.reservationsapi.model;

public enum ReservationStatusEnum {
    ACTIVE(0),
    CANCELLED(1);

    Integer id;

    ReservationStatusEnum(Integer id) {
        this.id = id;
    }

    /**
     * Get enum value by it's id.
     * @param id
     * @return ReservationStatusEnum
     */
    public static ReservationStatusEnum getById(Integer id) {
        if (id != null) {
            for (ReservationStatusEnum status : ReservationStatusEnum.values()) {
                if (status.getId().equals(id)) {
                    return status;
                }
            }
        }

        return null;
    }

    public Integer getId() {
        return id;
    }
}
