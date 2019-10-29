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
        SecurityPersonnel.class,
        SecurityDesks.class}, version = 17)
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
}
