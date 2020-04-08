# Denizen Township Manager  

A complete android application that digitizes everyday tasks inside gated communities like townships and societies.
It has simple UI/UX that follows Material Design guidelines, and houses a lot of features:
  
Residents can
1. Pay maintenance amount
2. Raise and track complaints
3. Book amenities
4. Talk to others using intercom

Authorities can
1. Issue notices
2. Manage complaints
3. Track expenses
4. Generate financial audit
5. Maintain emergency info
6. Talk to others using intercom

Security can
1. Track visitors' entry and exit
2. Talk to others using intercom

### Technologies used  

Frontend
1. Java + XML
2. Room (a layer over the local SQLite database, used for caching)
3. Volley/Retrofit (for consuming backend's REST API)
4. AWS S3 API (for storing files)
5. PayTM Developer API (for payments)

Backend
1. Django (application to host the logic and communicate with database)
2. PostgreSQL database (main database to store all user data)
3. gunicorn (WSGI server to serve the Django application)
4. Beams (for sending push notifications)
5. SendGrid (for sending emails to users)
6. Heroku (hosting the entire backend application)
