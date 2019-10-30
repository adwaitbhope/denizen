package com.township.manager;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {
        Notice.class,
        Wing.class,
        NoticeWing.class,
        Notice.Comment.class,
        Complaint.class,
        Maintenance.class,
        Resident.class,
        Amenity.class,
        AmenityBooking.class,
        MembershipPayment.class,
        Visitor.class,
        Intercom.class,
        SecurityPersonnel.class,
        SecurityDesks.class}, version = 22)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WingDao wingDao();

    public abstract NoticeDao noticeDao();

    public abstract NoticeWingDao noticeWingsDao();

    public abstract CommentDao commentDao();

    public abstract ComplaintDao complaintDao();

    public abstract MaintenanceDao maintenanceDao();

    public abstract ResidentDao residentDao();

    public abstract VisitorDao visitorDao();

    public abstract SecurityDesksDao securityDesksDao();

    public abstract SecurityPersonnelDao securityPersonnelDao();

    public abstract IntercomDao intercomDao();

    public abstract AmenityDao amenityDao();

    public abstract AmenityBookingDao amenityBookingDao();

    public abstract MembershipPaymentDao membershipPaymentDao();
}
