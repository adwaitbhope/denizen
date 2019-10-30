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
        Visitor.class,
        SecurityPersonnel.class,
        SecurityDesks.class,
        AdminInfo.class,
        MembershipPayment.class,
        FinancesCredit.class,
        FinancesDebit.class,
        Intercom.class,
        ServiceVendors.class}, version = 24)
public abstract class AppDatabase extends RoomDatabase {
    public abstract WingDao wingDao();

    public abstract NoticeDao noticeDao();

    public abstract NoticeWingDao noticeWingsDao();

    public abstract CommentDao commentDao();

    public abstract ComplaintDao complaintDao();

    public abstract MaintenanceDao maintenanceDao();

    public abstract ResidentDao residentDao();

    public abstract VisitorDao visitorDao();

    public abstract ServiceVendorDao serviceVendorDao();

    public abstract AdminInfoDao adminInfoDao();

    public abstract SecurityDesksDao securityDesksDao();

    public abstract SecurityPersonnelDao securityPersonnelDao();

    public abstract IntercomDao intercomDao();

    public abstract AmenityDao amenityDao();

    public abstract AmenityBookingDao amenityBookingDao();

    public abstract MembershipPaymentDao membershipPaymentDao();

    public abstract FinancesCreditDao financesCreditDao();

    public abstract FinancesDebitDao financesDebitDao();
}
