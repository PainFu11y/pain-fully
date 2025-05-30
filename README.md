# pain-fully Backend

**pain-fully** is the backend application for an event management platform that supports multiple user roles (participant, organizer, moderator), authentication via email and Google OAuth2, reviews, email invitations, and two-factor authentication (2FA) via email.

## 🔧 Technologies

- Java 17  
- Spring Boot 3  
- Spring Data JPA (Hibernate)  
- Spring Security (JWT + OAuth2)  
- PostgreSQL  
- Flyway (database migrations)  
- JavaMailSender (email service)  
- Gradle  

## 🚀 Quick Start

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/eventhub-backend.git
   cd eventhub-backend
   ```
2. Configure the application.yml with your database, email, and JWT settings.
3. Run the application:
 ```
./gradlew bootRun
```

🧩 Features

Registration and login via email

Login with Google OAuth2

Email confirmation with 2FA codes

Role-based access: Participant, Organizer, Moderator

Event management (create, edit, delete)

Send HTML invitations to participants via email

Subscribe to events

Review and rating system for events

Upload and display images (Base64)

Event search and filtering by category (with pagination)


📝 MIT License


Copyright (c) 2025 Vahan Avetisyan

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.




