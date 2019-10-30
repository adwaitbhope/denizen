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
        Visitor.class,
        Amenity.class,
        AmenityBooking.class,
        MembershipPayment.class,
        FinancesCredit.class,
        FinancesDebit.class},
        version = 21)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WingDao wingDao();

    public abstract NoticeDao noticeDao();

    public abstract NoticeWingDao noticeWingsDao();

    public abstract CommentDao commentDao();

    public abstract ComplaintDao complaintDao();

    public abstract MaintenanceDao maintenanceDao();

    public abstract ResidentDao residentDao();

    public abstract VisitorDao visitorDao();

    public abstract AmenityDao amenityDao();

    public abstract AmenityBookingDao amenityBookingDao();

    public abstract MembershipPaymentDao membershipPaymentDao();

    public abstract FinancesCreditDao financesCreditDao();

    public abstract FinancesDebitDao financesDebitDao();
}
